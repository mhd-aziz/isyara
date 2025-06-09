package com.application.isyaraapplication.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object Dashboard : Screen("dashboard_screen")
    object ForgotPassword : Screen("forgot_password_screen")
    object EditProfile : Screen("edit_profile_screen")
    object EditPassword : Screen("edit_password_screen")
    object Language : Screen("language_screen")
    object About : Screen("about_screen")
    object ChangeTheme : Screen("change_theme_screen")
}