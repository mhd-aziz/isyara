package com.application.isyaraapplication.features.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.isyaraapplication.features.viewmodel.ThemeViewModel

data class ThemeOption(val key: String, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeThemeScreen(
    navController: NavController,
    viewModel: ThemeViewModel = hiltViewModel()
) {
    val themes = listOf(
        ThemeOption("Light", "Terang"),
        ThemeOption("Dark", "Gelap"),
        ThemeOption("System", "Sesuai Sistem")
    )

    val currentThemeKey by viewModel.currentTheme.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ganti Tema", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            themes.forEach { theme ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setTheme(theme.key) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentThemeKey == theme.key,
                        onClick = { viewModel.setTheme(theme.key) }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = theme.name, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}