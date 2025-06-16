package com.application.isyaraapplication.features.viewmodel

import android.content.Context
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.isyaraapplication.core.State
import com.application.isyaraapplication.data.repository.HistoryRepository
import com.application.isyaraapplication.data.repository.TranslateRepository
import com.application.isyaraapplication.features.translate.utils.BisindoInterpreterHelper
import com.application.isyaraapplication.features.translate.utils.HandLandmarkerHelper
import com.application.isyaraapplication.features.translate.utils.TranslatorUiState
import com.google.mediapipe.tasks.vision.core.RunningMode
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class BisindoViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val translateRepository: TranslateRepository,
    private val historyRepository: HistoryRepository
) : ViewModel(), HandLandmarkerHelper.LandmarkerListener, CameraViewModel {

    private val _uiState = MutableStateFlow(TranslatorUiState())
    override val uiState = _uiState.asStateFlow()
    private var handLandmarkerHelper: HandLandmarkerHelper? = null
    private var bisindoInterpreterHelper: BisindoInterpreterHelper? = null
    override val cameraExecutor: ExecutorService =
        Executors.newSingleThreadExecutor()
    private var spellCheckJob: Job? = null
    private var lastPredictedLetter: String = ""
    private var spaceJustAdded = AtomicBoolean(false)
    private var lastPredictionTimestamp: Long = 0L

    init {
        viewModelScope.launch(Dispatchers.IO) {
            handLandmarkerHelper = HandLandmarkerHelper(
                context = context,
                runningMode = RunningMode.LIVE_STREAM,
                handLandmarkerHelperListener = this@BisindoViewModel
            )
            bisindoInterpreterHelper = try {
                BisindoInterpreterHelper(context = context)
            } catch (e: Exception) {
                onError("Gagal memuat model interpreter: ${e.message}")
                null
            }
            _uiState.update { it.copy(isInitializing = false) }
        }
    }

    override fun detectLiveStream(
        imageProxy: ImageProxy,
        isFrontCamera: Boolean
    ) {
        handLandmarkerHelper?.detectLiveStream(imageProxy, isFrontCamera) ?: imageProxy.close()
    }

    override fun onResults(resultBundle: HandLandmarkerHelper.ResultBundle) {
        viewModelScope.launch(Dispatchers.Default) {
            val handLandmarkerResult = resultBundle.results.first()

            if (handLandmarkerResult.landmarks().isEmpty()) {
                if (spaceJustAdded.compareAndSet(false, true)) {
                    withContext(Dispatchers.Main) {
                        _uiState.update { currentState ->
                            currentState.copy(
                                result = null,
                                currentLetter = "",
                                fullPrediction = if (currentState.fullPrediction.isNotEmpty() && !currentState.fullPrediction.endsWith(
                                        " "
                                    )
                                ) {
                                    currentState.fullPrediction + " "
                                } else {
                                    currentState.fullPrediction
                                },
                                predictionConfidence = 0f
                            )
                        }
                    }
                    triggerSpellCheckWithDelay()
                }
                lastPredictedLetter = ""
                return@launch
            }

            spellCheckJob?.cancel()
            spaceJustAdded.set(false)
            bisindoInterpreterHelper?.let { interpreter ->
                val classificationResult = interpreter.classify(handLandmarkerResult)
                val currentPrediction = classificationResult.predictedLetter

                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            result = handLandmarkerResult,
                            currentLetter = currentPrediction,
                            predictionConfidence = classificationResult.confidence
                        )
                    }
                }

                val currentTime = System.currentTimeMillis()
                if (currentPrediction.isNotBlank() &&
                    currentPrediction != "..." &&
                    currentPrediction != "?" &&
                    currentPrediction != lastPredictedLetter &&
                    (currentTime - lastPredictionTimestamp > 1500)
                ) {
                    lastPredictedLetter = currentPrediction
                    lastPredictionTimestamp = currentTime
                    withContext(Dispatchers.Main) {
                        _uiState.update { it.copy(fullPrediction = it.fullPrediction + currentPrediction) }
                    }
                }
            }
        }
    }

    private fun triggerSpellCheckWithDelay() {
        if (!_uiState.value.isSpellCheckEnabled) return

        spellCheckJob?.cancel()
        spellCheckJob = viewModelScope.launch {
            delay(3000L)
            if (_uiState.value.fullPrediction.trim().isNotBlank()) {
                spellCheck()
            }
        }
    }

    private suspend fun spellCheck() {
        val originalText = _uiState.value.fullPrediction
        if (originalText.trim().isBlank()) return

        _uiState.update { it.copy(isSpellChecking = true) }
        val result = translateRepository.spellCheck(originalText)
        _uiState.update { it.copy(isSpellChecking = false) }

        if (result is State.Success) {
            val correctedText = result.data
            _uiState.update { it.copy(fullPrediction = correctedText) }

            viewModelScope.launch {
                historyRepository.addHistory(
                    originalText = originalText.trim(),
                    correctedText = correctedText,
                    modelType = "BISINDO"
                )
            }
        } else if (result is State.Error) {
            onError(result.message)
        }
    }

    fun flipCamera() {
        _uiState.update { it.copy(isFrontCamera = !it.isFrontCamera) }
    }

    fun toggleSpellCheck() {
        val isEnabled = !_uiState.value.isSpellCheckEnabled
        _uiState.update { it.copy(isSpellCheckEnabled = isEnabled) }
        if (isEnabled) {
            triggerSpellCheckWithDelay()
        } else {
            spellCheckJob?.cancel()
        }
    }

    fun onClearClicked() {
        spellCheckJob?.cancel()
        _uiState.update {
            it.copy(
                fullPrediction = "",
                currentLetter = "",
                isSpellChecking = false
            )
        }
        lastPredictedLetter = ""
    }

    fun onDeleteClicked() {
        spellCheckJob?.cancel()
        _uiState.update {
            it.copy(fullPrediction = it.fullPrediction.dropLast(1), isSpellChecking = false)
        }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onError(error: String, errorCode: Int) {
        _uiState.update { it.copy(error = error, isSpellChecking = false) }
    }

    override fun onCleared() {
        super.onCleared()
        spellCheckJob?.cancel()
        handLandmarkerHelper?.clearHandLandmarker()
        bisindoInterpreterHelper?.close()
        cameraExecutor.shutdown()
    }
}

