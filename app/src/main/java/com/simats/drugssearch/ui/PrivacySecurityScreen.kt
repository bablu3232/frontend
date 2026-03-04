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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.ui.theme.DrugsSearchTheme
import com.simats.drugssearch.SessionManager
import kotlinx.coroutines.launch

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
    onHistoryClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
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

                Spacer(modifier = Modifier.height(16.dp))
                
                // Account Deletion
                val context = LocalContext.current
                val sessionManager = remember { SessionManager(context) }
                val scope = rememberCoroutineScope()
                var showDeleteDialog by remember { mutableStateOf(false) }

                SecurityMenuItem(
                    icon = Icons.Outlined.Lock,
                    title = "Delete Account",
                    iconBackgroundColor = Color(0xFFFFEBEE), // Light red
                    iconTint = Color.Red,
                    onClick = { showDeleteDialog = true }
                )

                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("Delete Account") },
                        text = { Text("Are you sure you want to delete your account? This action is permanent and will delete all your health reports.") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val userId = sessionManager.getUserId()
                                    if (userId != -1) {
                                        scope.launch {
                                            try {
                                                val response = com.simats.drugssearch.network.RetrofitClient.instance.deleteAccount(mapOf("user_id" to userId))
                                                if (response.isSuccessful) {
                                                    sessionManager.clearSession()
                                                    onLogoutClick() // Triggers navigation back to Welcome
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                    }
                                    showDeleteDialog = false
                                },
                                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                            ) {
                                Text("Delete")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Biometric Login
                var isBiometricEnabled by remember { mutableStateOf(sessionManager.isBiometricEnabled()) }
                
                SecurityToggleItem(
                    icon = Icons.Outlined.Lock,
                    title = "Biometric Login",
                    subtitle = if (isBiometricEnabled) "Enabled" else "Not enabled",
                    iconBackgroundColor = LightPurpleBg,
                    iconTint = PurpleColor,
                    isChecked = isBiometricEnabled,
                    onCheckedChange = { checked ->
                        isBiometricEnabled = checked
                        sessionManager.setBiometricEnabled(checked)
                        
                        // Update backend
                        val userId = sessionManager.getUserId()
                        if (userId != -1) {
                            scope.launch {
                                try {
                                    val req = mapOf(
                                        "user_id" to userId,
                                        "biometric_enabled" to if (checked) 1 else 0
                                    )
                                    com.simats.drugssearch.network.RetrofitClient.instance.updateSecurity(req)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
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
