package com.simats.drugssearch.ui

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import com.simats.drugssearch.R

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.ui.theme.DrugsSearchTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Report Analysis Screen Colors
private val PrimaryBlue = Color(0xFF3B82F6)
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextDarkColor = Color(0xFF1E293B)
private val TextGrayColor = Color(0xFF64748B)
private val CardBorderColor = Color(0xFFE2E8F0)
private val DisclaimerBackground = Color(0xFFF1F5F9)
private val GreenColor = Color(0xFF22C55E)
private val GreenBg = Color(0xFFDCFCE7)
private val RedColor = Color(0xFFEF4444)
private val RedBg = Color(0xFFFEE2E2)

// Normal ranges for each category
private val normalRanges = mapOf(
    // Blood Count
    "Hemoglobin" to (12.0 to 17.5),
    "WBC" to (4.5 to 11.0),
    "RBC" to (4.5 to 5.5),
    "Platelets" to (150.0 to 400.0),
    "Hematocrit" to (36.0 to 50.0),
    // Metabolic Panel
    "Blood Glucose" to (70.0 to 99.0),
    "Sodium" to (135.0 to 145.0),
    "Potassium" to (3.5 to 5.0),
    "Calcium" to (8.6 to 10.2),
    "Bicarbonate" to (22.0 to 29.0),
    // Lipid Profile
    "Total Cholesterol" to (0.0 to 200.0),
    "HDL Cholesterol" to (40.0 to 999.0),
    "LDL Cholesterol" to (0.0 to 100.0),
    "Triglycerides" to (0.0 to 150.0),
    "VLDL Cholesterol" to (5.0 to 40.0),
    "T-Chol/HDL Ratio" to (3.3 to 5.0),
    "LDL/HDL Ratio" to (1.0 to 3.6),
    // Kidney Function
    "Creatinine" to (0.6 to 1.3),
    "BUN" to (7.0 to 20.0),
    "eGFR" to (90.0 to 999.0),
    "Uric Acid" to (2.4 to 7.0),
    // Aliases (same ranges under different names)
    "Glucose" to (70.0 to 99.0)
)

private fun isValueNormal(name: String, value: String): Boolean {
    val numValue = value.toDoubleOrNull() ?: return true
    val range = normalRanges[name] ?: return true
    return numValue >= range.first && numValue <= range.second
}

private fun calculateScore(normalCount: Int, totalCount: Int): Int {
    if (totalCount == 0) return 100
    return ((normalCount.toDouble() / totalCount) * 100).toInt()
}

private fun getScoreStatus(score: Int): String {
    return when {
        score >= 90 -> "Excellent"
        score >= 70 -> "Good – Minor attention needed"
        score >= 50 -> "Attention required"
        else -> "High risk"
    }
}

