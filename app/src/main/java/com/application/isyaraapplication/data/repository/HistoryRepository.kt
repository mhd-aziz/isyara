package com.application.isyaraapplication.data.repository

import android.util.Log
import com.application.isyaraapplication.core.State
import com.application.isyaraapplication.data.local.HistoryDao
import com.application.isyaraapplication.data.model.FirestoreHistoryItem
import com.application.isyaraapplication.data.model.HistoryItem
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val historyDao: HistoryDao,
    private val firestore: FirebaseFirestore
) {
    private val historyCollection = firestore.collection("history")

    fun getHistory(): Flow<List<HistoryItem>> {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            return historyDao.getHistoryForUser("")
        }

        return callbackFlow {
            val query = historyCollection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)

            val listener = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("RealtimeHistory", "Listen failed.", error)
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val firestoreItems = snapshot.toObjects(FirestoreHistoryItem::class.java)
                    val localItems = firestoreItems.map { firestoreItem ->
                        val timestamp = (firestoreItem.timestamp as? Timestamp)?.toDate()?.time
                            ?: System.currentTimeMillis()
                        HistoryItem(
                            userId = firestoreItem.userId,
                            originalText = firestoreItem.originalText,
                            correctedText = firestoreItem.correctedText,
                            modelType = firestoreItem.modelType,
                            timestamp = timestamp
                        )
                    }

                    trySend(localItems)

                    CoroutineScope(Dispatchers.IO).launch {
                        historyDao.clearHistoryForUser(userId)
                        localItems.forEach { historyDao.insertHistory(it) }
                    }
                }
            }

            awaitClose {
                Log.d("RealtimeHistory", "Closing history listener.")
                listener.remove()
            }
        }
    }

    suspend fun addHistory(
        originalText: String,
        correctedText: String,
        modelType: String
    ): State<Unit> {
        val userId = auth.currentUser?.uid ?: return State.Error("Pengguna tidak ditemukan.")
        if (originalText.isBlank() || correctedText.isBlank()) {
            return State.Error("Teks tidak boleh kosong.")
        }
        return try {
            val firestoreItem = FirestoreHistoryItem(
                userId = userId,
                originalText = originalText,
                correctedText = correctedText,
                modelType = modelType
            )
            historyCollection.add(firestoreItem).await()

            State.Success(Unit)
        } catch (e: Exception) {
            State.Error(e.message ?: "Gagal menyimpan histori.")
        }
    }

    suspend fun clearHistory(): State<Unit> {
        val userId = auth.currentUser?.uid ?: return State.Error("Pengguna tidak ditemukan.")
        return try {
            val querySnapshot = historyCollection.whereEqualTo("userId", userId).get().await()
            val batch = firestore.batch()
            querySnapshot.documents.forEach { document ->
                batch.delete(document.reference)
            }
            batch.commit().await()

            State.Success(Unit)
        } catch (e: Exception) {
            State.Error(e.message ?: "Gagal menghapus histori.")
        }
    }
}