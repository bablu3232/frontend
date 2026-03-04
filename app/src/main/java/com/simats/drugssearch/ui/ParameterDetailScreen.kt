package com.simats.drugssearch.ui

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
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
import com.simats.drugssearch.ui.theme.drugssearchTheme

// Parameter Detail Screen Colors
private val PrimaryBlue = Color(0xFF3B82F6)
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextDarkColor = Color(0xFF1E293B)
private val TextGrayColor = Color(0xFF64748B)
private val CardBorderColor = Color(0xFFE2E8F0)
private val GreenColor = Color(0xFF22C55E)
private val GreenBg = Color(0xFFDCFCE7)
private val RedColor = Color(0xFFEF4444)
private val RedBg = Color(0xFFFEE2E2)
private val OrangeColor = Color(0xFFF97316)
private val OrangeBg = Color(0xFFFFF7ED)
private val DisclaimerBg = Color(0xFFF1F5F9)

// Parameter information data class
data class ParameterInfo(
    val name: String,
    val unit: String,
    val normalRange: Pair<Double, Double>,
    val normalRangeDisplay: String,
    val whatThisMeans: String,
    val normalRecommendations: List<String>,
    val abnormalRecommendations: List<String>
)

// All parameter definitions
private val parameterInfoMap = mapOf(
    // Blood Count
    "Hemoglobin" to ParameterInfo(
        name = "Hemoglobin",
        unit = "g/dL",
        normalRange = 12.0 to 17.5,
        normalRangeDisplay = "12-17.5 g/dL",
        whatThisMeans = "Hemoglobin is the protein in red blood cells that carries oxygen throughout your body. Your level indicates your blood's oxygen-carrying capacity.",
        normalRecommendations = listOf(
            "Maintain a balanced diet rich in iron",
            "Stay hydrated",
            "Regular exercise supports healthy blood oxygen levels"
        ),
        abnormalRecommendations = listOf(
            "Consult with your healthcare provider",
            "Include iron-rich foods like spinach, red meat, and legumes",
            "Consider vitamin B12 and folate supplementation if advised",
            "Follow up with additional blood tests as recommended"
        )
    ),
    "WBC" to ParameterInfo(
        name = "WBC Count",
        unit = "/µL",
        normalRange = 4000.0 to 11000.0,
        normalRangeDisplay = "4,000-11,000 /µL",
        whatThisMeans = "White Blood Cells (WBC) are part of your immune system and help fight infections. Your WBC count indicates how well your body can defend against illness.",
        normalRecommendations = listOf(
            "Maintain good hygiene practices",
            "Get adequate sleep for immune health",
            "Eat a balanced diet with fruits and vegetables"
        ),
        abnormalRecommendations = listOf(
            "Consult with your healthcare provider promptly",
            "Watch for signs of infection like fever",
            "Avoid exposure to sick individuals",
            "Follow up with additional tests as recommended"
        )
    ),
    "RBC" to ParameterInfo(
        name = "RBC Count",
        unit = "M/µL",
        normalRange = 4.5 to 5.5,
        normalRangeDisplay = "4.5-5.5 M/µL",
        whatThisMeans = "Red Blood Cells (RBC) carry oxygen from your lungs to all parts of your body. The count reflects your blood's ability to transport oxygen efficiently.",
        normalRecommendations = listOf(
            "Stay hydrated to maintain blood volume",
            "Include iron and vitamin B12 in your diet",
            "Regular physical activity supports RBC production"
        ),
        abnormalRecommendations = listOf(
            "Consult with your healthcare provider",
            "Consider dietary changes based on medical advice",
            "Avoid smoking and excessive alcohol",
            "Follow up with additional testing if needed"
        )
    ),
    "Platelets" to ParameterInfo(
        name = "Platelet Count",
        unit = "/µL",
        normalRange = 150000.0 to 450000.0,
        normalRangeDisplay = "150,000-450,000 /µL",
        whatThisMeans = "Platelets help your blood clot to stop bleeding. Your platelet count indicates how effectively your body can form clots when needed.",
        normalRecommendations = listOf(
            "Avoid excessive alcohol consumption",
            "Include vitamin K rich foods in your diet",
            "Regular health checkups help monitor platelet levels"
        ),
        abnormalRecommendations = listOf(
            "Consult with your healthcare provider",
            "Report any unusual bruising or bleeding",
            "Avoid medications that affect platelet function",
            "Follow up with hematology consultation if advised"
        )
    ),
    "Hematocrit" to ParameterInfo(
        name = "Hematocrit",
        unit = "%",
        normalRange = 38.0 to 50.0,
        normalRangeDisplay = "38-50%",
        whatThisMeans = "Hematocrit measures the percentage of red blood cells in your blood. It reflects your blood's oxygen-carrying capacity and hydration status.",
        normalRecommendations = listOf(
            "Stay well hydrated",
            "Maintain a balanced diet with iron",
            "Regular exercise supports healthy blood levels"
        ),
        abnormalRecommendations = listOf(
            "Consult with your healthcare provider",
            "Increase fluid intake if dehydrated",
            "Monitor for symptoms like fatigue or dizziness",
            "Follow up with additional tests as needed"
        )
    ),
    // Metabolic Panel
    "Blood Glucose" to ParameterInfo(
        name = "Blood Glucose",
        unit = "mg/dL",
        normalRange = 70.0 to 99.0,
        normalRangeDisplay = "70-99 mg/dL",
        whatThisMeans = "Blood glucose is your body's main source of energy. This level indicates how well your body regulates blood sugar, which is important for diabetes prevention.",
        normalRecommendations = listOf(
            "Maintain a balanced diet with complex carbohydrates",
            "Regular physical activity helps regulate blood sugar",
            "Limit sugary foods and processed snacks"
        ),
        abnormalRecommendations = listOf(
            "Consult with your healthcare provider",
            "Monitor your blood sugar regularly",
            "Consider dietary modifications as advised",
            "Discuss diabetes screening with your doctor"
        )
    ),
    "Sodium" to ParameterInfo(
        name = "Sodium",
        unit = "mEq/L",
        normalRange = 135.0 to 145.0,
        normalRangeDisplay = "135-145 mEq/L",
        whatThisMeans = "Sodium is an essential electrolyte that helps regulate fluid balance, blood pressure, and nerve function in your body.",
        normalRecommendations = listOf(
            "Maintain moderate salt intake",
            "Stay well hydrated",
            "Eat a balanced diet with fruits and vegetables"
        ),
        abnormalRecommendations = listOf(
            "Consult with your healthcare provider",
            "Adjust salt intake based on medical advice",
            "Monitor fluid intake carefully",
            "Follow up with electrolyte testing"
        )
    ),
    "Potassium" to ParameterInfo(
        name = "Potassium",
        unit = "mEq/L",
        normalRange = 3.5 to 5.0,
        normalRangeDisplay = "3.5-5.0 mEq/L",
        whatThisMeans = "Potassium is vital for heart function, muscle contractions, and nerve signals. Proper levels ensure your heart beats regularly.",
        normalRecommendations = listOf(
            "Include potassium-rich foods like bananas and potatoes",
            "Maintain balanced electrolyte intake",
            "Stay hydrated"
        ),
        abnormalRecommendations = listOf(
            "Consult with your healthcare provider immediately",
            "Review medications that affect potassium levels",
            "Adjust dietary potassium based on advice",
            "Monitor heart rhythm if symptomatic"
        )
    ),
    "Calcium" to ParameterInfo(
        name = "Calcium",
        unit = "mg/dL",
        normalRange = 8.6 to 10.2,
        normalRangeDisplay = "8.6-10.2 mg/dL",
        whatThisMeans = "Calcium is essential for strong bones, muscle function, and nerve signaling. Your level reflects bone health and metabolic balance.",
        normalRecommendations = listOf(
            "Include dairy products or calcium-fortified foods",
            "Get adequate vitamin D from sunlight or supplements",
            "Regular weight-bearing exercise supports bone health"
        ),
        abnormalRecommendations = listOf(
            "Consult with your healthcare provider",
            "Consider bone density testing if advised",
            "Review vitamin D and calcium supplementation",
            "Follow up with parathyroid function testing"
        )
    ),
    "Bicarbonate" to ParameterInfo(
        name = "Bicarbonate",
        unit = "mEq/L",
        normalRange = 22.0 to 29.0,
        normalRangeDisplay = "22-29 mEq/L",
        whatThisMeans = "Bicarbonate helps maintain your body's acid-base balance (pH). It's crucial for proper organ function and metabolism.",
        normalRecommendations = listOf(
            "Maintain a balanced diet",
            "Stay well hydrated",
            "Regular health checkups help monitor levels"
        ),
        abnormalRecommendations = listOf(
            "Consult with your healthcare provider",
            "Discuss underlying conditions that may affect pH",
            "Review medications with your doctor",
            "Follow up with arterial blood gas testing if needed"
        )
    ),
    // Lipid Profile
    "Total Cholesterol" to ParameterInfo(
        name = "Total Cholesterol",
        unit = "mg/dL",
        normalRange = 0.0 to 200.0,
        normalRangeDisplay = "<200 mg/dL",
        whatThisMeans = "Total cholesterol measures all cholesterol in your blood. While cholesterol is needed for cell membranes, high levels increase heart disease risk.",
        normalRecommendations = listOf(
            "Maintain a heart-healthy diet low in saturated fats",
            "Regular cardiovascular exercise",
            "Include fiber-rich foods like oats and beans"
        ),
        abnormalRecommendations = listOf(
            "Consult with your healthcare provider",
            "Reduce saturated and trans fat intake",
            "Consider medication if lifestyle changes aren't enough",
            "Get regular lipid panel monitoring"
        )
    ),
    "HDL Cholesterol" to ParameterInfo(
        name = "HDL Cholesterol",
        unit = "mg/dL",
        normalRange = 40.0 to 999.0,
        normalRangeDisplay = ">40 mg/dL",
        whatThisMeans = "HDL is 'good' cholesterol that helps remove other forms of cholesterol from your bloodstream. Higher levels are protective against heart disease.",
        normalRecommendations = listOf(
            "Regular aerobic exercise increases HDL",
            "Include healthy fats like olive oil and nuts",
            "Avoid smoking to maintain HDL levels"
        ),
        abnormalRecommendations = listOf(
            "Consult with your healthcare provider",
            "Increase physical activity",
            "Consider omega-3 fatty acid supplementation",
            "Quit smoking if applicable"
        )
    ),
    "LDL Cholesterol" to ParameterInfo(
        name = "LDL Cholesterol",
        unit = "mg/dL",
        normalRange = 0.0 to 100.0,
        normalRangeDisplay = "<100 mg/dL",
        whatThisMeans = "LDL is 'bad' cholesterol that can build up in artery walls, increasing the risk of heart disease and stroke. Lower levels are healthier.",
        normalRecommendations = listOf(
            "Maintain a diet low in saturated fats",
            "Include soluble fiber from oats, beans, and fruits",
            "Regular exercise helps lower LDL"
        ),
        abnormalRecommendations = listOf(
            "Consult with your healthcare provider",
            "Adopt a heart-healthy diet immediately",
            "Consider statin therapy if recommended",
            "Get cardiovascular risk assessment"
        )
    ),
    "Triglycerides" to ParameterInfo(
        name = "Triglycerides",
        unit = "mg/dL",
        normalRange = 0.0 to 150.0,
        normalRangeDisplay = "<150 mg/dL",
        whatThisMeans = "Triglycerides are a type of fat in your blood. High levels can contribute to hardening of arteries and increase heart disease risk.",
        normalRecommendations = listOf(
            "Limit sugar and refined carbohydrates",
            "Include omega-3 fatty acids from fish",
            "Maintain a healthy weight"
        ),
        abnormalRecommendations = listOf(
            "Consult with your healthcare provider",
            "Reduce alcohol consumption",
            "Limit sugar intake significantly",
            "Consider medication if very high"
        )
    ),
    // Kidney Function
    "Creatinine" to ParameterInfo(
        name = "Creatinine",
        unit = "mg/dL",
        normalRange = 0.6 to 1.3,
        normalRangeDisplay = "0.6-1.3 mg/dL",
        whatThisMeans = "Creatinine is a waste product from muscle metabolism that kidneys filter out. Your level indicates how well your kidneys are functioning.",
        normalRecommendations = listOf(
            "Stay well hydrated",
            "Maintain healthy blood pressure",
            "Limit excessive protein intake"
        ),
        abnormalRecommendations = listOf(
            "Consult with your healthcare provider",
            "Increase water intake",
            "Review medications that may affect kidneys",
            "Consider nephrology consultation"
        )
    ),
    "BUN" to ParameterInfo(
        name = "BUN",
        unit = "mg/dL",
        normalRange = 7.0 to 20.0,
        normalRangeDisplay = "7-20 mg/dL",
        whatThisMeans = "Blood Urea Nitrogen (BUN) measures nitrogen waste in your blood. It reflects kidney function and protein metabolism.",
        normalRecommendations = listOf(
            "Stay well hydrated",
            "Maintain a balanced protein diet",
            "Regular health checkups monitor kidney function"
        ),
        abnormalRecommendations = listOf(
            "Consult with your healthcare provider",
            "Ensure adequate hydration",
            "Review protein intake with a dietitian",
            "Follow up with kidney function tests"
        )
    ),
    "eGFR" to ParameterInfo(
        name = "eGFR",
        unit = "mL/min",
        normalRange = 90.0 to 999.0,
        normalRangeDisplay = "≥90 mL/min",
        whatThisMeans = "Estimated Glomerular Filtration Rate (eGFR) measures how well your kidneys filter waste from blood. Higher values indicate better kidney function.",
        normalRecommendations = listOf(
            "Stay hydrated",
            "Control blood pressure and blood sugar",
            "Avoid excessive use of NSAIDs"
        ),
        abnormalRecommendations = listOf(
            "Consult with your healthcare provider",
            "Manage underlying conditions like diabetes",
            "Avoid nephrotoxic medications",
            "Consider nephrologist referral"
        )
    ),
    "Uric Acid" to ParameterInfo(
        name = "Uric Acid",
        unit = "mg/dL",
        normalRange = 2.4 to 7.0,
        normalRangeDisplay = "2.4-7.0 mg/dL",
        whatThisMeans = "Uric acid is a waste product from purine breakdown. High levels can lead to gout and kidney stones.",
        normalRecommendations = listOf(
            "Stay well hydrated",
            "Limit purine-rich foods like red meat and shellfish",
            "Maintain a healthy weight"
        ),
        abnormalRecommendations = listOf(
            "Consult with your healthcare provider",
            "Reduce alcohol consumption, especially beer",
            "Limit high-purine foods",
            "Discuss medication options if gout develops"
        )
    )
)

