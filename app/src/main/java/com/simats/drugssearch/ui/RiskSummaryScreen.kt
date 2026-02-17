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
import com.simats.drugssearch.ui.theme.DrugsSearchTheme

// Risk Summary Screen Colors
private val PrimaryBlue = Color(0xFF3B82F6)
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextDarkColor = Color(0xFF1E293B)
private val TextGrayColor = Color(0xFF64748B)
private val CardBorderColor = Color(0xFFE2E8F0)
private val GreenColor = Color(0xFF22C55E)
private val GreenBg = Color(0xFFDCFCE7)
private val YellowColor = Color(0xFFEAB308)
private val YellowBg = Color(0xFFFEFCE8)
private val RedColor = Color(0xFFEF4444)
private val RedBg = Color(0xFFFEE2E2)
private val DisclaimerBg = Color(0xFFFFF7ED)

// Risk data class
data class RiskItem(
    val label: String,
    val level: String,
    val explanation: String,
    val recommendations: List<String>
)

// Normal ranges for risk calculation
private val normalRanges = mapOf(
    "Hemoglobin" to (12.0 to 17.5),
    "WBC" to (4.5 to 11.0),
    "Blood Glucose" to (70.0 to 99.0),
    "Sodium" to (135.0 to 145.0),
    "Total Cholesterol" to (0.0 to 200.0),
    "LDL Cholesterol" to (0.0 to 100.0),
    "Creatinine" to (0.6 to 1.3),
    "eGFR" to (90.0 to 999.0)
)

private fun isValueNormal(name: String, value: String): Boolean {
    val numValue = value.toDoubleOrNull() ?: return true
    val range = normalRanges[name] ?: return true
    return numValue >= range.first && numValue <= range.second
}

private fun calculateOverallRisk(values: Map<String, String>): String {
    val filteredValues = values.filter { it.value.isNotBlank() }
    if (filteredValues.isEmpty()) return "Low"
    
    val normalCount = filteredValues.count { (key, value) -> isValueNormal(key, value) }
    val percentage = (normalCount.toDouble() / filteredValues.size) * 100
    
    return when {
        percentage >= 80 -> "Low"
        percentage >= 50 -> "Moderate"
        else -> "High"
    }
}

private fun getRisksForCategory(
    categoryName: String,
    values: Map<String, String>
): List<RiskItem> {
    return when (categoryName) {
        "Blood Count" -> listOf(
            RiskItem(
                label = "Anemia Risk",
                level = if (isValueNormal("Hemoglobin", values["Hemoglobin"] ?: "")) "Low Risk" else "Moderate Risk",
                explanation = "Your hemoglobin levels are ${if (isValueNormal("Hemoglobin", values["Hemoglobin"] ?: "")) "within normal range. No immediate concern for anemia." else "outside normal range. This may indicate anemia."}",
                recommendations = listOf(
                    "Maintain balanced diet rich in iron",
                    "Regular check-ups",
                    "Stay hydrated"
                )
            ),
            RiskItem(
                label = "Infection Risk",
                level = if (isValueNormal("WBC", values["WBC"] ?: "")) "Low Risk" else "Moderate Risk",
                explanation = "Your white blood cell count is ${if (isValueNormal("WBC", values["WBC"] ?: "")) "within normal range. Your immune system is functioning well." else "outside normal range. Monitor for signs of infection."}",
                recommendations = listOf(
                    "Practice good hygiene",
                    "Adequate sleep and rest",
                    "Balanced nutrition"
                )
            )
        )
        "Metabolic Panel" -> listOf(
            RiskItem(
                label = "Diabetes Risk",
                level = if (isValueNormal("Blood Glucose", values["Blood Glucose"] ?: "")) "Low Risk" else "Moderate Risk",
                explanation = "Your blood glucose levels are ${if (isValueNormal("Blood Glucose", values["Blood Glucose"] ?: "")) "within normal range. Continue maintaining healthy habits." else "outside normal range. May indicate prediabetes or diabetes."}",
                recommendations = listOf(
                    "Monitor carbohydrate intake",
                    "Regular physical activity",
                    "Maintain healthy weight"
                )
            ),
            RiskItem(
                label = "Electrolyte Imbalance Risk",
                level = if (isValueNormal("Sodium", values["Sodium"] ?: "")) "Low Risk" else "Moderate Risk",
                explanation = "Your electrolyte levels are ${if (isValueNormal("Sodium", values["Sodium"] ?: "")) "balanced and within normal range." else "showing some imbalance. This needs monitoring."}",
                recommendations = listOf(
                    "Stay properly hydrated",
                    "Balanced salt intake",
                    "Monitor fluid intake"
                )
            )
        )
        "Lipid Profile" -> listOf(
            RiskItem(
                label = "Cardiovascular Risk",
                level = if (isValueNormal("LDL Cholesterol", values["LDL Cholesterol"] ?: "") && 
                           isValueNormal("Total Cholesterol", values["Total Cholesterol"] ?: "")) "Low Risk" else "Moderate Risk",
                explanation = "Your cholesterol levels ${if (isValueNormal("LDL Cholesterol", values["LDL Cholesterol"] ?: "")) "are within healthy range. Your heart health is good." else "need attention. Elevated cholesterol increases cardiovascular risk."}",
                recommendations = listOf(
                    "Heart-healthy diet (low saturated fat)",
                    "Regular cardiovascular exercise",
                    "Maintain healthy weight",
                    "Avoid smoking"
                )
            )
        )
        "Kidney Function" -> listOf(
            RiskItem(
                label = "Kidney Health Risk",
                level = if (isValueNormal("Creatinine", values["Creatinine"] ?: "") && 
                           isValueNormal("eGFR", values["eGFR"] ?: "")) "Low Risk" else "Moderate Risk",
                explanation = "Your kidney function markers ${if (isValueNormal("Creatinine", values["Creatinine"] ?: "")) "are normal. Your kidneys are working well." else "show some abnormality. Kidney function needs monitoring."}",
                recommendations = listOf(
                    "Stay well hydrated",
                    "Limit sodium intake",
                    "Monitor blood pressure",
                    "Avoid nephrotoxic medications"
                )
            )
        )
        else -> emptyList()
    }
}

