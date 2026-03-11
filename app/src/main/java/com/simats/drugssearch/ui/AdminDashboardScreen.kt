package com.simats.drugssearch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.simats.drugssearch.network.RetrofitClient
import com.simats.drugssearch.network.AdminStats
import com.simats.drugssearch.network.AdminUser
import com.simats.drugssearch.network.AdminReport
import com.simats.drugssearch.network.AddParameterRequest
import com.simats.drugssearch.network.AddDrugRequest
import com.simats.drugssearch.network.DeleteItemRequest
import com.simats.drugssearch.network.AdminParameterItem
import com.simats.drugssearch.network.AdminDrugItem

private val AdminPrimary = Color(0xFF1E293B)
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextDarkColor = Color(0xFF0F172A)
private val TextGrayColor = Color(0xFF64748B)
private val GreenColor = Color(0xFF10B981)
private val RedColor = Color(0xFFEF4444)
private val BlueColor = Color(0xFF3B82F6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onLogoutClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var stats by remember { mutableStateOf<AdminStats?>(null) }
    var users by remember { mutableStateOf<List<AdminUser>>(emptyList()) }
    var reports by remember { mutableStateOf<List<AdminReport>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) } // 0 = Overview, 1 = Users, 2 = Edit
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                Button(
                    onClick = { showLogoutDialog = false; onLogoutClick() },
                    colors = ButtonDefaults.buttonColors(containerColor = RedColor)
                ) { Text("Logout", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                val statsResponse = RetrofitClient.instance.getAdminStats()
                val usersResponse = RetrofitClient.instance.getAdminUsers()
                val reportsResponse = RetrofitClient.instance.getAdminReports()

                withContext(Dispatchers.Main) {
                    if (statsResponse.isSuccessful) stats = statsResponse.body()?.stats
                    if (usersResponse.isSuccessful) users = usersResponse.body()?.users ?: emptyList()
                    if (reportsResponse.isSuccessful) reports = reportsResponse.body()?.reports ?: emptyList()
                    isLoading = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AdminPrimary),
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Assessment, contentDescription = "Overview") },
                    label = { Text("Overview") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AdminPrimary, selectedTextColor = AdminPrimary,
                        unselectedIconColor = Color.Black, unselectedTextColor = Color.Black,
                        indicatorColor = Color.White
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.People, contentDescription = "Users") },
                    label = { Text("Users") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AdminPrimary, selectedTextColor = AdminPrimary,
                        unselectedIconColor = Color.Black, unselectedTextColor = Color.Black,
                        indicatorColor = Color.White
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Edit, contentDescription = "Edit") },
                    label = { Text("Edit") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AdminPrimary, selectedTextColor = AdminPrimary,
                        unselectedIconColor = Color.Black, unselectedTextColor = Color.Black,
                        indicatorColor = Color.White
                    )
                )
            }
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AdminPrimary)
            } else {
                when (selectedTab) {
                    0 -> AdminOverviewTab(stats)
                    1 -> AdminUsersTab(users)
                    2 -> AdminEditTab()
                }
            }
        }
    }
}

