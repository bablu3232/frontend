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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import com.simats.drugssearch.ui.theme.drugssearchTheme

// Abnormal Results Screen Colors
private val PrimaryBlue = Color(0xFF3B82F6)
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextDarkColor = Color(0xFF1E293B)
private val TextGrayColor = Color(0xFF64748B)
private val CardBorderColor = Color(0xFFE2E8F0)
private val GreenColor = Color(0xFF22C55E)
private val RedColor = Color(0xFFEF4444)
private val RedBg = Color(0xFFFEE2E2)
private val OrangeColor = Color(0xFFF97316)
private val OrangeBg = Color(0xFFFFF7ED)
private val YellowColor = Color(0xFFEAB308)
private val YellowBg = Color(0xFFFEFCE8)
private val DisclaimerBg = Color(0xFFF1F5F9)

// Normal ranges with units for display
private val normalRangesWithUnits = mapOf(
    // Blood Count
    "Hemoglobin" to Triple(12.0 to 17.0, "g/dL", "12-17 g/dL"),
    "WBC" to Triple(4000.0 to 11000.0, "/µL", "4000-11000 /µL"),
    "RBC" to Triple(4.5 to 5.5, "M/µL", "4.5-5.5 M/µL"),
    "Platelets" to Triple(150000.0 to 450000.0, "/µL", "150000-450000 /µL"),
    "Hematocrit" to Triple(38.0 to 50.0, "%", "38-50%"),
    // Metabolic Panel
    "Blood Glucose" to Triple(70.0 to 99.0, "mg/dL", "70-99 mg/dL"),
    "Sodium" to Triple(135.0 to 145.0, "mEq/L", "135-145 mEq/L"),
    "Potassium" to Triple(3.5 to 5.0, "mEq/L", "3.5-5.0 mEq/L"),
    "Calcium" to Triple(8.6 to 10.2, "mg/dL", "8.6-10.2 mg/dL"),
    "Bicarbonate" to Triple(22.0 to 29.0, "mEq/L", "22-29 mEq/L"),
    // Lipid Profile
    "Total Cholesterol" to Triple(0.0 to 200.0, "mg/dL", "<200 mg/dL"),
    "HDL Cholesterol" to Triple(40.0 to 999.0, "mg/dL", ">40 mg/dL"),
    "LDL Cholesterol" to Triple(0.0 to 100.0, "mg/dL", "<100 mg/dL"),
    "Triglycerides" to Triple(0.0 to 150.0, "mg/dL", "<150 mg/dL"),
    "VLDL Cholesterol" to Triple(5.0 to 40.0, "mg/dL", "5-40 mg/dL"),
    "T-Chol/HDL Ratio" to Triple(3.3 to 5.0, "", "3.3-5.0"),
    "LDL/HDL Ratio" to Triple(1.0 to 3.6, "", "1.0-3.6"),
    // Kidney Function
    "Creatinine" to Triple(0.6 to 1.3, "mg/dL", "0.6-1.3 mg/dL"),
    "BUN" to Triple(7.0 to 20.0, "mg/dL", "7-20 mg/dL"),
    "eGFR" to Triple(90.0 to 999.0, "mL/min/1.73m²", "≥90 mL/min"),
    "Uric Acid" to Triple(2.4 to 7.0, "mg/dL", "2.4-7.0 mg/dL"),
    // Aliases
    "Glucose" to Triple(70.0 to 99.0, "mg/dL", "70-99 mg/dL")
)



private fun getUnit(name: String): String {
    return normalRangesWithUnits[name]?.second ?: ""
}

private fun getNormalRangeDisplay(name: String): String {
    return normalRangesWithUnits[name]?.third ?: ""
}

// Calculate severity based on how far the value is from normal range
private fun getSeverity(name: String, value: String): String {
    val numValue = value.toDoubleOrNull() ?: return "Mild"
    val rangeData = normalRangesWithUnits[name] ?: return "Mild"
    val range = rangeData.first
    
    val deviation = if (numValue < range.first) {
        (range.first - numValue) / range.first
    } else {
        (numValue - range.second) / range.second
    }
    
    return when {
        deviation > 0.3 -> "High"
        deviation > 0.15 -> "Moderate"
        else -> "Mild"
    }
}

