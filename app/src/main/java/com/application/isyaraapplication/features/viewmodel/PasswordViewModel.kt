package com.application.isyaraapplication.features.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.isyaraapplication.core.State
import com.application.isyaraapplication.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _oldPassword = MutableStateFlow("")
    val oldPassword = _oldPassword.asStateFlow()

    private val _newPassword = MutableStateFlow("")
    val newPassword = _newPassword.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    private val _updateState = MutableStateFlow<State<Unit>>(State.Idle)
    val updateState = _updateState.asStateFlow()

    fun onOldPasswordChange(password: String) {
        _oldPassword.value = password
    }

    fun onNewPasswordChange(password: String) {
        _newPassword.value = password
    }

    fun onConfirmPasswordChange(password: String) {
        _confirmPassword.value = password
    }

    fun updatePassword() {
        viewModelScope.launch {
            if (_oldPassword.value.isBlank() || _newPassword.value.isBlank() || _confirmPassword.value.isBlank()) {
                _updateState.value = State.Error("Semua kolom harus diisi.")
                return@launch
            }
            if (_newPassword.value.length < 6) {
                _updateState.value = State.Error("Password baru minimal harus 6 karakter.")
                return@launch
            }
            if (_newPassword.value != _confirmPassword.value) {
                _updateState.value =
                    State.Error("Password baru dan konfirmasi password tidak cocok.")
                return@launch
            }

            _updateState.value = State.Loading
            val result = repository.updatePassword(_oldPassword.value, _newPassword.value)
            _updateState.value = result
        }
    }

    fun resetUpdateState() {
        _updateState.value = State.Idle
    }
}