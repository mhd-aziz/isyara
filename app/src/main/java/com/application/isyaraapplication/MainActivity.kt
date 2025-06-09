package com.application.isyaraapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.application.isyaraapplication.data.local.UserPreferencesRepository
import com.application.isyaraapplication.navigation.AppNavigation
import com.application.isyaraapplication.ui.theme.IsyaraApplicationTheme
import com.google.android.gms.auth.api.identity.SignInClient
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(2000)
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            IsyaraApplicationTheme {
                AppNavigation()
            }
        }
    }

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface ViewModelEntryPoint {
        fun getSignInClient(): SignInClient
        fun getUserPreferencesRepository(): UserPreferencesRepository
    }
}