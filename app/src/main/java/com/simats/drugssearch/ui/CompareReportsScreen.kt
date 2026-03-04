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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.ui.theme.drugssearchTheme

// Colors matching DrugsSearch design system
private val PrimaryBlue = Color(0xFF2196F3)
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextDarkColor = Color(0xFF1E293B)
private val TextGrayColor = Color(0xFF64748B)
private val CardBorderColor = Color(0xFFE2E8F0)
private val GreenColor = Color(0xFF10B981)
private val RedColor = Color(0xFFEF4444)

@Composable
fun CompareReportsScreen(
    report1: HealthReport,
    report2: HealthReport,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
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
            CompareTopBar(onBackClick = onBackClick, onHomeClick = onHomeClick)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Compare Reports",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = TextDarkColor
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Comparison Logic (Simple table-like view)
                val allParamNames = (report1.parameters.map { it.name } + report2.parameters.map { it.name }).distinct()

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("Parameter", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, color = TextGrayColor)
                            Text(report1.date, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = TextGrayColor, fontSize = 12.sp)
                            Text(report2.date, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = TextGrayColor, fontSize = 12.sp)
                        }
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        allParamNames.forEach { paramName ->
                            val p1 = report1.parameters.find { it.name == paramName }
                            val p2 = report2.parameters.find { it.name == paramName }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(paramName, modifier = Modifier.weight(1.5f), fontSize = 14.sp)
                                
                                Text(
                                    text = p1?.let { "${it.value} ${it.unit}" } ?: "-",
                                    modifier = Modifier.weight(1f),
                                    fontSize = 14.sp,
                                    color = if (p1?.isNormal == false) RedColor else TextDarkColor
                                )
                                
                                Text(
                                    text = p2?.let { "${it.value} ${it.unit}" } ?: "-",
                                    modifier = Modifier.weight(1f),
                                    fontSize = 14.sp,
                                    color = if (p2?.isNormal == false) RedColor else TextDarkColor
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                MedicalDisclaimerCard()
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun CompareTopBar(onBackClick: () -> Unit, onHomeClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
        Text("DrugsSearch", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        IconButton(onClick = onHomeClick) {
            Icon(Icons.Default.Home, contentDescription = "Home", tint = TextGrayColor)
        }
    }
}
