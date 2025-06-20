@file:Suppress("DEPRECATION")

package com.application.isyaraapplication.features.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.isyaraapplication.R
import com.application.isyaraapplication.core.State
import com.application.isyaraapplication.features.viewmodel.AuthViewModel
import com.application.isyaraapplication.navigation.Screen
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
    signInClient: SignInClient
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val serverClientId = context.getString(R.string.default_web_client_id)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            coroutineScope.launch {
                try {
                    val credential = signInClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    if (idToken != null) {
                        viewModel.signInWithGoogle(idToken)
                    } else {
                        throw ApiException(
                            com.google.android.gms.common.api.Status(
                                CommonStatusCodes.INTERNAL_ERROR
                            )
                        )
                    }
                } catch (e: ApiException) {
                    Toast.makeText(
                        context,
                        "Daftar dengan Google gagal: ${e.statusCode}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    LaunchedEffect(authState) {
        when (val state = authState) {
            is State.Success -> {
                Toast.makeText(context, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show()
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }
            }

            is State.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }

            else -> Unit
        }
    }


    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.top_backgroung),
                    contentDescription = "Register Background",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
                Text(
                    text = "Daftar",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (authState is State.Loading) {
                    CircularProgressIndicator()
                } else {
                    Button(
                        onClick = { viewModel.registerUser(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("DAFTAR", style = MaterialTheme.typography.labelLarge)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                val googleIdTokenRequestOptions =
                                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                        .setSupported(true)
                                        .setServerClientId(serverClientId)
                                        .setFilterByAuthorizedAccounts(false)
                                        .build()

                                val autoSelectRequest = BeginSignInRequest.builder()
                                    .setGoogleIdTokenRequestOptions(googleIdTokenRequestOptions)
                                    .setAutoSelectEnabled(true)
                                    .build()

                                try {
                                    val signInIntentSender =
                                        signInClient.beginSignIn(autoSelectRequest).await()
                                    launcher.launch(
                                        IntentSenderRequest.Builder(
                                            signInIntentSender?.pendingIntent?.intentSender
                                                ?: throw ApiException(
                                                    com.google.android.gms.common.api.Status(
                                                        CommonStatusCodes.INTERNAL_ERROR
                                                    )
                                                )
                                        ).build()
                                    )
                                } catch (e: Exception) {
                                    if (e is ApiException) {
                                        val manualRequest = BeginSignInRequest.builder()
                                            .setGoogleIdTokenRequestOptions(
                                                googleIdTokenRequestOptions
                                            )
                                            .build()
                                        try {
                                            val signInIntentSender =
                                                signInClient.beginSignIn(manualRequest).await()
                                            launcher.launch(
                                                IntentSenderRequest.Builder(
                                                    signInIntentSender?.pendingIntent?.intentSender
                                                        ?: throw ApiException(
                                                            com.google.android.gms.common.api.Status(
                                                                CommonStatusCodes.INTERNAL_ERROR
                                                            )
                                                        )
                                                ).build()
                                            )
                                        } catch (e2: Exception) {
                                            Toast.makeText(
                                                context,
                                                "Gagal memulai daftar Google: ${e2.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Gagal memulai daftar Google: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.icon_google),
                                contentDescription = "Google Logo",
                                modifier = Modifier.size(24.dp),
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DAFTAR DENGAN GOOGLE",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Sudah punya akun?")
                    TextButton(onClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    }) {
                        Text("Login di sini")
                    }
                }
            }
        }
    }
}