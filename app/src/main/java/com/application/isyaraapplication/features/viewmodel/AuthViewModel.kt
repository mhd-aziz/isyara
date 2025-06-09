package com.application.isyaraapplication.features.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.isyaraapplication.core.State
import com.application.isyaraapplication.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<State<Unit>>(State.Idle)
    val authState = _authState.asStateFlow()

    private val _user = MutableStateFlow<FirebaseUser?>(repository.currentUser)
    val user = _user.asStateFlow()

    init {
        repository.addAuthStateListener { firebaseAuth ->
            _user.value = firebaseAuth.currentUser
        }
    }

    override fun onCleared() {
        repository.removeAuthStateListener()
        super.onCleared()
    }

    fun registerUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = State.Error("Email dan password tidak boleh kosong.")
            return
        }

        viewModelScope.launch {
            _authState.value = State.Loading
            _authState.value = repository.registerUser(email, password)
        }
    }

    fun loginUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = State.Error("Email dan password tidak boleh kosong.")
            return
        }

        viewModelScope.launch {
            _authState.value = State.Loading
            _authState.value = repository.loginUser(email, password)
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = State.Loading
            _authState.value = repository.signInWithGoogle(idToken)
        }
    }

    fun sendPasswordResetEmail(email: String) {
        if (email.isBlank()) {
            _authState.value = State.Error("Silakan masukkan email Anda terlebih dahulu.")
            return
        }
        viewModelScope.launch {
            _authState.value = State.Loading
            _authState.value = repository.sendPasswordResetEmail(email)
        }
    }

    fun logout() {
        repository.logout()
    }
}