@Composable
fun ReportAnalysisScreen(
    categoryName: String = "Blood Count",
    values: Map<String, String> = emptyMap(),
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onViewNormalResultsClick: () -> Unit = {},
    onViewAbnormalResultsClick: () -> Unit = {},
    onViewDrugRecommendationsClick: () -> Unit = {},
    onViewRiskSummaryClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    // Calculate normal and abnormal counts
    val filteredValues = values.filter { it.value.isNotBlank() }
    val normalCount = filteredValues.count { (key, value) -> isValueNormal(key, value) }
    val abnormalCount = filteredValues.size - normalCount
    val totalCount = filteredValues.size
    
    // Calculate score
    val score = if (totalCount > 0) calculateScore(normalCount, totalCount) else 80
    val scoreStatus = getScoreStatus(score)
    
    // Get current date using SimpleDateFormat (API level 24 compatible)
    val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    val currentDate = dateFormat.format(Date())

    Scaffold(
        bottomBar = {
            ReportAnalysisBottomNav(
                onHomeClick = onHomeClick,
                onUploadClick = { },
                onSearchClick = onSearchClick,
                onHistoryClick = onHistoryClick,
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
            ReportAnalysisTopBar(
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

                // Title Section
                Text(
                    text = "Report Analysis",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = TextDarkColor
                )

                Text(
                    text = "$categoryName – $currentDate",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = TextGrayColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Score Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = categoryName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    ),
                                    color = Color.White
                                )
                                Text(
                                    text = "Based on entered parameters",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp
                                    ),
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "$score",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 48.sp
                                ),
                                color = Color.White
                            )
                            Text(
                                text = "/100",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 20.sp
                                ),
                                color = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = scoreStatus,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp
                            ),
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Normal Values Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp))
                            .clickable { onViewNormalResultsClick() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(GreenBg, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = GreenColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "$normalCount",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp
                                ),
                                color = TextDarkColor
                            )

                            Text(
                                text = "Normal Values",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 13.sp
                                ),
                                color = TextGrayColor
                            )
                        }
                    }

                    // Abnormal Values Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp))
                            .clickable { onViewAbnormalResultsClick() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(RedBg, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = RedColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "$abnormalCount",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp
                                ),
                                color = TextDarkColor
                            )

                            Text(
                                text = "Abnormal Values",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 13.sp
                                ),
                                color = TextGrayColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Test Category Section
                Text(
                    text = "Test Category",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = TextDarkColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Test Category Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderColor, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = categoryName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = TextDarkColor
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(GreenColor, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$normalCount Normal",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 13.sp
                                    ),
                                    color = TextGrayColor
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(RedColor, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$abnormalCount Abnormal",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 13.sp
                                    ),
                                    color = TextGrayColor
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Button(
                    onClick = onViewDrugRecommendationsClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenColor)
                ) {
                    Text(
                        text = "View Drug Recommendations",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onViewRiskSummaryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text(
                        text = "View Risk Summary",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Medical Disclaimer
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DisclaimerBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = TextGrayColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Medical Disclaimer",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                ),
                                color = TextDarkColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "This platform is designed for educational and informational purposes only. It is not a substitute for professional medical advice, diagnosis, or treatment. Always seek the advice of your physician or other qualified health provider with any questions you may have regarding a medical condition.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                ),
                                color = TextGrayColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Footer Links
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Privacy Policy",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = PrimaryBlue,
                        modifier = Modifier.clickable { }
                    )
                    Text(
                        text = "  •  ",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = TextGrayColor
                    )
                    Text(
                        text = "Terms of Service",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = PrimaryBlue,
                        modifier = Modifier.clickable { }
                    )
                    Text(
                        text = "  •  ",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = TextGrayColor
                    )
                    Text(
                        text = "Contact Us",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = PrimaryBlue,
                        modifier = Modifier.clickable { }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Copyright
                Text(
                    text = "© 2026 DrugSearch. All rights reserved.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = TextGrayColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ReportAnalysisTopBar(
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
private fun ReportAnalysisBottomNav(
    onHomeClick: () -> Unit,
    onUploadClick: () -> Unit,
    onSearchClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = false,
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                unselectedIconColor = TextDarkColor,
                                     unselectedTextColor = TextDarkColor,
                                     indicatorColor = Color.White
            )
        )
        NavigationBarItem(
            selected = true,
            onClick = onUploadClick,
            icon = { Icon(Icons.Default.Upload, "Upload") },
            label = { Text("Upload") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                unselectedIconColor = TextDarkColor,
                                     unselectedTextColor = TextDarkColor,
                                     indicatorColor = Color.White
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onSearchClick,
            icon = { Icon(Icons.Default.Search, "Search") },
            label = { Text("Search") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                unselectedIconColor = TextDarkColor,
                                     unselectedTextColor = TextDarkColor,
                                     indicatorColor = Color.White
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onHistoryClick,
            icon = { Icon(Icons.Default.History, "History") },
            label = { Text("History") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                unselectedIconColor = TextDarkColor,
                                     unselectedTextColor = TextDarkColor,
                                     indicatorColor = Color.White
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onProfileClick,
            icon = { Icon(Icons.Default.Person, "Profile") },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                unselectedIconColor = TextDarkColor,
                                     unselectedTextColor = TextDarkColor,
                                     indicatorColor = Color.White
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReportAnalysisScreenPreview() {
    DrugsSearchTheme {
        ReportAnalysisScreen()
    }
}
