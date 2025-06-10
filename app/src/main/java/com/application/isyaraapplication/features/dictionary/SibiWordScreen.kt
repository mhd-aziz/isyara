package com.application.isyaraapplication.features.dictionary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.application.isyaraapplication.R
import com.application.isyaraapplication.core.State
import com.application.isyaraapplication.data.model.DictionaryItem
import com.application.isyaraapplication.features.dictionary.utils.ShimmerLoadingList
import com.application.isyaraapplication.features.dictionary.utils.shimmerEffect
import com.application.isyaraapplication.features.viewmodel.DictionaryViewModel
import com.application.isyaraapplication.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SibiWordScreen(
    navController: NavController,
    viewModel: DictionaryViewModel = hiltViewModel()
) {
    val state by viewModel.sibiWordState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (state is State.Idle) {
            viewModel.loadSibiWords()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kamus SIBI - Kata", fontWeight = FontWeight.Bold) },
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
                label = { Text("Cari kata...") },
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
                        ShimmerLoadingList()
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
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(filteredList, key = { it.name }) { item ->
                                    WordVideoCard(
                                        item = item,
                                        viewModel = viewModel,
                                        navController = navController
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
}

@Composable
fun WordVideoCard(
    item: DictionaryItem,
    viewModel: DictionaryViewModel,
    navController: NavController
) {
    var videoUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(item.url) {
        videoUrl = viewModel.getUrlForPath(item.url)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clickable(enabled = videoUrl != null) {
                videoUrl?.let {
                    navController.navigate(Screen.VideoPlayer.createRoute(it))
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(videoUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Thumbnail untuk ${item.name}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = {
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .shimmerEffect()
                )
            },
            success = {
                Image(
                    painter = it.painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircleFilled,
                        contentDescription = "Putar Video",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    )
                }
            },
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_placeholder_image),
                        contentDescription = "Gagal memuat thumbnail",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        )
    }
}