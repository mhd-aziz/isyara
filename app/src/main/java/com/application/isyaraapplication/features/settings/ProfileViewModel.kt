package com.application.isyaraapplication.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.isyaraapplication.core.State
import com.application.isyaraapplication.data.model.User
import com.application.isyaraapplication.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<State<User>>(State.Idle)
    val profileState = _profileState.asStateFlow()

    private val _saveState = MutableStateFlow<State<Unit>>(State.Idle)
    val saveState = _saveState.asStateFlow()

    val currentUser = profileRepository.loadUserProfile()

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _fullName = MutableStateFlow("")
    val fullName = _fullName.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()

    private val _bio = MutableStateFlow("")
    val bio = _bio.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            profileRepository.loadUserProfile().collect { state ->
                if (state is State.Success) {
                    val profile = state.data
                    _username.value = profile.username ?: ""
                    _fullName.value = profile.fullName
                    _phoneNumber.value = profile.phoneNumber
                    _bio.value = profile.bio
                }
                _profileState.value = state
            }
        }
    }

    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
    }

    fun onFullNameChange(newFullName: String) {
        _fullName.value = newFullName
    }

    fun onPhoneNumberChange(newPhoneNumber: String) {
        _phoneNumber.value = newPhoneNumber
    }

    fun onBioChange(newBio: String) {
        _bio.value = newBio
    }

    fun saveProfile() {
        viewModelScope.launch {
            _saveState.value = State.Loading
            val result = profileRepository.saveUserProfile(
                username = _username.value,
                fullName = _fullName.value,
                phoneNumber = _phoneNumber.value,
                bio = _bio.value
            )
            _saveState.value = result
        }
    }

    fun resetSaveState() {
        _saveState.value = State.Idle
    }
}