// ─── Overview Tab ───
@Composable
fun AdminOverviewTab(stats: AdminStats?) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState)) {
        Text("Platform Statistics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextDarkColor)
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(modifier = Modifier.weight(1f), title = "Active Now", value = stats?.activeUsers?.toString() ?: "0", icon = Icons.Default.CheckCircle, color = Color(0xFF0EA5E9))
            StatCard(modifier = Modifier.weight(1f), title = "Total Users", value = stats?.totalUsers?.toString() ?: "0", icon = Icons.Default.People, color = BlueColor)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(modifier = Modifier.weight(1f), title = "Total Reports", value = stats?.totalReports?.toString() ?: "0", icon = Icons.Default.Assessment, color = GreenColor)
            StatCard(modifier = Modifier.weight(1f), title = "Total Drugs", value = stats?.totalDrugs?.toString() ?: "0", icon = Icons.Default.Medication, color = Color(0xFFF59E0B))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(modifier = Modifier.weight(0.5f), title = "Lab Parameters", value = stats?.totalParameters?.toString() ?: "0", icon = Icons.Default.Science, color = Color(0xFF8B5CF6))
            Spacer(modifier = Modifier.weight(0.5f)) // Empty pad to keep grid aligned 2 per row
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("System Statistics Trend", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextDarkColor)
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth().height(260.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            if (stats?.chartData != null && stats.chartData.labels.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    MultiLineChart(modifier = Modifier.fillMaxSize(), chartData = stats.chartData)
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading interactive chart data...", color = TextGrayColor)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Currently Active Users", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextDarkColor)
        Spacer(modifier = Modifier.height(12.dp))
        
        if (stats?.activeUsersList != null && stats.activeUsersList.isNotEmpty()) {
            stats.activeUsersList.forEach { user ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(10.dp).background(GreenColor, androidx.compose.foundation.shape.CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(user.name, fontWeight = FontWeight.Bold, color = TextDarkColor, fontSize = 15.sp)
                            Text(user.email, color = TextGrayColor, fontSize = 13.sp)
                        }
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("No users are currently active.", color = TextGrayColor, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, title: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
            Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextDarkColor)
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = TextGrayColor)
        }
    }
}

@Composable
fun MultiLineChart(modifier: Modifier = Modifier, chartData: com.simats.drugssearch.network.ChartData) {
    if (chartData.labels.isEmpty()) return
    
    val usersData = chartData.users.map { it.toFloat() }
    val reportsData = chartData.reports.map { it.toFloat() }
    
    val maxUsers = usersData.maxOrNull() ?: 10f
    val maxReports = reportsData.maxOrNull() ?: 10f
    val rawMaxVal = maxOf(maxUsers, maxReports).coerceAtLeast(10f)
    
    // Calculate nice dynamic Y-axis intervals (4 segments)
    val rawStep = rawMaxVal / 4f
    val magnitude = Math.pow(10.0, kotlin.math.floor(kotlin.math.log10(rawStep.toDouble()))).toFloat()
    val rawStepNormalized = rawStep / magnitude
    val niceStepNormalized = when {
        rawStepNormalized <= 1.0f -> 1.0f
        rawStepNormalized <= 2.0f -> 2.0f
        rawStepNormalized <= 2.5f -> 2.5f
        rawStepNormalized <= 5.0f -> 5.0f
        else -> 10.0f
    }
    val niceStep = niceStepNormalized * magnitude
    val cleanMaxVal = (niceStep * 4).coerceAtLeast(10f)
    
    val minVal = 0f
    val range = cleanMaxVal - minVal
    
    val usersColor = Color(0xFF0EA5E9)   // Blue
    val reportsColor = Color(0xFF10B981) // Green

    val paint = androidx.compose.ui.graphics.Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        textSize = 30f
        color = android.graphics.Color.parseColor("#94A3B8")
        textAlign = android.graphics.Paint.Align.CENTER
    }

    androidx.compose.foundation.Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val paddingX = 80f // left margin for Y labels
        val paddingY = 60f // bottom margin for X labels
        val topPadding = 20f
        
        val usableWidth = width - paddingX
        val usableHeight = height - paddingY - topPadding
        val stepX = usableWidth / (chartData.labels.size - 1).coerceAtLeast(1)
        
        // Draw grid lines & Y-axis labels
        val gridLines = 4
        val gridColor = Color(0xFFE2E8F0)
        for (i in 0..gridLines) {
            val y = topPadding + usableHeight - (i * (usableHeight / gridLines))
            val valueLabel = (minVal + (i * range / gridLines)).toInt().toString()
            
            drawLine(
                color = gridColor,
                start = androidx.compose.ui.geometry.Offset(paddingX, y),
                end = androidx.compose.ui.geometry.Offset(width, y),
                strokeWidth = 2f
            )
            drawContext.canvas.nativeCanvas.drawText(
                valueLabel,
                paddingX / 2,
                y + 10f,
                paint.apply { textAlign = android.graphics.Paint.Align.CENTER }
            )
        }
        
        // Draw Axes lines
        val axisColor = Color(0xFF94A3B8)
        drawLine(
            color = axisColor,
            start = androidx.compose.ui.geometry.Offset(paddingX, topPadding),
            end = androidx.compose.ui.geometry.Offset(paddingX, topPadding + usableHeight),
            strokeWidth = 3f
        )
        drawLine(
            color = axisColor,
            start = androidx.compose.ui.geometry.Offset(paddingX, topPadding + usableHeight),
            end = androidx.compose.ui.geometry.Offset(width, topPadding + usableHeight),
            strokeWidth = 3f
        )
        
        // X-axis labels
        chartData.labels.forEachIndexed { index, monthLabel ->
            val x = paddingX + (index * stepX)
            drawContext.canvas.nativeCanvas.drawText(
                monthLabel,
                x,
                topPadding + usableHeight + 40f,
                paint.apply { textAlign = android.graphics.Paint.Align.CENTER }
            )
        }

        fun drawDataLine(data: List<Float>, lineColor: Color) {
            if (data.isEmpty()) return
            val points = data.mapIndexed { index, value ->
                val x = paddingX + (index * stepX)
                val y = topPadding + usableHeight - ((value - minVal) / range) * usableHeight
                androidx.compose.ui.geometry.Offset(x, y)
            }

            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
            }
            
            drawPath(
                path = path,
                color = lineColor,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 6f, 
                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                    join = androidx.compose.ui.graphics.StrokeJoin.Round
                )
            )
            
            points.forEach { point ->
                drawCircle(color = lineColor, radius = 10f, center = point)
                drawCircle(color = Color.White, radius = 5f, center = point)
            }
        }

        drawDataLine(usersData, usersColor)
        drawDataLine(reportsData, reportsColor)
        
        // Legend
        val legendY = 0f
        drawCircle(color = usersColor, radius = 8f, center = androidx.compose.ui.geometry.Offset(paddingX + 20f, legendY))
        drawContext.canvas.nativeCanvas.drawText("Users", paddingX + 70f, legendY + 10f, paint.apply { textAlign = android.graphics.Paint.Align.CENTER })
        
        val reportsLegendX = paddingX + 160f
        drawCircle(color = reportsColor, radius = 8f, center = androidx.compose.ui.geometry.Offset(reportsLegendX, legendY))
        drawContext.canvas.nativeCanvas.drawText("Reports", reportsLegendX + 60f, legendY + 10f, paint.apply { textAlign = android.graphics.Paint.Align.CENTER })
    }
}

