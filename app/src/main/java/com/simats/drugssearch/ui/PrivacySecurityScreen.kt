package com.simats.drugssearch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
private val LightBlueBg = Color(0xFFEFF6FF)
private val LightGreenBg = Color(0xFFECFDF5)
private val LightPurpleBg = Color(0xFFF3E8FF)
private val GreenColor = Color(0xFF10B981)
private val PurpleColor = Color(0xFF8B5CF6)


@Composable
fun PrivacySecurityScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onUploadClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            DrugSearchBottomNav(
                selectedTab = "Profile",
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
            PrivacySecurityTopBar(
                onBackClick = onBackClick,
                onHomeClick = onHomeClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Manage your security settings",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextGrayColor,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Change Password
                SecurityMenuItem(
                    icon = Icons.Outlined.Key,
                    title = "Change Password",
                    iconBackgroundColor = LightBlueBg,
                    iconTint = PrimaryBlue,
                    onClick = onChangePasswordClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Two-Factor Authentication
                var isTwoFactorEnabled by remember { mutableStateOf(false) }
                SecurityToggleItem(
                    icon = Icons.Outlined.Security,
                    title = "Two-Factor Authentication",
                    subtitle = if (isTwoFactorEnabled) "Enabled" else "Not enabled",
                    iconBackgroundColor = LightGreenBg,
                    iconTint = GreenColor,
                    isChecked = isTwoFactorEnabled,
                    onCheckedChange = { isTwoFactorEnabled = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                 // Biometric Login (Optional but good for filling space and consistent with 'Security')
                 var isBiometricEnabled by remember { mutableStateOf(false) }
                 SecurityToggleItem(
                    icon = Icons.Outlined.Lock,
                    title = "Biometric Login",
                    subtitle = null,
                    iconBackgroundColor = LightPurpleBg,
                    iconTint = PurpleColor,
                    isChecked = isBiometricEnabled,
                    onCheckedChange = { isBiometricEnabled = it }
                )
            }
        }
    }
}

@Composable
private fun PrivacySecurityTopBar(
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

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Privacy & Security",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = TextDarkColor
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
private fun SecurityMenuItem(
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

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                ),
                color = TextDarkColor,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextGrayColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun SecurityToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    iconBackgroundColor: Color,
    iconTint: Color,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    ),
                    color = TextDarkColor
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 14.sp
                        ),
                        color = TextGrayColor
                    )
                }
            }

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryBlue,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFE2E8F0)
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PrivacySecurityScreenPreview() {
    DrugsSearchTheme {
        PrivacySecurityScreen()
    }
}
