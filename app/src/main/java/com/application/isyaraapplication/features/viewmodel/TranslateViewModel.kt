package com.application.isyaraapplication.features.viewmodel

import android.content.Context
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.isyaraapplication.features.translate.utils.BisindoInterpreterHelper
import com.application.isyaraapplication.features.translate.utils.HandLandmarkerHelper
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class TranslateViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel(), HandLandmarkerHelper.LandmarkerListener {

    private val _uiState = MutableStateFlow(TranslatorUiState())
    val uiState = _uiState.asStateFlow()
    private var handLandmarkerHelper: HandLandmarkerHelper? = null
    private var bisindoInterpreterHelper: BisindoInterpreterHelper? = null
    private val isInitializing = AtomicBoolean(false)
    private var lastPredictedLetter: String = ""
    private var spaceJustAdded = AtomicBoolean(false)


    init {
        viewModelScope.launch {
            createHandLandmarker()
        }
    }

    private fun createHandLandmarker() {
        if (isInitializing.getAndSet(true)) return
        handLandmarkerHelper = HandLandmarkerHelper(
            context = context,
            runningMode = RunningMode.LIVE_STREAM,
            handLandmarkerHelperListener = this@TranslateViewModel
        )
        isInitializing.set(false)
    }

    fun setInterpreter() {
        viewModelScope.launch {
            try {
                if (bisindoInterpreterHelper == null) {
                    bisindoInterpreterHelper = BisindoInterpreterHelper(
                        context = context
                    )
                }
            } catch (e: Exception) {
                onError("Gagal memuat model interpreter: ${e.message}")
            }
        }
    }

    fun detectLiveStream(imageProxy: ImageProxy, isFrontCamera: Boolean) {
        if (isInitializing.get()) return
        handLandmarkerHelper?.detectLiveStream(imageProxy, isFrontCamera)
    }

    override fun onResults(resultBundle: HandLandmarkerHelper.ResultBundle) {
        val handLandmarkerResult = resultBundle.results.first()

        if (handLandmarkerResult.landmarks().isEmpty()) {
            if (spaceJustAdded.compareAndSet(false, true)) {
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
            lastPredictedLetter = ""
            return
        }

        spaceJustAdded.set(false)
        bisindoInterpreterHelper?.let { interpreter ->
            val classificationResult = interpreter.classify(handLandmarkerResult)

            val currentPrediction = classificationResult.predictedLetter
            _uiState.update {
                it.copy(
                    result = handLandmarkerResult,
                    currentLetter = currentPrediction,
                    predictionConfidence = classificationResult.confidence
                )
            }

            if (currentPrediction.isNotBlank() && currentPrediction != "..." && currentPrediction != "?" && currentPrediction != lastPredictedLetter) {
                lastPredictedLetter = currentPrediction
                _uiState.update { it.copy(fullPrediction = it.fullPrediction + currentPrediction) }
            }
        }
    }

    fun onClearClicked() {
        _uiState.update { it.copy(fullPrediction = "", currentLetter = "") }
        lastPredictedLetter = ""
    }

    fun onDeleteClicked() {
        _uiState.update {
            it.copy(fullPrediction = it.fullPrediction.dropLast(1))
        }
    }

    override fun onError(error: String, errorCode: Int) {
        _uiState.update { it.copy(error = error) }
    }

    override fun onCleared() {
        super.onCleared()
        handLandmarkerHelper?.clearHandLandmarker()
        bisindoInterpreterHelper?.close()
    }
}

data class TranslatorUiState(
    val result: HandLandmarkerResult? = null,
    val currentLetter: String = "",
    val fullPrediction: String = "",
    val predictionConfidence: Float = 0.5f,
    val error: String? = null
)