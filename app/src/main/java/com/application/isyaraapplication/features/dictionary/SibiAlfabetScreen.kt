package com.application.isyaraapplication.features.dictionary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.isyaraapplication.R
import com.application.isyaraapplication.core.State
import com.application.isyaraapplication.data.model.DictionaryItem
import com.application.isyaraapplication.features.utils.shimmerEffect
import com.application.isyaraapplication.features.viewmodel.DictionaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SibiAlfabetScreen(
    navController: NavController,
    viewModel: DictionaryViewModel = hiltViewModel()
) {
    val state by viewModel.sibiAlfabetState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (state is State.Idle) {
            viewModel.loadSibiAlfabet()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kamus SIBI - Alfabet", fontWeight = FontWeight.Bold) },
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
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                label = { Text("Cari huruf...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari") },
                singleLine = true,
                shape = MaterialTheme.shapes.extraLarge
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (val result = state) {
                    is State.Loading, is State.Idle -> {
                        ShimmerLoadingGrid()
                    }

                    is State.Success -> {
                        val filteredList = remember(searchQuery, result.data) {
                            result.data.filter {
                                it.name.contains(searchQuery, ignoreCase = true)
                            }.sortedBy { it.name }
                        }
                        if (filteredList.isEmpty()) {
                            Text(
                                "Tidak ada hasil untuk \"$searchQuery\"",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 120.dp),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(filteredList, key = { it.name }) { item ->
                                    AlphabetCard(
                                        item = item,
                                        viewModel = viewModel,
                                        onClick = { imageUrl ->
                                            selectedImageUrl = imageUrl
                                        }
                                    )
                                }
                            }
                        }
                    }

                    is State.Error -> {
                        Text(text = "Gagal memuat data: ${result.message}")
                    }
                }
            }
        }
    }

    selectedImageUrl?.let { imageUrl ->
        EnlargedImageDialog(
            imageUrl = imageUrl,
            onDismiss = { selectedImageUrl = null }
        )
    }
}

@Composable
private fun AlphabetCard(
    item: DictionaryItem,
    viewModel: DictionaryViewModel,
    onClick: (String) -> Unit
) {
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var isLoadingUrl by remember { mutableStateOf(true) }

    LaunchedEffect(item.url) {
        isLoadingUrl = true
        imageUrl = viewModel.getUrlForPath(item.url)
        isLoadingUrl = false
    }

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(enabled = imageUrl != null) {
                imageUrl?.let { onClick(it) }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (isLoadingUrl) {
                    Spacer(modifier = Modifier
                        .fillMaxSize()
                        .shimmerEffect())
                } else if (imageUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.ic_placeholder_image),
                        error = painterResource(R.drawable.ic_error_image),
                        contentDescription = "Gambar isyarat untuk ${item.name}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_error_image),
                        contentDescription = "Gagal memuat gambar",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            Text(
                text = item.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}


@Composable
fun EnlargedImageDialog(imageUrl: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize(0.9f)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Gambar diperbesar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Tutup",
                    tint = Color.White,
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                )
            }
        }
    }
}

@Composable
fun ShimmerLoadingGrid() {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        userScrollEnabled = false
    ) {
        items(12) {
            Card(
                modifier = Modifier
                    .aspectRatio(1f)
                    .shimmerEffect()
            ) {}
        }
    }
}