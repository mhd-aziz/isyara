package com.application.isyaraapplication.features.translate.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panduan Penerjemah", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
        ) {
            GuideSection("1. Posisikan Tangan Anda", "Pastikan seluruh bagian tangan Anda, dari pergelangan hingga ujung jari, terlihat jelas di dalam frame kamera.")
            GuideSection("2. Pencahayaan yang Cukup", "Gunakan di tempat dengan pencahayaan yang baik agar kamera dapat mendeteksi gerakan tangan Anda dengan akurat.")
            GuideSection("3. Gerakan yang Jelas", "Lakukan gerakan isyarat satu per satu dengan jeda singkat. Untuk spasi, jauhkan tangan dari kamera sejenak atau sembunyikan tangan sementara agar tidak terbaca oleh kamera..")
            GuideSection("4. Koreksi Ejaan (Spell Check)", "Setelah beberapa huruf atau kata terdeteksi, Anda bisa menekan ikon spellcheck (centang) untuk memperbaiki ejaan dan tata bahasa menjadi kalimat yang lebih benar.")
            GuideSection("5. Kontrol Kamera dan Teks", "Gunakan ikon di pojok kanan atas untuk membalik kamera. Gunakan ikon hapus untuk menghapus huruf terakhir atau membersihkan semua teks.")
        }
    }
}

@Composable
private fun GuideSection(title: String, content: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = content,
        style = MaterialTheme.typography.bodyLarge
    )
}