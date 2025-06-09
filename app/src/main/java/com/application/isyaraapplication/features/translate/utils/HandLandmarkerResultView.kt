package com.application.isyaraapplication.features.translate.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

@Composable
fun HandLandmarkerResultView(result: HandLandmarkerResult?) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        result?.let { handLandmarkerResult ->
            for (landmarks in handLandmarkerResult.landmarks()) {
                for (normalizedLandmark in landmarks) {
                    drawCircle(
                        color = Color.Red,
                        radius = 5f,
                        center = Offset(
                            normalizedLandmark.x() * size.width,
                            normalizedLandmark.y() * size.height
                        )
                    )
                }

                HandLandmarker.HAND_CONNECTIONS.forEach {
                    val start = landmarks[it.start()]
                    val end = landmarks[it.end()]
                    drawLine(
                        color = Color.White,
                        start = Offset(start.x() * size.width, start.y() * size.height),
                        end = Offset(end.x() * size.width, end.y() * size.height),
                        strokeWidth = 3f
                    )
                }
            }
        }
    }
}