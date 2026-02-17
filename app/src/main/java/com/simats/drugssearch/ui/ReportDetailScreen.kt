package com.simats.drugssearch.ui

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import com.simats.drugssearch.R

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.ui.theme.DrugsSearchTheme
import com.simats.drugssearch.network.ReportParameter

// Colors matching DrugSearch design system
private val PrimaryBlue = Color(0xFF2196F3)
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextDarkColor = Color(0xFF1E293B)
private val TextGrayColor = Color(0xFF64748B)
private val CardBorderColor = Color(0xFFE2E8F0)
private val GreenColor = Color(0xFF10B981)
private val RedColor = Color(0xFFEF4444)
private val LightGreenBg = Color(0xFFECFDF5)
private val LightRedBg = Color(0xFFFEF2F2)

@Composable
fun ReportDetailScreen(
    report: HealthReport,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onDownloadClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onNavigationHomeClick: () -> Unit = {},
    onUploadClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            DrugSearchBottomNav(
                selectedTab = "History",
                onHomeClick = onNavigationHomeClick,
                onUploadClick = onUploadClick,
                onSearchClick = onSearchClick,
                onHistoryClick = {},
                onProfileClick = onProfileClick
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)

        ) {
            // Top App Bar
            ReportDetailTopBar(
                onBackClick = onBackClick,
                onHomeClick = onHomeClick
            )

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${report.type} Report",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                            color = TextDarkColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = report.date,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = TextGrayColor
                        )
                    }
                    
                    Row {
                        IconButton(onClick = onDownloadClick) {
                            Icon(Icons.Default.Download, contentDescription = "Download", tint = PrimaryBlue)
                        }
                        IconButton(onClick = onShareClick) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = PrimaryBlue)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Status Card
                StatusSummaryCard(
                    isNormal = report.isNormal,
                    parameterCount = report.parameters.size,
                    abnormalCount = report.abnormalCount
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Test Results Card
                TestResultsCard(parameters = report.parameters)

                Spacer(modifier = Modifier.height(32.dp))

                // Medical Disclaimer
                MedicalDisclaimerCard()

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ReportDetailTopBar(
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextDarkColor
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // App Logo
        Image(
            painter = painterResource(id = R.drawable.logo_drugsearch),
            contentDescription = "Logo",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(4.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "DrugSearch",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = TextDarkColor
            )
            Text(
                text = "Your Medical Assistant",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = TextGrayColor
            )
        }

        IconButton(onClick = onHomeClick) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = TextGrayColor
            )
        }
    }
}

@Composable
private fun StatusSummaryCard(
    isNormal: Boolean,
    parameterCount: Int,
    abnormalCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isNormal) LightGreenBg else LightRedBg
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isNormal) GreenColor.copy(alpha = 0.15f) else RedColor.copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isNormal) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isNormal) GreenColor else RedColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = if (isNormal) "All Values Normal" else "$abnormalCount Abnormal Values",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = TextDarkColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isNormal) "$parameterCount parameters within range" else "$abnormalCount of $parameterCount parameters out of range",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = if (isNormal) GreenColor else RedColor
                )
            }
        }
    }
}

@Composable
private fun TestResultsCard(
    parameters: List<ReportParameter>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Text(
                text = "Test Results",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = TextDarkColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            parameters.forEachIndexed { index: Int, parameter: ReportParameter ->
                ParameterRow(parameter = parameter)
                if (index < parameters.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = CardBorderColor.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ParameterRow(
    parameter: ReportParameter
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = parameter.name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 15.sp
            ),
            color = TextDarkColor
        )
        Text(
            text = "${parameter.value} ${parameter.unit}",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            ),
            color = if (parameter.isNormal) GreenColor else RedColor
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReportDetailScreenPreview() {
    val sampleReport = HealthReport(
        id = "1",
        type = "Blood Count",
        date = "January 20, 2026",
        isNormal = true,
        parameters = listOf(
            ReportParameter("Hemoglobin", "14.2", "g/dL", true),
            ReportParameter("WBC", "7500", "/μL", true),
            ReportParameter("RBC", "4.8", "M/μL", true),
            ReportParameter("Platelets", "250000", "/μL", true)
        )
    )
    DrugsSearchTheme {
        ReportDetailScreen(report = sampleReport)
    }
}
