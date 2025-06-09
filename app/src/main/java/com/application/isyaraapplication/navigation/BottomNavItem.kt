package com.application.isyaraapplication.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    object Dashboard : BottomNavItem("Dashboard", Icons.Default.Dashboard, "dashboard")
    object Dictionary : BottomNavItem("Dictionary", Icons.Default.MenuBook, "dictionary")
    object Translate : BottomNavItem("Translate", Icons.Default.Translate, "translate")
    object History : BottomNavItem("History", Icons.Default.History, "history")
    object Settings : BottomNavItem("Settings", Icons.Default.Settings, "settings")
}