package com.simats.drugssearch.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.R
import com.simats.drugssearch.ui.theme.drugssearchTheme
import com.simats.drugssearch.network.RetrofitClient
import com.simats.drugssearch.network.ChangePasswordRequest
import kotlinx.coroutines.launch

// Change Password Screen Colors
private val PrimaryBlue = Color(0xFF3B82F6)
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextDarkColor = Color(0xFF1E293B)
private val TextGrayColor = Color(0xFF64748B)
private val InputBorderColor = Color(0xFFE2E8F0)
private val RequirementBg = Color(0xFFEFF6FF)
private val RequirementBorder = Color(0xFFDBEAFE)
private val RequirementTextColor = Color(0xFF1E40AF)
private val CancelBtnColor = Color(0xFFE2E8F0)

@Composable
fun ChangePasswordScreen(
    userId: Int,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onChangePasswordClick: (newPassword: String) -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    var currentPasswordError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    
    var isLoading by remember { mutableStateOf(false) }
    var apiMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
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
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Change Password",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = TextDarkColor
            )

            Text(
                text = "Update your account password",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGrayColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Content Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, InputBorderColor.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Current Password
                    Text(
                        text = "Current Password",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = TextDarkColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { 
                            currentPassword = it
                            currentPasswordError = null
                        },
                        placeholder = { Text("Enter current password", color = TextGrayColor.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                Icon(
                                    imageVector = if (currentPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = TextGrayColor
                                )
                            }
                        },
                        isError = currentPasswordError != null
                    )
                    
                    if (currentPasswordError != null) {
                        Text(text = currentPasswordError!!, color = Color.Red, fontSize = 12.sp)
                    }
                    
                    // Forgot Password Link
                    Text(
                        text = "Forgot Password?",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .align(Alignment.End)
                            .clickable { onForgotPasswordClick() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // New Password
                    Text(
                        text = "New Password",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = TextDarkColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { 
                            newPassword = it
                            passwordError = null
                        },
                        placeholder = { Text("Enter new password", color = TextGrayColor.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                Icon(
                                    imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = TextGrayColor
                                )
                            }
                        },
                        isError = passwordError != null
                    )
                    
                    if (passwordError != null) {
                        Text(text = passwordError!!, color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Password
                    Text(
                        text = "Confirm New Password",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = TextDarkColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { 
                            confirmPassword = it
                            confirmPasswordError = null
                        },
                        placeholder = { Text("Enter confirm new password", color = TextGrayColor.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = TextGrayColor
                                )
                            }
                        },
                        isError = confirmPasswordError != null
                    )
                    
                    if (confirmPasswordError != null) {
                        Text(text = confirmPasswordError!!, color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Password Requirements Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(RequirementBg, RoundedCornerShape(10.dp))
                            .border(1.dp, RequirementBorder, RoundedCornerShape(10.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Password must be at least 8 characters with uppercase, lowercase, number and special character.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = RequirementTextColor,
                                lineHeight = 18.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onBackClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = CancelBtnColor)
                        ) {
                            Text(text = "Cancel", color = TextDarkColor, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                var isValid = true
                                if (currentPassword.isEmpty()) {
                                    currentPasswordError = "Enter current password"
                                    isValid = false
                                }
                                if (newPassword.isEmpty()) {
                                    passwordError = "Enter new password"
                                    isValid = false
                                }
                                if (confirmPassword.isEmpty()) {
                                    confirmPasswordError = "Confirm new password"
                                    isValid = false
                                }
                                if (newPassword != confirmPassword) {
                                    confirmPasswordError = "Passwords do not match"
                                    isValid = false
                                }
                                
                                if (isValid) {
                                    isLoading = true
                                    currentPasswordError = null
                                    passwordError = null
                                    confirmPasswordError = null
                                    apiMessage = null
                                    
                                    scope.launch {
                                        try {
                                            val response = RetrofitClient.instance.changePassword(
                                                ChangePasswordRequest(
                                                    user_id = userId,
                                                    current_password = currentPassword,
                                                    new_password = newPassword,
                                                    confirm_password = confirmPassword
                                                )
                                            )
                                            if (response.isSuccessful) {
                                                android.widget.Toast.makeText(context, "Password Changed Successfully", android.widget.Toast.LENGTH_SHORT).show()
                                                onChangePasswordClick(newPassword)
                                            } else {
                                                val errorMsg = response.errorBody()?.string()?.let {
                                                    try {
                                                        org.json.JSONObject(it).getString("message")
                                                    } catch (e: Exception) {
                                                        "Failed to change password"
                                                    }
                                                } ?: "Failed to change password"
                                                apiMessage = errorMsg
                                            }
                                        } catch (e: Exception) {
                                            apiMessage = "Network error: ${e.message}"
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(text = "Update Password", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    
                    if (apiMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = apiMessage!!,
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = TextGrayColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Medical Disclaimer",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    color = TextGrayColor
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "This platform is designed for educational and informational purposes only. It is not a substitute for professional medical advice, diagnosis, or treatment. Always seek the advice of your physician or other qualified health provider with any questions you may have regarding a medical condition.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                ),
                color = TextGrayColor
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChangePasswordScreenPreview() {
    drugssearchTheme {
        ChangePasswordScreen(userId = 0)
    }
}
