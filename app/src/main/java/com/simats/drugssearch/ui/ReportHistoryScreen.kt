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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.ui.theme.DrugsSearchTheme

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

// Data class for Report
// Data class for Report (UI Model)
data class HealthReport(
    val id: String,
    val type: String,
    val date: String,
    val uploadedAt: String, // Store formatted time or raw string
    val isNormal: Boolean,
    val abnormalCount: Int = 0,
    val parameters: List<com.simats.drugssearch.network.ReportParameter> = emptyList(),
    val patientName: String? = null,
    val patientAge: Int? = null,
    val patientGender: String? = null
)

// ReportParameter is now in Models.kt

// Sort Option Enum
enum class SortOption {
    NEWEST, OLDEST, ABNORMAL_FIRST
}

@Composable
fun ReportHistoryScreen(
    userId: Int, // Added userId
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onReportClick: (HealthReport) -> Unit = {},
    onNavigationHomeClick: () -> Unit = {},
    onUploadClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var reports by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<List<HealthReport>>(emptyList()) }
    var isLoading by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) }
    var sortOption by remember { mutableStateOf(SortOption.NEWEST) }

    LaunchedEffect(userId) {
        if (userId != 0) {
            try {
                val response = com.simats.drugssearch.network.RetrofitClient.instance.getUserReports(userId)
                if (response.isSuccessful) {
                    val userReports = response.body() ?: emptyList()
                    // Map UserReport (API) to HealthReport (UI)
                    reports = userReports.map { apiReport ->
                        // Format the timestamp if needed
                        HealthReport(
                            id = apiReport.id,
                            type = apiReport.category,
                            date = apiReport.date,
                            uploadedAt = apiReport.uploadedAt ?: "",
                            isNormal = apiReport.isNormal,
                            abnormalCount = apiReport.abnormalCount,
                            parameters = apiReport.parameters, // ReportParameter is shared/compatible
                            patientName = apiReport.patientName,
                            patientAge = apiReport.patientAge,
                            patientGender = apiReport.patientGender
                        )
                    }
                } else {
                     android.widget.Toast.makeText(context, "Failed to load reports", android.widget.Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                 android.widget.Toast.makeText(context, "Error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    val totalReports = reports.size
    val normalReports = reports.count { it.isNormal }
    val abnormalReports = reports.count { !it.isNormal }

    // Sort Logic
    val sortedReports = remember(reports, sortOption) {
        when (sortOption) {
            SortOption.NEWEST -> reports.sortedByDescending { it.id.toIntOrNull() ?: 0 } // Assuming ID correlates with time or parse date if available
            SortOption.OLDEST -> reports.sortedBy { it.id.toIntOrNull() ?: 0 }
            SortOption.ABNORMAL_FIRST -> reports.sortedBy { it.isNormal } // False (Abnormal) comes before True (Normal)
        }
    }

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
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)

            ) {
                // Top App Bar
                item {
                    ReportHistoryTopBar(
                        onBackClick = onBackClick,
                        onHomeClick = onHomeClick
                    )
                }

                // Title Section
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Report History",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                ),
                                color = TextDarkColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "View your past reports",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                color = TextGrayColor
                            )
                        }

                        // Sort Button
                        SortMenu(
                            currentSort = sortOption,
                            onSortSelected = { sortOption = it }
                        )
                    }
                }

                // Stats Cards
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            value = totalReports.toString(),
                            label = "Total",
                            valueColor = TextDarkColor,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            value = normalReports.toString(),
                            label = "Normal",
                            valueColor = GreenColor,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            value = abnormalReports.toString(),
                            label = "Abnormal",
                            valueColor = RedColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Report Cards
                item { Spacer(modifier = Modifier.height(24.dp)) }
                
                if (sortedReports.isEmpty()) {
                    item {
                        Text(
                            text = "No reports found.",
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            textAlign = TextAlign.Center,
                            color = TextGrayColor
                        )
                    }
                } else {
                    items(sortedReports) { report ->
                        ReportCard(
                            report = report,
                            onClick = { onReportClick(report) },
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
                        )
                    }
                }

                // Medical Disclaimer
                item {
                    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
                        MedicalDisclaimerCard()
                    }
                }
            }
        }
    }
}

@Composable
private fun SortMenu(
    currentSort: SortOption,
    onSortSelected: (SortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = "Sort",
                tint = PrimaryBlue
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Newest First") },
                onClick = {
                    onSortSelected(SortOption.NEWEST)
                    expanded = false
                },
                leadingIcon = {
                    if (currentSort == SortOption.NEWEST) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                }
            )
            DropdownMenuItem(
                text = { Text("Oldest First") },
                onClick = {
                    onSortSelected(SortOption.OLDEST)
                    expanded = false
                },
                leadingIcon = {
                    if (currentSort == SortOption.OLDEST) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                }
            )
            DropdownMenuItem(
                text = { Text("Abnormal First") },
                onClick = {
                    onSortSelected(SortOption.ABNORMAL_FIRST)
                    expanded = false
                },
                leadingIcon = {
                    if (currentSort == SortOption.ABNORMAL_FIRST) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                }
            )
        }
    }
}

@Composable
private fun ReportHistoryTopBar(
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
private fun StatCard(
    value: String,
    label: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp))
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = valueColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = TextGrayColor
            )
        }
    }
}

@Composable
private fun ReportCard(
    report: HealthReport,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        if (report.isNormal) LightGreenBg else LightRedBg,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (report.isNormal) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (report.isNormal) GreenColor else RedColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Report Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = report.type,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        color = TextDarkColor
                    )
                    
                    // Report ID Badge
                    Surface(
                        color = BackgroundColor,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                    ) {
                        Text(
                            text = "#${report.id}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = TextGrayColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Date and Time
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = TextGrayColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = report.date,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                        color = TextGrayColor
                    )
                    
                    if (report.uploadedAt.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = TextGrayColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                         // Display only time part if possible, but full string is fine for now
                        Text(
                            text = report.uploadedAt, 
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                            color = TextGrayColor,
                            maxLines = 1
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                // Status Badge
                Box(
                    modifier = Modifier
                        .background(
                            if (report.isNormal) LightGreenBg else LightRedBg,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (report.isNormal) "Normal" else "${report.abnormalCount} Abnormal",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        ),
                        color = if (report.isNormal) GreenColor else RedColor
                    )
                }
            }

            // Document Icon
            Icon(
                imageVector = Icons.Default.ChevronRight, // Changed to ChevronRight for better affordance
                contentDescription = "View Details",
                tint = TextGrayColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReportHistoryScreenPreview() {
    DrugsSearchTheme {
        ReportHistoryScreen(userId = 1)
    }
}
