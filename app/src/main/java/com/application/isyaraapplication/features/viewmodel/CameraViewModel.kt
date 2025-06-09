package com.application.isyaraapplication.features.viewmodel

import androidx.camera.core.ImageProxy
import com.application.isyaraapplication.features.translate.utils.TranslatorUiState
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.ExecutorService

interface CameraViewModel {
    val uiState: StateFlow<TranslatorUiState>
    val cameraExecutor: ExecutorService
    fun detectLiveStream(imageProxy: ImageProxy, isFrontCamera: Boolean)
}