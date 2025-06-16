package com.application.isyaraapplication.navigation

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.application.isyaraapplication.R

sealed class BottomNavItem(
    val title: String,
    val route: String,
    val imageVector: ImageVector? = null,
    @DrawableRes val drawableId: Int? = null
) {
    init {
        require(imageVector != null || drawableId != null) {
            "Icon must be either ImageVector or DrawableRes"
        }
    }

    object Dashboard : BottomNavItem(
        title = "Beranda",
        route = "dashboard",
        imageVector = Icons.Default.Dashboard
    )

    object Dictionary : BottomNavItem(
        title = "Kamus",
        route = "dictionary",
        imageVector = Icons.Default.MenuBook
    )

    object Translate : BottomNavItem(
        title = "Penerjemah",
        route = "translate",
        drawableId = R.drawable.logo_isyara_putih_main
    )

    object History : BottomNavItem(
        title = "Histori",
        route = "history",
        imageVector = Icons.Default.History
    )

    object Settings : BottomNavItem(
        title = "Pengaturan",
        route = "settings",
        imageVector = Icons.Default.Settings
    )
}