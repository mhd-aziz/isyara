package com.application.isyaraapplication.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.application.isyaraapplication.MainActivity
import com.application.isyaraapplication.features.MainScreen
import com.application.isyaraapplication.features.auth.AuthViewModel
import com.application.isyaraapplication.features.auth.ForgotPasswordScreen
import com.application.isyaraapplication.features.auth.LoginScreen
import com.application.isyaraapplication.features.auth.RegisterScreen
import dagger.hilt.android.EntryPointAccessors

@Composable
fun AppNavigation(
    viewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val currentUser by viewModel.user.collectAsState()

    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            }
        }
    }
    val startDestination = if (currentUser != null) Screen.Dashboard.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Login.route) {
            val context = LocalContext.current
            val hiltEntryPoint = EntryPointAccessors.fromActivity(
                context as Activity,
                MainActivity.ViewModelEntryPoint::class.java
            )
            val signInClient = hiltEntryPoint.getSignInClient()

            LoginScreen(
                navController = navController,
                signInClient = signInClient
            )
        }
        composable(route = Screen.Register.route) {
            val context = LocalContext.current
            val hiltEntryPoint = EntryPointAccessors.fromActivity(
                context as Activity,
                MainActivity.ViewModelEntryPoint::class.java
            )
            val signInClient = hiltEntryPoint.getSignInClient()

            RegisterScreen(
                navController = navController,
                signInClient = signInClient
            )
        }
        composable(route = Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }
        composable(route = Screen.Dashboard.route) {
            MainScreen()
        }
    }
}