package com.application.isyaraapplication.features.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.isyaraapplication.core.State
import com.application.isyaraapplication.features.viewmodel.FeedbackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    navController: NavController,
    viewModel: FeedbackViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val subject by viewModel.subject.collectAsState()
    val message by viewModel.message.collectAsState()
    val sendState by viewModel.sendState.collectAsState()

    LaunchedEffect(sendState) {
        when (val state = sendState) {
            is State.Success -> {
                Toast.makeText(context, "Terima kasih atas masukan Anda!", Toast.LENGTH_SHORT).show()
                viewModel.resetSendState()
                navController.popBackStack()
            }
            is State.Error -> {
                Toast.makeText(context, "Gagal mengirim: ${state.message}", Toast.LENGTH_LONG).show()
                viewModel.resetSendState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kirim Masukan", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Kami sangat menghargai masukan Anda untuk membuat aplikasi ini menjadi lebih baik.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = subject,
                onValueChange = viewModel::onSubjectChange,
                label = { Text("Subjek") },
                leadingIcon = { Icon(Icons.Default.Subject, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = message,
                onValueChange = viewModel::onMessageChange,
                label = { Text("Pesan Anda") },
                leadingIcon = { Icon(Icons.Default.Message, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))

            val isSending = sendState is State.Loading
            Button(
                onClick = { viewModel.sendFeedback() },
                enabled = !isSending,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "KIRIM MASUKAN",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}