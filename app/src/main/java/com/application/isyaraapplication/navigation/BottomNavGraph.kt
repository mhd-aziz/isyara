package com.application.isyaraapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.application.isyaraapplication.features.dashboard.DashboardScreen
import com.application.isyaraapplication.features.dictionary.DictionaryScreen
import com.application.isyaraapplication.features.history.HistoryScreen
import com.application.isyaraapplication.features.settings.*
import com.application.isyaraapplication.features.translate.TranslateScreen
import com.application.isyaraapplication.features.viewmodel.AuthViewModel

@Composable
fun BottomNavGraph(
    bottomNavController: NavHostController,
    appNavController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = bottomNavController,
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
            TranslateScreen(navController = appNavController)
        }
        composable(route = BottomNavItem.History.route) {
            HistoryScreen()
        }
        composable(route = BottomNavItem.Settings.route) {
            SettingsScreen(navController = appNavController, viewModel = hiltViewModel<AuthViewModel>())
        }
    }
}