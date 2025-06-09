// app/src/main/java/com/application/isyaraapplication/features/MainScreen.kt
package com.application.isyaraapplication.features

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.application.isyaraapplication.navigation.BottomNavGraph
import com.application.isyaraapplication.navigation.BottomNavItem

@Composable
fun MainScreen(appNavController: NavHostController) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            val items = listOf(
                BottomNavItem.Dashboard,
                BottomNavItem.Dictionary,
                BottomNavItem.Translate,
                BottomNavItem.History,
                BottomNavItem.Settings,
            )
            val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            bottomNavController.navigate(screen.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        BottomNavGraph(
            bottomNavController = bottomNavController,
            appNavController = appNavController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}