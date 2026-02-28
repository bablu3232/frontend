package com.simats.drugssearch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.ui.theme.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment

@Composable
fun TermsOfServiceScreen(
    onBackClick: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onBackClick,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header Bar with Close Icon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Terms of Service",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    )
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextDarkColor
                        )
                    }
                }

                Divider(color = LightGray)

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Last Updated: October 2026",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextGrayColor
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "1. Acceptance of Terms",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextDarkColor
                    )
                    Text(
                        text = "By creating an account and logging into the DrugsSearch platform, you agree to comply with and be bound by these Terms of Service. If you disagree with any part of the terms, you must not use our service.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDarkColor,
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                    )

                    Text(
                        text = "2. Medical Disclaimer (Not Medical Advice)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextDarkColor
                    )
                    Text(
                        text = "The DrugsSearch application is an informational tool only. Any OCR analysis, drug recommendations, or risk assessments provided by this application DO NOT CONSTITUTE PROFESSIONAL MEDICAL ADVICE. You should ALWAYS consult a qualified healthcare provider or doctor for medical diagnosis, advice, or treatment.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDarkColor,
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                    )

                    Text(
                        text = "3. User Accounts",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextDarkColor
                    )
                    Text(
                        text = "When you create an account, you must provide information that is accurate, complete, and current at all times. Failure to do so constitutes a breach of the Terms, which may result in immediate termination of your account.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDarkColor,
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                    )

                    Text(
                        text = "4. Accuracy of OCR Data",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextDarkColor
                    )
                    Text(
                        text = "We use third-party APIs (Gemini, OCRSpace) to extract text from your uploaded lab files. We cannot guarantee 100% accuracy. Users are solely responsible for manually verifying the extracted parameters against their original physical report before saving any analysis.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDarkColor,
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TermsOfServiceScreenPreview() {
    DrugsSearchTheme {
        TermsOfServiceScreen()
    }
}
