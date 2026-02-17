package com.simats.drugssearch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.ui.theme.DrugsSearchTheme

// Colors
private val PrimaryBlue = Color(0xFF2196F3)
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextDarkColor = Color(0xFF1E293B)
private val TextGrayColor = Color(0xFF64748B)
private val CardBorderColor = Color(0xFFE2E8F0)
private val BadgeBlueBg = Color(0xFFE3F2FD)
private val BadgeBlueText = Color(0xFF1976D2)
private val WarningOrangeBg = Color(0xFFFFF3E0)
private val WarningOrangeText = Color(0xFFE65100)
private val IconBlueBg = Color(0xFFE3F2FD)

// Drug Details data class
data class DrugDetails(
    val name: String,
    val condition: String,
    val commonUses: String,
    val typicalDosage: String,
    val sideEffects: String,
    val precautions: String
)

@Composable
fun DrugDetailsScreen(
    drug: DrugDetails,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onNavigationHomeClick: () -> Unit = {},
    onUploadClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            DrugSearchBottomNav(
                selectedTab = "Search",
                onHomeClick = onNavigationHomeClick,
                onUploadClick = onUploadClick,
                onSearchClick = {},
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
            DrugSearchTopBar(
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

                // Drug Name
                Text(
                    text = drug.name,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    ),
                    color = TextDarkColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Condition Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BadgeBlueBg
                ) {
                    Text(
                        text = drug.condition.uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        color = BadgeBlueText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Information Cards
                InfoCard(
                    icon = Icons.Default.Info,
                    iconBg = IconBlueBg,
                    iconTint = PrimaryBlue,
                    title = "Common Uses",
                    content = drug.commonUses
                )

                Spacer(modifier = Modifier.height(16.dp))

                InfoCard(
                    icon = Icons.Default.Medication,
                    iconBg = IconBlueBg,
                    iconTint = PrimaryBlue,
                    title = "Typical Dosage",
                    content = drug.typicalDosage
                )

                Spacer(modifier = Modifier.height(16.dp))

                WarningInfoCard(
                    icon = Icons.Default.Warning,
                    title = "Side Effects",
                    content = drug.sideEffects
                )

                Spacer(modifier = Modifier.height(16.dp))

                InfoCard(
                    icon = Icons.Default.Shield,
                    iconBg = IconBlueBg,
                    iconTint = PrimaryBlue,
                    title = "Precautions",
                    content = drug.precautions
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Medical Disclaimer
                MedicalDisclaimerCard()

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun InfoCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    content: String
) {
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
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(iconBg, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = TextDarkColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                ),
                color = TextGrayColor
            )
        }
    }
}

@Composable
private fun WarningInfoCard(
    icon: ImageVector,
    title: String,
    content: String
) {
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
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(WarningOrangeBg, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = WarningOrangeText,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = TextDarkColor
                    )
                }

                // Important Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFFEBEE)
                ) {
                    Text(
                        text = "IMPORTANT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = Color(0xFFC62828),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                ),
                color = TextGrayColor
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DrugDetailsScreenPreview() {
    DrugsSearchTheme {
        DrugDetailsScreen(
            drug = DrugDetails(
                name = "Metformin",
                condition = "Diabetes",
                commonUses = "Used to treat type 2 diabetes. Helps control blood sugar levels and improves insulin sensitivity. May also be used for PCOS treatment.",
                typicalDosage = "Adults: 500mg-850mg twice daily with meals. Maximum dose: 2000-2550mg per day. Start with low dose and increase gradually.",
                sideEffects = "Common: Nausea, diarrhea, upset stomach. Rare but serious: Lactic acidosis, vitamin B12 deficiency. Contact doctor if severe symptoms occur.",
                precautions = "Do not use if you have kidney disease, liver problems, or heart failure. Avoid alcohol. Inform doctor about all medications. Monitor blood sugar regularly."
            )
        )
    }
}
