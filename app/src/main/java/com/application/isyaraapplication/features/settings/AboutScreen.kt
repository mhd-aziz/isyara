package com.application.isyaraapplication.features.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.application.isyaraapplication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tentang Aplikasi", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Image(
                painter = painterResource(id = R.drawable.logo_isyara),
                contentDescription = "Logo Isyara",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Isyara",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Menjembatani Komunikasi Dengan Bahasa Isyarat",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))

            AboutSection(
                title = "Apa itu Isyara?",
                content = "Isyara adalah aplikasi penerjemah bahasa isyarat inovatif yang dirancang untuk memecahkan hambatan komunikasi antara komunitas Tuli dan teman dengar. Dengan teknologi kecerdasan buatan (AI) terdepan, kami mengubah cara kita berinteraksi."
            )

            AboutSection(
                title = "Fitur Utama",
                content = "• Terjemahan Real-time: Gunakan kamera perangkat Anda untuk menerjemahkan gerakan bahasa isyarat menjadi teks secara langsung, membuat percakapan lebih lancar dan natural.\n\n" +
                        "• Dukungan SIBI & BISINDO: Kami mendukung dua sistem bahasa isyarat utama di Indonesia: SIBI (Sistem Isyarat Bahasa Indonesia) yang lebih terstruktur dan BISINDO (Bahasa Isyarat Indonesia) yang tumbuh alami di dalam komunitas Tuli.\n\n" +
                        "• Kamus Isyarat: Pelajari isyarat baru kapan saja melalui kamus lengkap kami yang mudah diakses.\n\n" +
                        "• Berbasis AI: Ditenagai oleh model machine learning canggih untuk memastikan akurasi terjemahan yang terus meningkat."
            )

            AboutSection(
                title = "Misi Kami",
                content = "Misi kami adalah menciptakan dunia yang lebih inklusif dengan menyediakan alat komunikasi yang mudah diakses dan andal. Kami percaya bahwa setiap orang berhak untuk dipahami."
            )

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Versi 1.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun AboutSection(title: String, content: String) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Justify
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}