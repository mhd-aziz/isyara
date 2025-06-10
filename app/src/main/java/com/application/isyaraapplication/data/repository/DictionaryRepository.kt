package com.application.isyaraapplication.data.repository

import com.application.isyaraapplication.core.State
import com.application.isyaraapplication.data.model.DictionaryItem
import com.application.isyaraapplication.data.model.ItemType
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DictionaryRepository @Inject constructor(
    private val storage: FirebaseStorage
) {
    fun getSibiWordList(): Flow<State<List<DictionaryItem>>> = flow {
        emit(State.Loading)
        try {
            val listResult = storage.reference.child("sibi_kata").listAll().await()
            val items = listResult.items.map { item ->
                DictionaryItem(
                    name = item.name.substringBeforeLast('.'),
                    url = item.path,
                    type = ItemType.VIDEO
                )
            }
            emit(State.Success(items))
        } catch (e: Exception) {
            emit(State.Error(e.message ?: "Gagal memuat daftar kata SIBI"))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getDownloadUrl(path: String): String {
        return storage.reference.child(path).downloadUrl.await().toString()
    }

    fun getSibiAlfabet(): Flow<State<List<DictionaryItem>>> = flow {
        emit(State.Loading)
        try {
            val listResult = storage.reference.child("sibi_huruf").listAll().await()
            val items = listResult.items.map { item ->
                DictionaryItem(
                    name = item.name.substringBeforeLast('.'),
                    url = item.path,
                    type = ItemType.IMAGE
                )
            }
            emit(State.Success(items))
        } catch (e: Exception) {
            emit(State.Error(e.message ?: "Gagal memuat data alfabet SIBI"))
        }
    }.flowOn(Dispatchers.IO)
}