package com.application.isyaraapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.FieldValue

@Entity(tableName = "history_items")
data class HistoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val originalText: String,
    val correctedText: String,
    val modelType: String,
    val timestamp: Long = System.currentTimeMillis(),
)

data class FirestoreHistoryItem(
    val userId: String = "",
    val originalText: String = "",
    val correctedText: String = "",
    val modelType: String = "",
    val timestamp: Any = FieldValue.serverTimestamp()
)