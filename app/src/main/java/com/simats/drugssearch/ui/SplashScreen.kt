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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import kotlinx.coroutines.launch

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
        delay(2000) // Give the user time to see the logo
        // Final snappy scale up pop and fade out for seamless dashboard transition
        // We run these in parallel so the pulse and fade happen together
        val job1 = launch {
            scaleAnim.animateTo(
                targetValue = 1.3f, 
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            )
        }
        val job2 = launch {
            alphaAnim.animateTo(
                targetValue = 0f, 
                animationSpec = tween(durationMillis = 400, easing = LinearEasing)
            )
        }
        
        // Wait for both animations to finish
        job1.join()
        job2.join()
        
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Subtle Background Decorative Elements
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Faint Molecule-like dots/lines for a medical feel
            repeat(5) { index ->
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .offset(
                            x = (30 + index * 60).dp,
                            y = (50 + index * 100).dp
                        )
                        .alpha(0.03f)
                        .background(Color.LightGray, shape = RoundedCornerShape(50))
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value)
        ) {
            // Logo Container
            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo1),
                    contentDescription = "DrugsSearch Logo",
                    modifier = Modifier
                        .width(660.dp)
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(32.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            // Animated Loading dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
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
            color = Color(0xFF94A3B8),
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
                color = Color(0xFF2196F3), // PrimaryBlue
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
