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

    private val _bisindoAlfabetState = MutableStateFlow<State<List<DictionaryItem>>>(State.Idle)
    val bisindoAlfabetState = _bisindoAlfabetState.asStateFlow()

    private val _bisindoWordState = MutableStateFlow<State<List<DictionaryItem>>>(State.Idle)
    val bisindoWordState = _bisindoWordState.asStateFlow()

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

    fun loadBisindoAlfabet() {
        viewModelScope.launch {
            repository.getBisindoAlfabet().collect {
                _bisindoAlfabetState.value = it
            }
        }
    }

    fun loadBisindoWords() {
        viewModelScope.launch {
            repository.getBisindoWordList().collect {
                _bisindoWordState.value = it
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
        } catch (_: Exception) {
            null
        }
    }
}