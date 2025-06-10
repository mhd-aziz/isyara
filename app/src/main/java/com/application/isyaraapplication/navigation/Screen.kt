package com.application.isyaraapplication.navigation

import android.util.Base64

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
    object Feedback : Screen("feedback_screen")
    object SIBI : Screen("sibi_screen")
    object BISINDO : Screen("bisindo_screen")
    object Guide : Screen("guide_screen")
    object SibiAlfabet : Screen("sibi_alfabet_screen")
    object SibiWord : Screen("sibi_word_screen")
    object BisindoAlfabet : Screen("bisindo_alfabet_screen")
    object BisindoWord : Screen("bisindo_word_screen")
    object VideoPlayer : Screen("video_player_screen/{videoUrl}") {
        fun createRoute(videoUrl: String): String {
            val encodedUrl = Base64.encodeToString(videoUrl.toByteArray(), Base64.URL_SAFE)
            return "video_player_screen/$encodedUrl"
        }
    }
}