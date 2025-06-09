package com.application.isyaraapplication.features.translate.utils

import android.content.Context
import android.util.Log
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import kotlin.math.sqrt
import org.tensorflow.lite.Interpreter

class SibiInterpreterHelper(
    val context: Context,
    val modelPath: String = "model_sibi.tflite",
    val confidenceThreshold: Float = 0.5F
) {
    private var interpreter: Interpreter? = null
    private val alphabet = ('A'..'Z').map { it.toString() }

    init {
        setupInterpreter()
    }

    private fun setupInterpreter() {
        try {
            val assetManager = context.assets
            val modelFd = assetManager.openFd(modelPath)
            val inputStream = FileInputStream(modelFd.fileDescriptor)
            val modelBuffer =
                inputStream.channel.map(
                    FileChannel.MapMode.READ_ONLY,
                    modelFd.startOffset,
                    modelFd.declaredLength
                )
            val interpreterOptions = Interpreter.Options()
            interpreter = Interpreter(modelBuffer, interpreterOptions)
            Log.d(TAG, "Custom TFLite model SIBI ($modelPath) loaded successfully.")
        } catch (e: Exception) {
            throw IllegalStateException(
                "Error initializing TFLite SIBI interpreter: ${e.message}",
                e
            )
        }
    }

    fun classify(result: HandLandmarkerResult): ClassificationResult {
        var predictedLetter = "..."
        var predictionConfidence = 0f

        if (interpreter != null && result.landmarks().isNotEmpty()) {
            val features = extractAndNormalizeFeatures(result)
            val inputBuffer = ByteBuffer.allocateDirect(42 * 4).order(ByteOrder.nativeOrder())
            inputBuffer.asFloatBuffer().put(features)

            val outputArray = Array(1) { FloatArray(26) }
            interpreter?.run(inputBuffer, outputArray)

            val probabilities = outputArray[0]
            val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1

            if (maxIndex != -1) {
                predictionConfidence = probabilities[maxIndex]
                predictedLetter = if (predictionConfidence > confidenceThreshold) {
                    alphabet[maxIndex]
                } else {
                    "?"
                }
            }
        }
        return ClassificationResult(predictedLetter, predictionConfidence)
    }

    private fun extractAndNormalizeFeatures(result: HandLandmarkerResult): FloatArray {
        val numCoords = 21 * 2
        val landmarks = FloatArray(numCoords)

        if (result.landmarks().isNotEmpty()) {
            val firstHand = result.landmarks().first()
            val wristX = firstHand[0].x()
            val wristY = firstHand[0].y()

            val translatedLandmarks = mutableListOf<Float>()
            firstHand.forEach { landmark ->
                translatedLandmarks.add(landmark.x() - wristX)
                translatedLandmarks.add(landmark.y() - wristY)
            }

            var maxDist = 0f
            for (i in translatedLandmarks.indices step 2) {
                val x = translatedLandmarks[i]
                val y = translatedLandmarks[i + 1]
                val dist = sqrt(x * x + y * y)
                if (dist > maxDist) {
                    maxDist = dist
                }
            }

            if (maxDist < 1e-8) {
                return FloatArray(numCoords)
            }

            for (i in translatedLandmarks.indices) {
                landmarks[i] = translatedLandmarks[i] / maxDist
            }
        }

        return landmarks
    }

    fun close() {
        interpreter?.close()
        interpreter = null
    }

    data class ClassificationResult(val predictedLetter: String, val confidence: Float)

    companion object {
        private const val TAG = "SibiInterpreterHelper"
    }
}