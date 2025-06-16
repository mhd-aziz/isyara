package com.application.isyaraapplication.navigation

import android.app.Activity
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.application.isyaraapplication.MainActivity
import com.application.isyaraapplication.features.MainScreen
import com.application.isyaraapplication.features.auth.ForgotPasswordScreen
import com.application.isyaraapplication.features.auth.LoginScreen
import com.application.isyaraapplication.features.auth.RegisterScreen
import com.application.isyaraapplication.features.dictionary.BisindoAlfabetScreen
import com.application.isyaraapplication.features.dictionary.BisindoWordScreen
import com.application.isyaraapplication.features.dictionary.SibiAlfabetScreen
import com.application.isyaraapplication.features.dictionary.SibiWordScreen
import com.application.isyaraapplication.features.dictionary.VideoPlayerScreen
import com.application.isyaraapplication.features.onboarding.OnboardingScreen
import com.application.isyaraapplication.features.settings.AboutScreen
import com.application.isyaraapplication.features.settings.ChangeThemeScreen
import com.application.isyaraapplication.features.settings.EditPasswordScreen
import com.application.isyaraapplication.features.settings.EditProfileScreen
import com.application.isyaraapplication.features.settings.FeedbackScreen
import com.application.isyaraapplication.features.settings.LanguageScreen
import com.application.isyaraapplication.features.translate.BISINDOScreen
import com.application.isyaraapplication.features.translate.SIBIScreen
import com.application.isyaraapplication.features.translate.utils.GuideScreen
import com.application.isyaraapplication.features.viewmodel.AuthViewModel
import dagger.hilt.android.EntryPointAccessors
import java.nio.charset.StandardCharsets

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val authViewModel: AuthViewModel = hiltViewModel()
    val currentUser by authViewModel.user.collectAsState()
    val userPrefsRepository = EntryPointAccessors.fromActivity(
        context as Activity,
        MainActivity.ViewModelEntryPoint::class.java
    ).getUserPreferencesRepository()
    val onboardingCompleted by userPrefsRepository.onboardingCompletedFlow.collectAsState(initial = true)

    val startDestination = if (!onboardingCompleted) {
        Screen.Onboarding.route
    } else if (currentUser == null) {
        Screen.Login.route
    } else {
        Screen.Dashboard.route
    }

    LaunchedEffect(currentUser, onboardingCompleted) {
        if (onboardingCompleted && currentUser == null) {
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }
        composable(route = Screen.Login.route) {
            val hiltEntryPoint = EntryPointAccessors.fromActivity(
                context,
                MainActivity.ViewModelEntryPoint::class.java
            )
            LoginScreen(
                navController = navController,
                signInClient = hiltEntryPoint.getSignInClient()
            )
        }
        composable(route = Screen.Register.route) {
            val hiltEntryPoint = EntryPointAccessors.fromActivity(
                context,
                MainActivity.ViewModelEntryPoint::class.java
            )
            RegisterScreen(
                navController = navController,
                signInClient = hiltEntryPoint.getSignInClient()
            )
        }
        composable(route = Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }
        composable(route = Screen.Dashboard.route) {
            MainScreen(appNavController = navController)
        }
        composable(route = Screen.EditProfile.route) {
            EditProfileScreen(navController = navController)
        }
        composable(route = Screen.EditPassword.route) {
            EditPasswordScreen(navController = navController)
        }
        composable(route = Screen.Language.route) {
            LanguageScreen()
        }
        composable(route = Screen.ChangeTheme.route) {
            ChangeThemeScreen(navController = navController)
        }
        composable(route = Screen.Feedback.route) {
            FeedbackScreen(navController = navController)
        }
        composable(route = Screen.About.route) {
            AboutScreen(navController = navController)
        }
        composable(route = Screen.SIBI.route) {
            SIBIScreen(navController = navController)
        }
        composable(route = Screen.BISINDO.route) {
            BISINDOScreen(navController = navController)
        }
        composable(route = Screen.Guide.route) {
            GuideScreen(navController = navController)
        }
        composable(route = Screen.SibiAlfabet.route) {
            SibiAlfabetScreen(navController = navController)
        }
        composable(route = Screen.SibiWord.route) {
            SibiWordScreen(navController = navController)
        }
        composable(route = Screen.BisindoAlfabet.route) {
            BisindoAlfabetScreen(navController = navController)
        }
        composable(route = Screen.BisindoWord.route) {
            BisindoWordScreen(navController = navController)
        }

        composable(
            route = Screen.VideoPlayer.route,
            arguments = listOf(navArgument("videoUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("videoUrl") ?: ""
            val decodedUrl = remember(encodedUrl) {
                runCatching {
                    val decodedBytes = Base64.decode(encodedUrl, Base64.URL_SAFE)
                    String(decodedBytes, StandardCharsets.UTF_8)
                }.getOrNull()
            }
            if (decodedUrl != null) {
                VideoPlayerScreen(navController = navController, videoUrl = decodedUrl)
            } else {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, "URL Video tidak valid", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("URL video tidak valid, mengalihkan kembali...")
                }
            }
        }
    }
}