@Composable
fun RiskSummaryScreen(
    categoryName: String = "Blood Count",
    values: Map<String, String> = emptyMap(),
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onBackToAnalysisClick: () -> Unit = {},
    onViewRecommendationsClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val overallRisk = calculateOverallRisk(values)
    val risks = getRisksForCategory(categoryName, values)
    
    val riskColor = when (overallRisk) {
        "Low" -> GreenColor
        "Moderate" -> YellowColor
        else -> RedColor
    }
    
    val riskBgColor = when (overallRisk) {
        "Low" -> GreenBg
        "Moderate" -> YellowBg
        else -> RedBg
    }

    Scaffold(
        bottomBar = {
            RiskSummaryBottomNav(
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
            RiskSummaryTopBar(
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

                // Title
                Text(
                    text = "Risk Summary",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = TextDarkColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle
                Text(
                    text = "$categoryName Analysis",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = TextGrayColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Overall Risk Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = riskBgColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (overallRisk == "Low") Icons.Default.Check else Icons.Default.Warning,
                                contentDescription = null,
                                tint = riskColor,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Overall Risk Level",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    ),
                                    color = riskColor
                                )
                                Text(
                                    text = "Based on your ${categoryName.lowercase()} results",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp
                                    ),
                                    color = riskColor.copy(alpha = 0.8f)
                                )
                            }
                        }
                        
                        // Risk Badge
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = riskColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = overallRisk,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Risk Breakdown Title
                Text(
                    text = "Risk Breakdown",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = TextDarkColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Risk Cards
                risks.forEach { risk ->
                    RiskCard(risk = risk)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Important Notice Card
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
                            tint = YellowColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Important",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                ),
                                color = TextDarkColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "This risk assessment is based on your test results and is for informational purposes only. Always consult with your healthcare provider for proper medical advice and treatment.",
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

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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

                    Button(
                        onClick = onViewRecommendationsClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text(
                            text = "View Recommendations",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            ),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Medical Disclaimer
                Text(
                    text = "This platform is designed for educational and informational purposes only. It is not a substitute for professional medical advice, diagnosis, or treatment.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    ),
                    color = TextGrayColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Footer Links
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Privacy Policy",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = PrimaryBlue,
                        modifier = Modifier.clickable { }
                    )
                    Text(
                        text = "  •  ",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextGrayColor
                    )
                    Text(
                        text = "Terms of Service",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = PrimaryBlue,
                        modifier = Modifier.clickable { }
                    )
                    Text(
                        text = "  •  ",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextGrayColor
                    )
                    Text(
                        text = "Contact Us",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
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
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
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
private fun RiskCard(risk: RiskItem) {
    val riskColor = when {
        risk.level.contains("Low") -> GreenColor
        risk.level.contains("Moderate") -> YellowColor
        else -> RedColor
    }
    
    val riskBgColor = when {
        risk.level.contains("Low") -> GreenBg
        risk.level.contains("Moderate") -> YellowBg
        else -> RedBg
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorderColor, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = risk.label,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = TextDarkColor
                )

                // Risk Level Badge
                Card(
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(containerColor = riskBgColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = risk.level,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        ),
                        color = riskColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = risk.explanation,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                ),
                color = TextGrayColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Recommendations:",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                ),
                color = TextDarkColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            risk.recommendations.forEach { recommendation ->
                Row(
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                        color = PrimaryBlue,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = recommendation,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        ),
                        color = TextGrayColor
                    )
                }
            }
        }
    }
}

@Composable
private fun RiskSummaryTopBar(
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
private fun RiskSummaryBottomNav(
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
fun RiskSummaryScreenPreview() {
    DrugsSearchTheme {
        RiskSummaryScreen(
            categoryName = "Blood Count",
            values = mapOf(
                "Hemoglobin" to "14.2",
                "WBC" to "7500"
            )
        )
    }
}