private fun isValueNormal(normalRange: Pair<Double, Double>, value: Double): Boolean {
    return value >= normalRange.first && value <= normalRange.second
}

private fun getValueStatus(normalRange: Pair<Double, Double>, value: Double): String {
    return if (isValueNormal(normalRange, value)) "Within Normal Range" else "Outside Normal Range"
}

private fun getSeverity(normalRange: Pair<Double, Double>, value: Double): String {
    if (isValueNormal(normalRange, value)) return "Normal"
    
    val deviation = if (value < normalRange.first) {
        (normalRange.first - value) / normalRange.first
    } else {
        (value - normalRange.second) / normalRange.second
    }
    
    return when {
        deviation > 0.3 -> "High"
        deviation > 0.15 -> "Moderate"
        else -> "Mild"
    }
}

@Composable
fun ParameterDetailScreen(
    categoryName: String = "Blood Count",
    parameterName: String = "Hemoglobin",
    parameterValue: String = "14.2",
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onBackToAnalysisClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val parameterInfo = parameterInfoMap[parameterName] ?: parameterInfoMap["Hemoglobin"]!!
    val numValue = parameterValue.toDoubleOrNull() ?: 0.0
    val isNormal = isValueNormal(parameterInfo.normalRange, numValue)
    val statusText = getValueStatus(parameterInfo.normalRange, numValue)
    val severity = getSeverity(parameterInfo.normalRange, numValue)
    val recommendations = if (isNormal) parameterInfo.normalRecommendations else parameterInfo.abnormalRecommendations
    
    val statusColor = when (severity) {
        "Normal" -> GreenColor
        "Mild" -> OrangeColor
        "Moderate" -> OrangeColor
        else -> RedColor
    }
    val statusBgColor = when (severity) {
        "Normal" -> GreenBg
        "Mild" -> OrangeBg
        "Moderate" -> OrangeBg
        else -> RedBg
    }

    Scaffold(
        bottomBar = {
            ParameterDetailBottomNav(
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
            ParameterDetailTopBar(
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

                // Category Breadcrumb
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = TextGrayColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Parameter Name Title
                Text(
                    text = parameterInfo.name,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = TextDarkColor
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Value Summary Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GreenColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "Your Value",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp
                                    ),
                                    color = TextGrayColor
                                )
                                Row(
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Text(
                                        text = parameterValue,
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 36.sp
                                        ),
                                        color = PrimaryBlue
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = parameterInfo.unit,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 16.sp
                                        ),
                                        color = TextGrayColor,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }
                            }
                            
                            // Status Icon
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(statusBgColor, RoundedCornerShape(24.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isNormal) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = statusColor,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Normal Range
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = TextGrayColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Normal Range: ",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp
                                ),
                                color = TextGrayColor
                            )
                            Text(
                                text = parameterInfo.normalRangeDisplay,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                ),
                                color = TextDarkColor
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Status Badge
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = statusBgColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = "Status: $statusText",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                ),
                                color = statusColor,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // What This Means Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "What This Means",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = TextDarkColor
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = parameterInfo.whatThisMeans + if (isNormal) " Your level is within the normal range, indicating healthy ${parameterInfo.name.lowercase()} levels." else " Your level is outside the normal range and may require attention.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = 22.sp
                            ),
                            color = TextGrayColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Recommendations Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Recommendations",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = TextDarkColor
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        recommendations.forEach { recommendation ->
                            Row(
                                modifier = Modifier.padding(vertical = 6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                        .size(6.dp)
                                        .background(PrimaryBlue, RoundedCornerShape(3.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = recommendation,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    ),
                                    color = TextGrayColor
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Back to Analysis Button
                Button(
                    onClick = onBackToAnalysisClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text(
                        text = "Back to Analysis Overview",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        color = Color.White
                    )
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

                Spacer(modifier = Modifier.height(16.dp))

            }
        }
    }
}

@Composable
private fun ParameterDetailTopBar(
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
                .clip(CircleShape)
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
private fun ParameterDetailBottomNav(
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
fun ParameterDetailScreenPreview() {
    drugssearchTheme {
        ParameterDetailScreen(
            categoryName = "Blood Count",
            parameterName = "Hemoglobin",
            parameterValue = "14.2"
        )
    }
}
