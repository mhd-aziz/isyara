package com.application.isyaraapplication.features.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.isyaraapplication.data.local.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    fun setOnboardingCompleted() {
        viewModelScope.launch {
            repository.setOnboardingCompleted(true)
        }
    }
}