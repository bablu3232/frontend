package com.simats.drugssearch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

// Drug data class
// Drug data class - Removed, using com.simats.drugssearch.network.Drug

@Composable
fun SearchResultsScreen(
    searchQuery: String = "",
    drugs: List<com.simats.drugssearch.network.Drug> = emptyList(),
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onDrugClick: (com.simats.drugssearch.network.Drug) -> Unit = {},
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

                // Title
                Text(
                    text = "Search Results",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = TextDarkColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Results Count
                Text(
                    text = "${drugs.size} medication${if (drugs.size != 1) "s" else ""} found",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = TextGrayColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Drug Cards
                drugs.forEach { drug ->
                    DrugResultCard(
                        drug = drug,
                        onClick = { onDrugClick(drug) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Medical Disclaimer
                MedicalDisclaimerCard()

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DrugResultCard(
    drug: com.simats.drugssearch.network.Drug,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Drug Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFE3F2FD), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Medication,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Drug Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = drug.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = TextDarkColor
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Condition Badge and Form Type
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BadgeBlueBg
                    ) {
                        Text(
                            text = drug.condition.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = BadgeBlueText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "Tablet", // Hardcoded for now as it's not in API
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        ),
                        color = TextGrayColor
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Dosages
                Text(
                    text = "Available: ${drug.dosages.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp
                    ),
                    color = TextGrayColor
                )
            }

            // Arrow Icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "View Details",
                tint = TextGrayColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SearchResultsScreenPreview() {
    DrugsSearchTheme {
        SearchResultsScreen(
            searchQuery = "metformin",
            drugs = listOf(
                com.simats.drugssearch.network.Drug(
                    name = "Metformin",
                    condition = "Diabetes",
                    dosages = listOf("500mg", "850mg", "1000mg"),
                    description = "Description",
                    sideEffects = "Side Effects",
                    warnings = "Warnings",
                    storage = "Storage"
                )
            )
        )
    }
}
