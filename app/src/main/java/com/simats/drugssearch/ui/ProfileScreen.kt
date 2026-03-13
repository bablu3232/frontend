package com.simats.drugssearch.ui

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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import com.simats.drugssearch.R
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import coil.compose.AsyncImage
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.ui.theme.DrugsSearchTheme

// Profile Screen Colors
private val PrimaryBlue = Color(0xFF2196F3)
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextDarkColor = Color(0xFF1E293B)
private val TextGrayColor = Color(0xFF64748B)
private val CardBorderColor = Color(0xFFE2E8F0)
private val LightBlueBg = Color(0xFFEFF6FF)
private val LightGreenBg = Color(0xFFECFDF5)
private val LightPurpleBg = Color(0xFFF3E8FF)
private val LightOrangeBg = Color(0xFFFFF7ED)
private val LightRedBg = Color(0xFFFEF2F2)
private val DisclaimerBackground = Color(0xFFF1F5F9)
private val GreenColor = Color(0xFF10B981)
private val PurpleColor = Color(0xFF8B5CF6)
private val OrangeColor = Color(0xFFF97316)
private val RedColor = Color(0xFFEF4444)

@Composable
fun ProfileScreen(
    userName: String = "John Doe",
    userEmail: String = "john.doe@email.com",
    totalReports: Int = 12,
    normalReports: Int = 8,
    abnormalReports: Int = 4,
    profileImage: String = "",
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onPersonalInfoClick: () -> Unit = {},
    onPrivacySecurityClick: () -> Unit = {},

    onAboutAppClick: () -> Unit = {},
    onHelpSupportClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onNavigationHomeClick: () -> Unit = {},
    onUploadClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    onTermsOfServiceClick: () -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            DrugSearchBottomNav(
                selectedTab = "Profile",
                onHomeClick = onNavigationHomeClick,
                onUploadClick = onUploadClick,
                onSearchClick = onSearchClick,
                onHistoryClick = onHistoryClick,
                onProfileClick = {}
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
            ProfileTopBar(
                onBackClick = onBackClick,
                onHomeClick = onHomeClick
            )

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Profile Header Section
                ProfileHeaderSection(
                    userName = userName,
                    userEmail = userEmail,
                    totalReports = totalReports,
                    normalReports = normalReports,
                    abnormalReports = abnormalReports,
                    profileImage = profileImage
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Menu Items
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileMenuItem(
                        icon = Icons.Outlined.Person,
                        title = "Personal Information",
                        iconBackgroundColor = LightBlueBg,
                        iconTint = PrimaryBlue,
                        onClick = onPersonalInfoClick
                    )

                    ProfileMenuItem(
                        icon = Icons.Outlined.Shield,
                        title = "Privacy & Security",
                        iconBackgroundColor = LightGreenBg,
                        iconTint = GreenColor,
                        onClick = onPrivacySecurityClick
                    )



                    ProfileMenuItem(
                        icon = Icons.Outlined.Info,
                        title = "About App",
                        iconBackgroundColor = LightOrangeBg,
                        iconTint = OrangeColor,
                        onClick = onAboutAppClick
                    )

                    ProfileMenuItem(
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        title = "Help & Support",
                        iconBackgroundColor = LightBlueBg,
                        iconTint = PrimaryBlue,
                        onClick = onHelpSupportClick
                    )

                    ProfileMenuItem(
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        title = "Logout",
                        iconBackgroundColor = LightRedBg,
                        iconTint = RedColor,
                        onClick = { showLogoutDialog = true }
                    )
                }

                if (showLogoutDialog) {
                    AlertDialog(
                        onDismissRequest = { showLogoutDialog = false },
                        title = {
                            Text(text = "Confirm Logout", fontWeight = FontWeight.Bold, color = Color.Black)
                        },
                        text = {
                            Text("Are you sure you want to log out of your account?", color = Color.Black)
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showLogoutDialog = false
                                    onLogoutClick()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = RedColor)
                            ) {
                                Text("Yes, Logout")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showLogoutDialog = false }) {
                                Text("Cancel", color = TextGrayColor)
                            }
                        },
                        containerColor = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Medical Disclaimer
                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    MedicalDisclaimerCard()
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Footer Links
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Privacy Policy",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        ),
                        color = PrimaryBlue,
                        modifier = Modifier.clickable { onPrivacyPolicyClick() }
                    )
                    Text(
                        text = "  •  ",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = TextGrayColor
                    )
                    Text(
                        text = "Terms of Service",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        ),
                        color = PrimaryBlue,
                        modifier = Modifier.clickable { onTermsOfServiceClick() }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Copyright
                Text(
                    text = "2026 © Powered by SIMATS Engineering",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp
                    ),
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
private fun ProfileTopBar(
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
private fun ProfileHeaderSection(
    userName: String,
    userEmail: String,
    totalReports: Int,
    normalReports: Int,
    abnormalReports: Int,
    profileImage: String = ""
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(if (profileImage.isEmpty()) PrimaryBlue else Color.LightGray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (profileImage.isNotEmpty()) {
                val fullUrl = if (profileImage.startsWith("http")) profileImage else "http://10.88.244.212/drugssearch/$profileImage"
                AsyncImage(
                    model = fullUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User Name
        Text(
            text = userName,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = TextDarkColor
        )

        Spacer(modifier = Modifier.height(4.dp))

        // User Email
        Text(
            text = userEmail,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = TextGrayColor
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Stats Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStatItem(value = totalReports.toString(), label = "Reports")
                ProfileStatItem(value = normalReports.toString(), label = "Normal")
                ProfileStatItem(value = abnormalReports.toString(), label = "Abnormal")
            }
        }
    }
}

@Composable
private fun ProfileStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = TextDarkColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = TextGrayColor
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    iconBackgroundColor: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
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
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconBackgroundColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                ),
                color = TextDarkColor,
                modifier = Modifier.weight(1f)
            )

            // Arrow
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextGrayColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    DrugsSearchTheme {
        ProfileScreen()
    }
}
