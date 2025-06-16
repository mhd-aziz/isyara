package com.application.isyaraapplication.core

sealed class State<out T> {
    data object Idle : State<Nothing>()
    data object Loading : State<Nothing>()
    data class Success<out T>(val data: T, val isLoading: Boolean = false) : State<T>()
    data class Error(val message: String) : State<Nothing>()
}