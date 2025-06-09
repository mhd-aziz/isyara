package com.application.isyaraapplication.data.model

import com.google.firebase.firestore.FieldValue

data class Feedback(
    val userId: String = "",
    val email: String? = "",
    val subject: String = "",
    val message: String = "",
    val timestamp: Any = FieldValue.serverTimestamp()
)