package com.simats.drugssearch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.ui.theme.DrugsSearchTheme
import kotlinx.coroutines.delay

// Login Success Screen Colors
private val SuccessGreen = Color(0xFF22C55E)
private val BackgroundColor = Color(0xFFF0FDF4)
private val TextDarkColor = Color(0xFF1E293B)
private val TextGrayColor = Color(0xFF64748B)

@Composable
fun LoginSuccessScreen(
    onNavigateToDashboard: () -> Unit = {}
) {
    // Auto-navigate after 1 second
    LaunchedEffect(Unit) {
        delay(1000L)
        onNavigateToDashboard()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Success Icon Circle
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = SuccessGreen,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Inner circle with checkmark
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            color = Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Success",
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Success Title
            Text(
                text = "Login Successful!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                color = TextDarkColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle
            Text(
                text = "Redirecting to your dashboard...",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp
                ),
                color = TextGrayColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Loading Indicator
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = SuccessGreen,
                strokeWidth = 3.dp,
                trackColor = Color.Transparent
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginSuccessScreenPreview() {
    DrugsSearchTheme {
        LoginSuccessScreen()
    }
}
