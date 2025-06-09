package com.application.isyaraapplication.features.translate.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

class HandLandmarkerHelper(
    var minHandDetectionConfidence: Float = DEFAULT_HAND_DETECTION_CONFIDENCE,
    var minHandTrackingConfidence: Float = DEFAULT_HAND_TRACKING_CONFIDENCE,
    var minHandPresenceConfidence: Float = DEFAULT_HAND_PRESENCE_CONFIDENCE,
    var maxNumHands: Int = DEFAULT_NUM_HANDS,
    var currentDelegate: Int = DELEGATE_CPU,
    var runningMode: RunningMode = RunningMode.LIVE_STREAM,
    val context: Context,
    val handLandmarkerHelperListener: LandmarkerListener? = null
) {

    private var handLandmarker: HandLandmarker? = null
    private var bitmapBuffer: Bitmap? = null
    private var rotationMatrix = Matrix()
    private var lastProcessingTimeMs: Long = 0

    init {
        setupHandLandmarker()
    }

    fun clearHandLandmarker() {
        handLandmarker?.close()
        handLandmarker = null
    }

    fun setupHandLandmarker() {
        val baseOptionBuilder = BaseOptions.builder()

        when (currentDelegate) {
            DELEGATE_CPU -> baseOptionBuilder.setDelegate(Delegate.CPU)
            DELEGATE_GPU -> baseOptionBuilder.setDelegate(Delegate.GPU)
        }

        baseOptionBuilder.setModelAssetPath(MP_HAND_LANDMARKER_TASK)

        if (runningMode == RunningMode.LIVE_STREAM && handLandmarkerHelperListener == null) {
            throw IllegalStateException("handLandmarkerHelperListener must be set when runningMode is LIVE_STREAM.")
        }

        try {
            val baseOptions = baseOptionBuilder.build()
            val optionsBuilder =
                HandLandmarker.HandLandmarkerOptions.builder()
                    .setBaseOptions(baseOptions)
                    .setMinHandDetectionConfidence(minHandDetectionConfidence)
                    .setMinTrackingConfidence(minHandTrackingConfidence)
                    .setMinHandPresenceConfidence(minHandPresenceConfidence)
                    .setNumHands(maxNumHands)
                    .setRunningMode(runningMode)

            if (runningMode == RunningMode.LIVE_STREAM) {
                optionsBuilder
                    .setResultListener(this::returnLivestreamResult)
                    .setErrorListener(this::returnLivestreamError)
            }

            val options = optionsBuilder.build()
            handLandmarker = HandLandmarker.createFromOptions(context, options)
        } catch (e: Exception) {
            val errorMessage = "Hand Landmarker failed to initialize. See error logs for details"
            handLandmarkerHelperListener?.onError(errorMessage)
            Log.e(TAG, "MediaPipe failed to load the task with error: ${e.message}")
        }
    }

    @SuppressLint("UseKtx")
    fun detectLiveStream(imageProxy: ImageProxy, isFrontCamera: Boolean) {
        if (runningMode != RunningMode.LIVE_STREAM || handLandmarker == null) {
            imageProxy.close()
            return
        }
        val currentTime = SystemClock.uptimeMillis()
        if (currentTime - lastProcessingTimeMs < FRAME_PROCESSING_INTERVAL_MS) {
            imageProxy.close()
            return
        }
        lastProcessingTimeMs = currentTime

        if (bitmapBuffer == null || bitmapBuffer!!.width != imageProxy.width || bitmapBuffer!!.height != imageProxy.height) {
            bitmapBuffer = Bitmap.createBitmap(
                imageProxy.width,
                imageProxy.height,
                Bitmap.Config.ARGB_8888
            )
        }

        bitmapBuffer?.copyPixelsFromBuffer(imageProxy.planes[0].buffer)
        val imageRotation = imageProxy.imageInfo.rotationDegrees
        imageProxy.close()

        rotationMatrix.reset()
        rotationMatrix.postRotate(imageRotation.toFloat())
        rotationMatrix.postScale(
            if (isFrontCamera) -1f else 1f,
            1f,
            imageProxy.width / 2f,
            imageProxy.height / 2f
        )

        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer!!, 0, 0, bitmapBuffer!!.width, bitmapBuffer!!.height, rotationMatrix, true
        )

        val mpImage = BitmapImageBuilder(rotatedBitmap).build()
        handLandmarker?.detectAsync(mpImage, currentTime)
    }

    private fun returnLivestreamResult(result: HandLandmarkerResult, input: MPImage) {
        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - result.timestampMs()

        handLandmarkerHelperListener?.onResults(
            ResultBundle(
                results = listOf(result),
                inferenceTime = inferenceTime,
                inputImageHeight = input.height,
                inputImageWidth = input.width
            )
        )
    }

    private fun returnLivestreamError(error: RuntimeException) {
        handLandmarkerHelperListener?.onError(error.message ?: "An unknown error has occurred")
    }

    companion object {
        const val TAG = "HandLandmarkerHelper"
        private const val MP_HAND_LANDMARKER_TASK = "hand_landmarker.task"
        private const val FRAME_PROCESSING_INTERVAL_MS = 100L
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DEFAULT_HAND_DETECTION_CONFIDENCE = 0.7F
        const val DEFAULT_HAND_TRACKING_CONFIDENCE = 0.5F
        const val DEFAULT_HAND_PRESENCE_CONFIDENCE = 0.5F
        const val DEFAULT_NUM_HANDS = 2
        const val OTHER_ERROR = 0
        const val GPU_ERROR = 1
    }

    data class ResultBundle(
        val results: List<HandLandmarkerResult>,
        val inferenceTime: Long,
        val inputImageHeight: Int,
        val inputImageWidth: Int
    )

    interface LandmarkerListener {
        fun onError(error: String, errorCode: Int = OTHER_ERROR)
        fun onResults(resultBundle: ResultBundle)
    }
}