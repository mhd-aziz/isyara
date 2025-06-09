package com.application.isyaraapplication.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.isyaraapplication.features.auth.AuthViewModel
import com.application.isyaraapplication.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by viewModel.user.collectAsState()
    val isEmailPasswordUser = currentUser?.providerData?.any { it.providerId == "password" } == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = currentUser?.displayName ?: "Username",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currentUser?.email ?: "email@example.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Text(
                text = "AKUN",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            SettingItem(
                icon = Icons.Default.Person,
                title = "Edit Akun",
                onClick = { navController.navigate(Screen.EditProfile.route) }
            )

            if (isEmailPasswordUser) {
                SettingItem(
                    icon = Icons.Default.Lock,
                    title = "Ganti Password",
                    onClick = { navController.navigate(Screen.EditPassword.route) }
                )
            }

            Text(
                text = "APLIKASI",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            SettingItem(
                icon = Icons.Default.Language,
                title = "Bahasa",
                onClick = { navController.navigate(Screen.Language.route) }
            )
            SettingItem(
                icon = Icons.Default.Info,
                title = "Tentang Aplikasi",
                onClick = { navController.navigate(Screen.About.route) }
            )

            Spacer(modifier = Modifier.weight(1f))

            SettingItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                title = "Logout",
                isLogout = true,
                onClick = { viewModel.logout() }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingItem(
    icon: ImageVector,
    title: String,
    isLogout: Boolean = false,
    onClick: () -> Unit
) {
    val titleColor =
        if (isLogout) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    val iconColor =
        if (isLogout) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = titleColor,
            modifier = Modifier.weight(1f)
        )
        if (!isLogout) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
    Divider(modifier = Modifier.padding(start = 60.dp))
}