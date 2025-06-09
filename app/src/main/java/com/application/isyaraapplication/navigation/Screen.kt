package com.application.isyaraapplication.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object Dashboard : Screen("dashboard_screen")
    object ForgotPassword : Screen("forgot_password_screen")
}