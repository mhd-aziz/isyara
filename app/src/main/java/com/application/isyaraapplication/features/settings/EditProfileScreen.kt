package com.application.isyaraapplication.features.settings

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.application.isyaraapplication.core.State
import com.application.isyaraapplication.R
import com.application.isyaraapplication.features.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val profileState by viewModel.profileState.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onPhotoUriChange(uri)
    }

    LaunchedEffect(saveState) {
        when (val state = saveState) {
            is State.Success -> {
                Toast.makeText(context, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                viewModel.resetSaveState()
                navController.popBackStack()
            }

            is State.Error -> {
                Toast.makeText(context, "Gagal: ${state.message}", Toast.LENGTH_LONG).show()
                viewModel.resetSaveState()
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Akun", fontWeight = FontWeight.Bold) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = profileState) {
                is State.Loading -> {
                    CircularProgressIndicator()
                }

                is State.Success -> {
                    val user = state.data
                    val username by viewModel.username.collectAsState()
                    val fullName by viewModel.fullName.collectAsState()
                    val phoneNumber by viewModel.phoneNumber.collectAsState()
                    val bio by viewModel.bio.collectAsState()
                    val newPhotoUri by viewModel.photoUri.collectAsState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 16.dp)
                        ) {
                            AsyncImage(
                                model = newPhotoUri ?: user.photoUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable {
                                        imagePickerLauncher.launch("image/*")
                                    },
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = R.drawable.ic_placeholder_image),
                                error = painterResource(id = R.drawable.ic_error_image)
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                                    .size(36.dp)
                                    .clickable {
                                        imagePickerLauncher.launch("image/*")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Foto",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        OutlinedTextField(
                            value = user.email ?: "Tidak ada email",
                            onValueChange = {},
                            label = { Text("Email") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = username,
                            onValueChange = viewModel::onUsernameChange,
                            label = { Text("Username") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = viewModel::onFullNameChange,
                            label = { Text("Nama Panjang") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = viewModel::onPhoneNumberChange,
                            label = { Text("Nomor Handphone") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = bio,
                            onValueChange = viewModel::onBioChange,
                            label = { Text("Bio") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.NoteAlt,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        )
                        Spacer(modifier = Modifier.height(32.dp))

                        val isSaving = saveState is State.Loading
                        Button(
                            onClick = { viewModel.saveProfile() },
                            enabled = !isSaving,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "SIMPAN PERUBAHAN",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }

                    }
                }

                is State.Error -> {
                    Text(text = "Gagal memuat profil: ${state.message}")
                }

                is State.Idle -> {}
            }
        }
    }
}