package com.simats.drugssearch.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.R
import com.simats.drugssearch.ui.theme.DrugsSearchTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit = {}
) {
    // Fade-in and Scale animation for the logo and text
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.95f) }

    // Animation for the loading dots
    val infiniteTransition = rememberInfiniteTransition(label = "loading_dots")
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing, delayMillis = 200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing, delayMillis = 400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    // Splash duration and animation start
    LaunchedEffect(Unit) {
        alphaAnim.animateTo(1f, animationSpec = tween(1000))
        scaleAnim.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy))
        delay(2500)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A)),
        contentAlignment = Alignment.Center
    ) {
        // Soft Yellow Glow Gradient in the background
        Box(
            modifier = Modifier
                .size(450.dp)
                .offset(y = (-80).dp)
                .blur(100.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFB800).copy(alpha = 0.15f),
                            Color(0xFFFFB800).copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value)
        ) {
            // Refined Logo Container - Tightly wrapping the logo based on its dimensions
            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(horizontal = 32.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.splash_logo),
                    contentDescription = "DrugSearch Logo",
                    modifier = Modifier
                        .width(280.dp) // Fixed width to ensure prominence
                        .wrapContentHeight() // Height adapts to logo's aspect ratio
                        .padding(12.dp), // Snug but professional internal margin
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Name
            Text(
                text = "DrugSearch",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = Color.White
            )

            // Tagline
            Text(
                text = "Smart Drug Guidance from Medical Reports",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 48.dp)
            )

            Spacer(modifier = Modifier.height(56.dp))

            // Animated Loading dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LoadingDot(alpha = dot1Alpha)
                LoadingDot(alpha = dot2Alpha)
                LoadingDot(alpha = dot3Alpha)
            }
        }

        // Footer Text
        Text(
            text = "For informational use only",
            style = MaterialTheme.typography.labelSmall,
            color = Color.DarkGray,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        )
    }
}

@Composable
fun LoadingDot(alpha: Float) {
    Box(
        modifier = Modifier
            .size(7.dp)
            .alpha(alpha)
            .background(
                color = Color(0xFFFFB800),
                shape = RoundedCornerShape(50)
            )
    )
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    DrugsSearchTheme {
        SplashScreen()
    }
}
