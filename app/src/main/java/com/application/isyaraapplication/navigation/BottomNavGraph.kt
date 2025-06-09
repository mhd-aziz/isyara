package com.application.isyaraapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.application.isyaraapplication.features.auth.AuthViewModel
import com.application.isyaraapplication.features.dashboard.DashboardScreen
import com.application.isyaraapplication.features.dictionary.DictionaryScreen
import com.application.isyaraapplication.features.history.HistoryScreen
import com.application.isyaraapplication.features.settings.SettingsScreen
import com.application.isyaraapplication.features.translate.TranslateScreen

@Composable
fun BottomNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Dashboard.route,
        modifier = modifier
    ) {
        composable(route = BottomNavItem.Dashboard.route) {
            DashboardScreen(viewModel = hiltViewModel<AuthViewModel>())
        }
        composable(route = BottomNavItem.Dictionary.route) {
            DictionaryScreen()
        }
        composable(route = BottomNavItem.Translate.route) {
            TranslateScreen()
        }
        composable(route = BottomNavItem.History.route) {
            HistoryScreen()
        }
        composable(route = BottomNavItem.Settings.route) {
            SettingsScreen(viewModel = hiltViewModel<AuthViewModel>())
        }
    }
}