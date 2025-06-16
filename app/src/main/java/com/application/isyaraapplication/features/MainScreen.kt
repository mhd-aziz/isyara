package com.application.isyaraapplication.features

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.application.isyaraapplication.navigation.BottomNavGraph
import com.application.isyaraapplication.navigation.BottomNavItem
import com.application.isyaraapplication.navigation.CradleShape

@Composable
fun MainScreen(appNavController: NavHostController) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val fabSize = 56.dp
            val bottomBarHeight = 62.dp

            val isTranslateSelected = currentDestination?.route == BottomNavItem.Translate.route
            val fabTextColor by animateColorAsState(
                targetValue = if (isTranslateSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.6f
                ),
                label = "FabTextColor"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bottomBarHeight)
            ) {
                BottomAppBar(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            shape = CradleShape(fabDiameter = fabSize, cornerRadius = 24.dp)
                            clip = true
                            shadowElevation = 22f
                        },
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                ) {
                    val items = listOf(
                        BottomNavItem.Dashboard,
                        BottomNavItem.Dictionary,
                        BottomNavItem.History,
                        BottomNavItem.Settings
                    )
                    BottomAppBarItem(
                        item = items[0],
                        currentDestination = currentDestination,
                        navController = bottomNavController
                    )
                    BottomAppBarItem(
                        item = items[1],
                        currentDestination = currentDestination,
                        navController = bottomNavController
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Penerjemah",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            color = fabTextColor,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                    BottomAppBarItem(
                        item = items[2],
                        currentDestination = currentDestination,
                        navController = bottomNavController
                    )
                    BottomAppBarItem(
                        item = items[3],
                        currentDestination = currentDestination,
                        navController = bottomNavController
                    )
                }

                FloatingActionButton(
                    onClick = {
                        if (currentDestination?.route != BottomNavItem.Translate.route) {
                            bottomNavController.navigate(BottomNavItem.Translate.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(fabSize)
                        .align(Alignment.TopCenter)
                        .offset(y = -(fabSize / 2))
                ) {
                    val item = BottomNavItem.Translate
                    if (item.drawableId != null) {
                        Icon(
                            painter = painterResource(id = item.drawableId),
                            contentDescription = item.title,
                            modifier = Modifier.size(36.dp)
                        )
                    } else if (item.imageVector != null) {
                        Icon(
                            imageVector = item.imageVector,
                            contentDescription = item.title,
                            modifier = Modifier.size(36.dp)
                        )
                    }
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

@Composable
private fun RowScope.BottomAppBarItem(
    item: BottomNavItem,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
            alpha = 0.6f
        ),
        label = "BottomItemColor"
    )
    val yOffset by animateDpAsState(
        targetValue = if (selected) (-4).dp else 0.dp,
        label = "BottomItemOffset"
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(CircleShape)
            .clickable(
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (item.drawableId != null) {
            Icon(
                painter = painterResource(id = item.drawableId),
                contentDescription = item.title,
                tint = contentColor,
                modifier = Modifier
                    .size(26.dp)
                    .offset(y = yOffset)
            )
        } else if (item.imageVector != null) {
            Icon(
                imageVector = item.imageVector,
                contentDescription = item.title,
                tint = contentColor,
                modifier = Modifier
                    .size(26.dp)
                    .offset(y = yOffset)
            )
        }
        Text(
            text = item.title,
            color = contentColor,
            fontSize = 10.sp,
            softWrap = false
        )
    }
}