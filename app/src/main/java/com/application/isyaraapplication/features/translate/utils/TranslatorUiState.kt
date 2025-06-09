package com.application.isyaraapplication.features.translate.utils

import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

data class TranslatorUiState(
    val result: HandLandmarkerResult? = null,
    val currentLetter: String = "",
    val fullPrediction: String = "",
    val predictionConfidence: Float = 0.5f,
    val error: String? = null,
    val isSpellChecking: Boolean = false,
    val isSpellCheckEnabled: Boolean = false,
    val isFrontCamera: Boolean = true,
    val isInitializing: Boolean = true
)