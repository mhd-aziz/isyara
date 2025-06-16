package com.application.isyaraapplication.features.history

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.isyaraapplication.core.State
import com.application.isyaraapplication.data.model.HistoryItem
import com.application.isyaraapplication.features.viewmodel.HistoryViewModel
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val historyState by viewModel.historyState.collectAsState()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Konfirmasi") },
            text = { Text("Apakah Anda yakin ingin menghapus semua histori?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearHistory()
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Histori Terjemahan", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    val historyList = (historyState as? State.Success)?.data
                    if (!historyList.isNullOrEmpty()) {
                        IconButton(onClick = { showDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus Histori")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = historyState) {
                is State.Idle -> {}
                is State.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is State.Success -> {
                    val historyList = state.data
                    if (historyList.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Belum ada histori.", style = MaterialTheme.typography.bodyLarge)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                historyList,
                                key = { "${it.timestamp}-${it.correctedText}" }) { item ->
                                HistoryCard(item = item)
                            }
                        }
                    }

                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }

                is State.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: ${state.message}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryCard(item: HistoryItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.correctedText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Asal: ${item.originalText}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.modelType,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(
                        Date(
                            item.timestamp
                        )
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}