package com.application.isyaraapplication.features.translate.utils

import android.content.Context
import android.util.Log
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import org.tensorflow.lite.Interpreter

class BisindoInterpreterHelper(
    val context: Context,
    val modelPath: String = "model_bisindo.tflite",
    val confidenceThreshold: Float = 0.75F
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
            Log.d(TAG, "Custom TFLite model ($modelPath) loaded successfully.")
        } catch (e: Exception) {
            throw IllegalStateException("Error initializing TFLite interpreter: ${e.message}", e)
        }
    }

    fun classify(result: HandLandmarkerResult): ClassificationResult {
        var predictedLetter = "..."
        var predictionConfidence = 0f

        if (interpreter != null && result.landmarks().isNotEmpty()) {
            val features = extractAndNormalizeFeatures(result)
            val inputBuffer = ByteBuffer.allocateDirect(126 * 4).order(ByteOrder.nativeOrder())
            inputBuffer.asFloatBuffer().put(features)

            val outputArray = Array(1) { FloatArray(26) }
            interpreter?.run(inputBuffer, outputArray)

            val probabilities = outputArray[0]
            val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1

            if (maxIndex != -1) {
                predictionConfidence = probabilities[maxIndex]
                if (predictionConfidence > confidenceThreshold) {
                    predictedLetter = alphabet[maxIndex]
                } else {
                    predictedLetter = "?"
                }
            }
        }
        return ClassificationResult(predictedLetter, predictionConfidence)
    }

    private fun extractAndNormalizeFeatures(result: HandLandmarkerResult): FloatArray {
        val numCoordsPerHand = 21 * 3
        val leftHandLandmarks = FloatArray(numCoordsPerHand)
        val rightHandLandmarks = FloatArray(numCoordsPerHand)

        if (result.landmarks().isNotEmpty()) {
            result.landmarks().forEachIndexed { handIndex, landmarks ->
                if (handIndex < result.handedness().size) {
                    val handedness = result.handedness()[handIndex][0].categoryName()
                    val tempLandmarks = FloatArray(numCoordsPerHand)
                    landmarks.forEachIndexed { landmarkIndex, landmark ->
                        tempLandmarks[landmarkIndex * 3] = landmark.x()
                        tempLandmarks[landmarkIndex * 3 + 1] = landmark.y()
                        tempLandmarks[landmarkIndex * 3 + 2] = landmark.z()
                    }
                    if (handedness == "Right") {
                        System.arraycopy(tempLandmarks, 0, leftHandLandmarks, 0, numCoordsPerHand)
                    } else if (handedness == "Left") {
                        System.arraycopy(tempLandmarks, 0, rightHandLandmarks, 0, numCoordsPerHand)
                    }
                }
            }
        }

        val wristLeft =
            floatArrayOf(leftHandLandmarks[0], leftHandLandmarks[1], leftHandLandmarks[2])
        if (wristLeft.any { it != 0f }) {
            for (i in 0 until 21) {
                leftHandLandmarks[i * 3] -= wristLeft[0]
                leftHandLandmarks[i * 3 + 1] -= wristLeft[1]
                leftHandLandmarks[i * 3 + 2] -= wristLeft[2]
            }
        }

        val wristRight =
            floatArrayOf(rightHandLandmarks[0], rightHandLandmarks[1], rightHandLandmarks[2])
        if (wristRight.any { it != 0f }) {
            for (i in 0 until 21) {
                rightHandLandmarks[i * 3] -= wristRight[0]
                rightHandLandmarks[i * 3 + 1] -= wristRight[1]
                rightHandLandmarks[i * 3 + 2] -= wristRight[2]
            }
        }

        return leftHandLandmarks + rightHandLandmarks
    }


    fun close() {
        interpreter?.close()
        interpreter = null
    }

    data class ClassificationResult(val predictedLetter: String, val confidence: Float)

    companion object {
        private const val TAG = "BisindoInterpreterHelper"
    }
}