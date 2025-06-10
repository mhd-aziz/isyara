package com.application.isyaraapplication.features.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.isyaraapplication.core.State
import com.application.isyaraapplication.data.model.DictionaryItem
import com.application.isyaraapplication.data.repository.DictionaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val repository: DictionaryRepository
) : ViewModel() {

    private val _sibiAlfabetState = MutableStateFlow<State<List<DictionaryItem>>>(State.Idle)
    val sibiAlfabetState = _sibiAlfabetState.asStateFlow()

    private val _sibiWordState = MutableStateFlow<State<List<DictionaryItem>>>(State.Idle)
    val sibiWordState = _sibiWordState.asStateFlow()

    private val urlCache = mutableMapOf<String, String>()

    fun loadSibiAlfabet() {
        viewModelScope.launch {
            repository.getSibiAlfabet().collect {
                _sibiAlfabetState.value = it
            }
        }
    }

    fun loadSibiWords() {
        viewModelScope.launch {
            repository.getSibiWordList().collect {
                _sibiWordState.value = it
            }
        }
    }

    suspend fun getUrlForPath(path: String): String? {
        if (urlCache.containsKey(path)) {
            return urlCache[path]
        }
        return try {
            val url = repository.getDownloadUrl(path)
            urlCache[path] = url
            url
        } catch (e: Exception) {
            null
        }
    }
}