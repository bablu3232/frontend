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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.ui.theme.DrugsSearchTheme

// Dashboard Colors
private val PrimaryBlue = Color(0xFF3B82F6)
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextDarkColor = Color(0xFF1E293B)
private val TextGrayColor = Color(0xFF64748B)
private val CardBorderColor = Color(0xFFE2E8F0)
private val DisclaimerBackground = Color(0xFFF1F5F9)

// Grid Item Colors
private val UploadBg = Color(0xFFDDEAFF)
private val UploadIcon = Color(0xFF3B82F6)
private val SearchBg = Color(0xFFDCFCE7)
private val SearchIcon = Color(0xFF22C55E)
private val HistoryBg = Color(0xFFF3E8FF)
private val HistoryIcon = Color(0xFFA855F7)
private val ProfileBg = Color(0xFFFFE4C2)
private val ProfileIcon = Color(0xFFF97316)

@Composable
fun DashboardScreen(
    userName: String = "John",
    totalReports: Int = 0,
    normalReports: Int = 0,
    abnormalReports: Int = 0,
    onUploadClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onViewAllClick: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentScreen = "Home",
                onHomeClick = onHomeClick,
                onUploadClick = onUploadClick,
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
            DashboardTopBar()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Welcome Section
                    Text(
                        text = "Welcome back, $userName!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = TextDarkColor
                    )
                    Text(
                        text = "Here's your health overview",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 15.sp
                        ),
                        color = TextGrayColor
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Grid Menu
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DashboardCard(
                            title = "Upload Report",
                            subtitle = "Analyze your test results",
                            icon = Icons.Default.Upload,
                            iconBgColor = UploadBg,
                            iconColor = UploadIcon,
                            modifier = Modifier.weight(1f),
                            onClick = onUploadClick
                        )
                        DashboardCard(
                            title = "Search Drug",
                            subtitle = "Find medication info",
                            icon = Icons.Default.Search,
                            iconBgColor = SearchBg,
                            iconColor = SearchIcon,
                            modifier = Modifier.weight(1f),
                            onClick = onSearchClick
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DashboardCard(
                            title = "View History",
                            subtitle = "Past reports & tests",
                            icon = Icons.Default.History,
                            iconBgColor = HistoryBg,
                            iconColor = HistoryIcon,
                            modifier = Modifier.weight(1f),
                            onClick = onHistoryClick
                        )
                        DashboardCard(
                            title = "Profile",
                            subtitle = "Settings & preferences",
                            icon = Icons.Default.Person,
                            iconBgColor = ProfileBg,
                            iconColor = ProfileIcon,
                            modifier = Modifier.weight(1f),
                            onClick = onProfileClick
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Health Summary
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Health Summary",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    ),
                                    color = Color.White
                                )
                                Icon(
                                    imageVector = Icons.Default.Timeline,
                                    contentDescription = "Graph",
                                    tint = Color.White.copy(alpha = 0.8f)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatItem("$totalReports", "Total Reports")
                                StatItem("$normalReports", "Normal")
                                StatItem("$abnormalReports", "Abnormal")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Recent Activity
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Activity",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = TextDarkColor
                        )
                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            ),
                            color = PrimaryBlue,
                            modifier = Modifier.clickable { onViewAllClick() }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Activity Item
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
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
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(SearchBg, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Description,
                                    contentDescription = "Report",
                                    tint = SearchIcon
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Blood Test Results",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = TextDarkColor
                                )
                                Text(
                                    text = "All values normal",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGrayColor
                                )
                            }
                            
                            Text(
                                text = "2 days ago",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGrayColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Disclaimer
                    DisclaimerSection()

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Footer Links
                    FooterLinks()
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Copyright
                    Text(
                        text = "© 2026 DrugSearch. All rights reserved.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = TextGrayColor,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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

        // App Title
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
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp
                ),
                color = TextGrayColor
            )
        }
        
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = TextGrayColor
            )
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(0.85f)
            .clickable(onClick = onClick)
            .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBgColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = TextDarkColor
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                ),
                color = TextGrayColor
            )
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp
            ),
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Composable
fun BottomNavigationBar(
    currentScreen: String,
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
            selected = currentScreen == "Home",
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
            selected = currentScreen == "Upload",
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
            selected = currentScreen == "Search",
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
            selected = currentScreen == "History",
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
            selected = currentScreen == "Profile",
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

@Composable
fun DisclaimerSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = DisclaimerBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
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
}

@Composable
fun FooterLinks() {
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
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardScreenPreview() {
    DrugsSearchTheme {
        DashboardScreen()
    }
}