private fun getSeverityColor(severity: String): Color {
    return when (severity) {
        "High" -> RedColor
        "Moderate" -> OrangeColor
        else -> YellowColor
    }
}

private fun getSeverityBgColor(severity: String): Color {
    return when (severity) {
        "High" -> RedBg
        "Moderate" -> OrangeBg
        else -> YellowBg
    }
}

@Composable
fun AbnormalResultsScreen(
    categoryName: String = "Blood Count",
    analysis: com.simats.drugssearch.network.OcrResponse? = null,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onParameterClick: (String) -> Unit = {},
    onViewRecommendationsClick: () -> Unit = {},
    onBackToAnalysisClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    // Filter to get only abnormal values from backend analysis
    val parameters = analysis?.parameters ?: emptyMap()
    val abnormalValues = parameters.filter { (_, details) ->
        details.status != "Normal"
    }
    val abnormalCount = abnormalValues.size

    Scaffold(
        bottomBar = {
            AbnormalResultsBottomNav(
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
            AbnormalResultsTopBar(
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

                // Red Warning Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = RedBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = RedColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Abnormal Values",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = RedColor
                            )
                            Text(
                                text = "$abnormalCount parameters outside normal range",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp
                                ),
                                color = RedColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = "$categoryName - Abnormal Results",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = TextDarkColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Abnormal Parameter Cards
                abnormalValues.forEach { (name, details) ->
                    val displayValue = details.value?.toString() ?: ""
                    val displayUnit = details.unit ?: getUnit(name)
                    val severity = getSeverity(name, displayValue)
                    AbnormalParameterCard(
                        parameterName = name,
                        yourValue = displayValue,
                        unit = displayUnit,
                        normalRange = getNormalRangeDisplay(name),
                        severity = severity,
                        onClick = { onParameterClick(name) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Next Steps Info Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderColor, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
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
                            contentDescription = null,
                            tint = TextGrayColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Next Steps",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                ),
                                color = TextDarkColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Consult with your healthcare provider about these results. They can provide personalized advice and treatment options.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                ),
                                color = TextGrayColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onViewRecommendationsClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenColor)
                    ) {
                        Text(
                            text = "View Recommendations",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            ),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }

                    OutlinedButton(
                        onClick = onBackToAnalysisClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder
                    ) {
                        Text(
                            text = "Back to Analysis",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            ),
                            color = TextDarkColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Medical Disclaimer
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DisclaimerBg),
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
                            contentDescription = null,
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
                                text = "This platform is designed for educational and informational purposes only. It is not a substitute for professional medical advice, diagnosis, or treatment.",
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
            }
        }
    }
}

@Composable
private fun AbnormalParameterCard(
    parameterName: String,
    yourValue: String,
    unit: String,
    normalRange: String,
    severity: String,
    onClick: () -> Unit = {}
) {
    val severityColor = getSeverityColor(severity)
    val severityBgColor = getSeverityBgColor(severity)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, severityColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = parameterName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = TextDarkColor
                )

                // Severity Badge
                Card(
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(containerColor = severityBgColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = severity,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        ),
                        color = severityColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Your Value:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = TextGrayColor
                )
                Text(
                    text = "$yourValue $unit",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    color = RedColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Normal Range:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = TextGrayColor
                )
                Text(
                    text = normalRange,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ),
                    color = TextDarkColor
                )
            }
        }
    }
}

@Composable
private fun AbnormalResultsTopBar(
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
                text = "DrugsSearch",
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
private fun AbnormalResultsBottomNav(
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
fun AbnormalResultsScreenPreview() {
    drugssearchTheme {
        AbnormalResultsScreen(
            categoryName = "Blood Count",
            analysis = null
        )
    }
}