// ─── Users Tab with Popup ───
@Composable
fun AdminUsersTab(users: List<AdminUser>) {
    val scope = rememberCoroutineScope()
    var selectedUser by remember { mutableStateOf<AdminUser?>(null) }
    var userTotalReports by remember { mutableStateOf(0) }
    var userNormalValues by remember { mutableStateOf(0) }
    var userAbnormalValues by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var isLoadingStats by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("Registered Users", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextDarkColor)
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(users) { user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable {
                        selectedUser = user
                        isLoadingStats = true
                        showDialog = true
                        scope.launch(Dispatchers.IO) {
                            try {
                                val response = RetrofitClient.instance.getAdminUserStats(user.id)
                                withContext(Dispatchers.Main) {
                                    if (response.isSuccessful && response.body() != null) {
                                        val body = response.body()!!
                                        userTotalReports = body.totalReports
                                        userNormalValues = body.normalValues
                                        userAbnormalValues = body.abnormalValues
                                    }
                                    isLoadingStats = false
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) { isLoadingStats = false }
                            }
                        }
                    },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = user.fullName, fontWeight = FontWeight.Bold, color = TextDarkColor)
                    Text(text = user.email, style = MaterialTheme.typography.bodyMedium, color = TextGrayColor)
                    Text(text = "Joined: ${user.createdAt}", style = MaterialTheme.typography.bodySmall, color = TextGrayColor)
                }
            }
        }
    }

    // User Stats Dialog
    if (showDialog && selectedUser != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = selectedUser!!.fullName,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            text = {
                if (isLoadingStats) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AdminPrimary, modifier = Modifier.size(32.dp))
                    }
                } else {
                    Column {
                        Text(text = selectedUser!!.email, color = TextGrayColor, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        UserStatRow("Total Reports", userTotalReports.toString(), BlueColor)
                        Spacer(modifier = Modifier.height(8.dp))
                        UserStatRow("Normal Values", userNormalValues.toString(), GreenColor)
                        Spacer(modifier = Modifier.height(8.dp))
                        UserStatRow("Abnormal Values", userAbnormalValues.toString(), RedColor)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Close", color = AdminPrimary)
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun UserStatRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.Black, fontWeight = FontWeight.Medium, fontSize = 15.sp)
        Text(text = value, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

// ─── Edit Tab ───
@Composable
fun AdminEditTab() {
    val scope = rememberCoroutineScope()
    var expandedForm by remember { mutableStateOf<String?>(null) }

    // Lab Parameter fields
    var paramName by remember { mutableStateOf("") }
    var paramUnit by remember { mutableStateOf("") }
    var paramMin by remember { mutableStateOf("") }
    var paramMax by remember { mutableStateOf("") }
    var paramCategory by remember { mutableStateOf("") }
    var paramCondition by remember { mutableStateOf("") }
    var paramDrugCategory by remember { mutableStateOf("") }
    var paramExampleDrugs by remember { mutableStateOf("") }
    var paramSummary by remember { mutableStateOf("") }
    var paramLoading by remember { mutableStateOf(false) }
    var paramMessage by remember { mutableStateOf<String?>(null) }

    // Drug fields
    var drugName by remember { mutableStateOf("") }
    var genericName by remember { mutableStateOf("") }
    var drugCategory by remember { mutableStateOf("") }
    var indication by remember { mutableStateOf("") }
    var commonDosage by remember { mutableStateOf("") }
    var drugDescription by remember { mutableStateOf("") }
    var sideEffects by remember { mutableStateOf("") }
    var safetyWarnings by remember { mutableStateOf("") }
    var storageDetails by remember { mutableStateOf("") }
    var drugLoading by remember { mutableStateOf(false) }
    var drugMessage by remember { mutableStateOf<String?>(null) }

    // Delete lists
    var paramList by remember { mutableStateOf<List<AdminParameterItem>>(emptyList()) }
    var drugsList by remember { mutableStateOf<List<AdminDrugItem>>(emptyList()) }
    var paramSearchQuery by remember { mutableStateOf("") }
    var drugSearchQuery by remember { mutableStateOf("") }
    var paramSortBy by remember { mutableStateOf("name") }
    var drugSortBy by remember { mutableStateOf("name") }
    var deleteConfirmItem by remember { mutableStateOf<Pair<String, Pair<Int, String>>?>(null) } // type to (id, name)

    // Fetch lists on load
    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                val pRes = RetrofitClient.instance.getAdminParameters()
                val dRes = RetrofitClient.instance.getAdminDrugs()
                withContext(Dispatchers.Main) {
                    if (pRes.isSuccessful) paramList = pRes.body()?.parameters ?: emptyList()
                    if (dRes.isSuccessful) drugsList = dRes.body()?.drugs ?: emptyList()
                }
            } catch (_: Exception) {}
        }
    }

    // Delete confirmation dialog
    if (deleteConfirmItem != null) {
        val (type, idName) = deleteConfirmItem!!
        val (id, name) = idName
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { deleteConfirmItem = null },
            title = { Text("Delete ${if (type == "param") "Parameter" else "Drug"}") },
            text = { Text("Are you sure you want to delete \"$name\"? This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        deleteConfirmItem = null
                        scope.launch(Dispatchers.IO) {
                            try {
                                val res = if (type == "param")
                                    RetrofitClient.instance.adminDeleteParameter(DeleteItemRequest(id))
                                else
                                    RetrofitClient.instance.adminDeleteDrug(DeleteItemRequest(id))
                                withContext(Dispatchers.Main) {
                                    if (res.isSuccessful) {
                                        if (type == "param") {
                                            paramList = paramList.filter { it.id != id }
                                            paramMessage = "\"$name\" deleted"
                                        } else {
                                            drugsList = drugsList.filter { it.id != id }
                                            drugMessage = "\"$name\" deleted"
                                        }
                                    }
                                }
                            } catch (_: Exception) {}
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedColor)
                ) { Text("Delete", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmItem = null }) { Text("Cancel") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Data Management", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextDarkColor)
        Spacer(modifier = Modifier.height(16.dp))

        // ─── Add Lab Parameter Card ───
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandedForm = if (expandedForm == "parameter") null else "parameter" },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Science, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Add Lab Parameter", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDarkColor)
                }

                if (expandedForm == "parameter") {
                    Spacer(modifier = Modifier.height(16.dp))
                    AdminTextField(value = paramName, onValueChange = { paramName = it }, label = "Parameter Name *")
                    AdminTextField(value = paramUnit, onValueChange = { paramUnit = it }, label = "Unit *")
                    AdminTextField(value = paramMin, onValueChange = { paramMin = it }, label = "Min Value")
                    AdminTextField(value = paramMax, onValueChange = { paramMax = it }, label = "Max Value")
                    AdminTextField(value = paramCategory, onValueChange = { paramCategory = it }, label = "Category")
                    AdminTextField(value = paramCondition, onValueChange = { paramCondition = it }, label = "Condition If Abnormal")
                    AdminTextField(value = paramDrugCategory, onValueChange = { paramDrugCategory = it }, label = "Drug Category")
                    AdminTextField(value = paramExampleDrugs, onValueChange = { paramExampleDrugs = it }, label = "Example Drugs")
                    AdminTextField(value = paramSummary, onValueChange = { paramSummary = it }, label = "Summary")
                    Spacer(modifier = Modifier.height(8.dp))

                    if (paramMessage != null) {
                        Text(paramMessage!!, color = if (paramMessage!!.contains("success", true) || paramMessage!!.contains("deleted", true)) GreenColor else RedColor, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Button(
                        onClick = {
                            if (paramName.isBlank() || paramUnit.isBlank()) {
                                paramMessage = "Parameter name and unit are required"
                                return@Button
                            }
                            paramLoading = true
                            paramMessage = null
                            scope.launch(Dispatchers.IO) {
                                try {
                                    val req = AddParameterRequest(
                                        parameterName = paramName.trim(),
                                        unit = paramUnit.trim(),
                                        minValue = paramMin.toDoubleOrNull() ?: 0.0,
                                        maxValue = paramMax.toDoubleOrNull() ?: 0.0,
                                        category = paramCategory.trim(),
                                        conditionIfAbnormal = paramCondition.trim(),
                                        drugCategory = paramDrugCategory.trim(),
                                        exampleDrugs = paramExampleDrugs.trim()
                                    )
                                    val response = RetrofitClient.instance.adminAddParameter(req)
                                    // Refresh list
                                    val pRes = RetrofitClient.instance.getAdminParameters()
                                    withContext(Dispatchers.Main) {
                                        paramLoading = false
                                        if (pRes.isSuccessful) paramList = pRes.body()?.parameters ?: emptyList()
                                        paramMessage = if (response.isSuccessful) {
                                            paramName = ""; paramUnit = ""; paramMin = ""; paramMax = ""
                                            paramCategory = ""; paramCondition = ""; paramDrugCategory = ""; paramExampleDrugs = ""; paramSummary = ""
                                            "Lab parameter added successfully!"
                                        } else "Failed to add parameter"
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        paramLoading = false
                                        paramMessage = "Error: ${e.localizedMessage}"
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AdminPrimary),
                        enabled = !paramLoading
                    ) {
                        if (paramLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        else Text("Add Parameter", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ─── Delete Parameters (Expandable) ───
        Card(
            modifier = Modifier.fillMaxWidth().clickable { expandedForm = if (expandedForm == "delete-param") null else "delete-param" },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = RedColor, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Delete Parameters (${paramList.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDarkColor)
                }

                if (expandedForm == "delete-param") {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = paramSearchQuery, onValueChange = { paramSearchQuery = it },
                        placeholder = { Text("Search parameters...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AdminPrimary, unfocusedBorderColor = Color(0xFFE2E8F0), cursorColor = AdminPrimary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Sort chips
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Name" to "name", "Unit" to "unit", "Category" to "category").forEach { (label, key) ->
                            FilterChip(
                                selected = paramSortBy == key,
                                onClick = { paramSortBy = key },
                                label = { Text(label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AdminPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    if (paramMessage != null) {
                        Text(paramMessage!!, color = if (paramMessage!!.contains("deleted", true)) GreenColor else RedColor, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    val filteredParams = paramList
                        .filter { it.parameterName.contains(paramSearchQuery, ignoreCase = true) }
                        .sortedBy { when (paramSortBy) { "unit" -> it.unit; "category" -> it.category ?: ""; else -> it.parameterName } }

                    Box(modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp)) {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            filteredParams.forEach { p ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(p.parameterName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextDarkColor)
                                        Text("${p.unit} | ${p.minValue ?: ""} - ${p.maxValue ?: ""} | ${p.category ?: ""}", fontSize = 12.sp, color = TextGrayColor)
                                    }
                                    IconButton(onClick = { deleteConfirmItem = Pair("param", Pair(p.id, p.parameterName)) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedColor, modifier = Modifier.size(22.dp))
                                    }
                                }
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                            }
                            if (filteredParams.isEmpty()) {
                                Text("No parameters found", color = TextGrayColor, fontSize = 13.sp, modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ─── Add Drug Card ───
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandedForm = if (expandedForm == "drug") null else "drug" },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Medication, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Add Drug", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDarkColor)
                }

                if (expandedForm == "drug") {
                    Spacer(modifier = Modifier.height(16.dp))
                    AdminTextField(value = drugName, onValueChange = { drugName = it }, label = "Drug Name *")
                    AdminTextField(value = genericName, onValueChange = { genericName = it }, label = "Generic Name")
                    AdminTextField(value = drugCategory, onValueChange = { drugCategory = it }, label = "Drug Category")
                    AdminTextField(value = indication, onValueChange = { indication = it }, label = "Indication")
                    AdminTextField(value = commonDosage, onValueChange = { commonDosage = it }, label = "Common Dosage")
                    AdminTextField(value = drugDescription, onValueChange = { drugDescription = it }, label = "Description")
                    AdminTextField(value = sideEffects, onValueChange = { sideEffects = it }, label = "Side Effects")
                    AdminTextField(value = safetyWarnings, onValueChange = { safetyWarnings = it }, label = "Safety Warnings")
                    AdminTextField(value = storageDetails, onValueChange = { storageDetails = it }, label = "Storage Details")
                    Spacer(modifier = Modifier.height(8.dp))

                    if (drugMessage != null) {
                        Text(drugMessage!!, color = if (drugMessage!!.contains("success", true) || drugMessage!!.contains("deleted", true)) GreenColor else RedColor, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Button(
                        onClick = {
                            if (drugName.isBlank()) {
                                drugMessage = "Drug name is required"
                                return@Button
                            }
                            drugLoading = true
                            drugMessage = null
                            scope.launch(Dispatchers.IO) {
                                try {
                                    val req = AddDrugRequest(
                                        drugName = drugName.trim(),
                                        genericName = genericName.trim(),
                                        drugCategory = drugCategory.trim(),
                                        indication = indication.trim(),
                                        commonDosage = commonDosage.trim(),
                                        description = drugDescription.trim(),
                                        sideEffects = sideEffects.trim(),
                                        safetyWarnings = safetyWarnings.trim(),
                                        storageDetails = storageDetails.trim()
                                    )
                                    val response = RetrofitClient.instance.adminAddDrug(req)
                                    val dRes = RetrofitClient.instance.getAdminDrugs()
                                    withContext(Dispatchers.Main) {
                                        drugLoading = false
                                        if (dRes.isSuccessful) drugsList = dRes.body()?.drugs ?: emptyList()
                                        drugMessage = if (response.isSuccessful) {
                                            drugName = ""; genericName = ""; drugCategory = ""; indication = ""
                                            commonDosage = ""; drugDescription = ""; sideEffects = ""; safetyWarnings = ""; storageDetails = ""
                                            "Drug added successfully!"
                                        } else "Failed to add drug"
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        drugLoading = false
                                        drugMessage = "Error: ${e.localizedMessage}"
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AdminPrimary),
                        enabled = !drugLoading
                    ) {
                        if (drugLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        else Text("Add Drug", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ─── Delete Drugs (Expandable) ───
        Card(
            modifier = Modifier.fillMaxWidth().clickable { expandedForm = if (expandedForm == "delete-drug") null else "delete-drug" },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = RedColor, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Delete Drugs (${drugsList.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDarkColor)
                }

                if (expandedForm == "delete-drug") {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = drugSearchQuery, onValueChange = { drugSearchQuery = it },
                        placeholder = { Text("Search drugs...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AdminPrimary, unfocusedBorderColor = Color(0xFFE2E8F0), cursorColor = AdminPrimary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Sort chips
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Name" to "name", "Generic" to "generic", "Category" to "category").forEach { (label, key) ->
                            FilterChip(
                                selected = drugSortBy == key,
                                onClick = { drugSortBy = key },
                                label = { Text(label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AdminPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    if (drugMessage != null) {
                        Text(drugMessage!!, color = if (drugMessage!!.contains("deleted", true)) GreenColor else RedColor, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    val filteredDrugs = drugsList
                        .filter { it.drugName.contains(drugSearchQuery, ignoreCase = true) }
                        .sortedBy { when (drugSortBy) { "generic" -> it.genericName ?: ""; "category" -> it.drugCategory ?: ""; else -> it.drugName } }

                    Box(modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp)) {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            filteredDrugs.forEach { d ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(d.drugName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextDarkColor)
                                        Text("${d.genericName ?: "-"} | ${d.drugCategory ?: "-"}", fontSize = 12.sp, color = TextGrayColor)
                                    }
                                    IconButton(onClick = { deleteConfirmItem = Pair("drug", Pair(d.id, d.drugName)) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedColor, modifier = Modifier.size(22.dp))
                                    }
                                }
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                            }
                            if (filteredDrugs.isEmpty()) {
                                Text("No drugs found", color = TextGrayColor, fontSize = 13.sp, modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun AdminTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedBorderColor = AdminPrimary,
            unfocusedBorderColor = Color(0xFFE2E8F0),
            cursorColor = AdminPrimary,
            focusedLabelColor = AdminPrimary,
            unfocusedLabelColor = TextGrayColor
        ),
        shape = RoundedCornerShape(10.dp)
    )
}
