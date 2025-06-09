package com.application.isyaraapplication.features.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.isyaraapplication.core.State
import com.application.isyaraapplication.data.repository.FeedbackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val repository: FeedbackRepository
) : ViewModel() {

    private val _subject = MutableStateFlow("")
    val subject = _subject.asStateFlow()

    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()

    private val _sendState = MutableStateFlow<State<Unit>>(State.Idle)
    val sendState = _sendState.asStateFlow()

    fun onSubjectChange(newSubject: String) {
        _subject.value = newSubject
    }

    fun onMessageChange(newMessage: String) {
        _message.value = newMessage
    }

    fun sendFeedback() {
        viewModelScope.launch {
            if (_subject.value.isBlank() || _message.value.isBlank()) {
                _sendState.value = State.Error("Subjek dan pesan tidak boleh kosong.")
                return@launch
            }
            _sendState.value = State.Loading
            val result = repository.sendFeedback(_subject.value, _message.value)
            _sendState.value = result
        }
    }

    fun resetSendState() {
        _sendState.value = State.Idle
    }
}