package com.application.isyaraapplication.features.translate

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.isyaraapplication.features.translate.utils.CameraScreen
import com.application.isyaraapplication.features.translate.utils.HandLandmarkerResultView
import com.application.isyaraapplication.features.viewmodel.TranslateViewModel

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BISINDOScreen(
    navController: NavController,
    viewModel: TranslateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onErrorShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Penerjemah BISINDO", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.flipCamera() }) {
                        Icon(
                            imageVector = Icons.Default.Cameraswitch,
                            contentDescription = "Balik Kamera",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CameraScreen(viewModel = viewModel)
            HandLandmarkerResultView(result = uiState.result)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = uiState.fullPrediction.ifEmpty { "Arahkan tangan ke kamera..." },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 72.dp)
                                .verticalScroll(rememberScrollState()),
                            style = MaterialTheme.typography.headlineMedium,
                            color = if (uiState.fullPrediction.isEmpty()) Color.Gray else MaterialTheme.colorScheme.onSurface
                        )
                        AnimatedVisibility(visible = uiState.isSpellChecking) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(start = 8.dp)
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.2f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Live: ${uiState.currentLetter} (${
                                    String.format(
                                        "%.0f",
                                        uiState.predictionConfidence * 100
                                    )
                                }%)",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        IconButton(onClick = { viewModel.toggleSpellCheck() }) {
                            Icon(
                                imageVector = Icons.Default.Spellcheck,
                                contentDescription = "Koreksi Ejaan",
                                tint = if (uiState.isSpellCheckEnabled) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }

                        IconButton(onClick = { viewModel.onDeleteClicked() }) {
                            Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Hapus")
                        }

                        IconButton(onClick = { viewModel.onClearClicked() }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Bersihkan Semua")
                        }
                    }
                }
            }
        }
    }
}