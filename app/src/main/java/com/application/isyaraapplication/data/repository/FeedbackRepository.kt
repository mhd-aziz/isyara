package com.application.isyaraapplication.data.repository

import com.application.isyaraapplication.core.State
import com.application.isyaraapplication.data.model.Feedback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun sendFeedback(subject: String, message: String): State<Unit> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return State.Error("Pengguna tidak terautentikasi.")
            }
            val feedback = Feedback(
                userId = currentUser.uid,
                email = currentUser.email,
                subject = subject,
                message = message
            )

            firestore.collection("feedback").add(feedback).await()
            State.Success(Unit)
        } catch (e: Exception) {
            State.Error(e.localizedMessage ?: "Gagal mengirim masukan.")
        }
    }
}