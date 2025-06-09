package com.application.isyaraapplication.features.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.isyaraapplication.R
import com.application.isyaraapplication.navigation.Screen
import kotlinx.coroutines.delay

private data class OnboardingPage(
    val imageRes: Int,
    val title: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pages = listOf(
        OnboardingPage(
            imageRes = R.drawable.onboarding_1,
            title = "Mulai dari awal dengan kamus",
            description = "Kamu juga bisa mulai belajar dari awal dengan kamus yang sudah disediakan secara gratis!"
        ),
        OnboardingPage(
            imageRes = R.drawable.onboarding_2,
            title = "Terjemahkan kalimat",
            description = "Kamu dapat menggunakan fitur terjemah untuk meningkatkan kemampuan kamu"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })

    LaunchedEffect(key1 = Unit) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(page = pages[page])
            }

            PagerIndicator(
                pagerState = pagerState,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            if (pagerState.currentPage == pages.lastIndex) {
                OnboardingActions(
                    onLoginClicked = {
                        viewModel.setOnboardingCompleted()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    },
                    onRegisterClicked = {
                        viewModel.setOnboardingCompleted()
                        navController.navigate(Screen.Register.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            } else {
                Spacer(modifier = Modifier.height(130.dp))
            }
        }
    }
}


@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = page.title,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(1f),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun PagerIndicator(pagerState: PagerState, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val color =
                if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else Color.LightGray
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .size(12.dp)
                    .background(color)
            )
        }
    }
}


@Suppress("DEPRECATION")
@Composable
private fun OnboardingActions(onLoginClicked: () -> Unit, onRegisterClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onLoginClicked,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Masuk", modifier = Modifier.padding(vertical = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        val annotatedText = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onSurface
                )
            ) {
                append("Belum punya akun? ")
            }

            pushStringAnnotation(tag = "REGISTER", annotation = "register")
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append("Daftar dulu")
            }
            pop()
        }

        ClickableText(
            text = annotatedText,
            style = MaterialTheme.typography.bodyLarge,
            onClick = { offset ->
                annotatedText.getStringAnnotations(tag = "REGISTER", start = offset, end = offset)
                    .firstOrNull()?.let {
                        onRegisterClicked()
                    }
            }
        )
    }
}