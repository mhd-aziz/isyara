package com.application.isyaraapplication.features.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.isyaraapplication.core.State
import com.application.isyaraapplication.data.model.HistoryItem
import com.application.isyaraapplication.data.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _historyState = MutableStateFlow<State<List<HistoryItem>>>(State.Idle)
    val historyState = _historyState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getHistory()
    }

    private fun getHistory() {
        viewModelScope.launch {
            _historyState.value = State.Loading
            historyRepository.getHistory()
                .catch { e ->
                    _historyState.value = State.Error(e.message ?: "Gagal memuat histori")
                }
                .collect { historyList ->
                    _historyState.value = State.Success(historyList)
                }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            _historyState.update { if (it is State.Success) it.copy(isLoading = true) else State.Loading }

            when (val result = historyRepository.clearHistory()) {
                is State.Success -> {
                    _eventFlow.emit("Histori berhasil dihapus.")
                }
                is State.Error -> {
                    _eventFlow.emit(result.message)
                }
                else -> {}
            }
            _historyState.update { if (it is State.Success) it.copy(isLoading = false) else State.Idle }
        }
    }
}