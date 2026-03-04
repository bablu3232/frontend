package com.simats.drugssearch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.ui.theme.drugssearchTheme
import com.simats.drugssearch.network.RetrofitClient
import com.simats.drugssearch.network.ResetPasswordRequest
import kotlinx.coroutines.launch

// Reset Password Screen Colors
private val PrimaryBlue = Color(0xFF3B82F6)
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextDarkColor = Color(0xFF1E293B)
private val TextGrayColor = Color(0xFF64748B)
private val InputBorderColor = Color(0xFFE2E8F0)
private val RequirementBg = Color(0xFFEFF6FF)
private val RequirementBorder = Color(0xFFDBEAFE)
private val RequirementTextColor = Color(0xFF1E40AF)
private val CancelBtnColor = Color(0xFFE2E8F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    email: String,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onResetPasswordClick: () -> Unit = {},
    onBackToLoginClick: () -> Unit = {}
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Header
            Text(
                text = "Reset Password",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                color = TextDarkColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Create a new strong password for your account.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp
                ),
                color = TextGrayColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // New Password Input
            Text(
                text = "New Password",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = TextDarkColor,
                modifier = Modifier.align(Alignment.Start)
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
                isError = passwordError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = if (passwordError != null) Color.Red else PrimaryBlue,
                    unfocusedBorderColor = if (passwordError != null) Color.Red else InputBorderColor,
                    cursorColor = PrimaryBlue
                )
            )
            if (passwordError != null) {
                Text(
                    text = passwordError!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Start).padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Input
            Text(
                text = "Confirm Password",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = TextDarkColor,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { 
                    confirmPassword = it
                    confirmPasswordError = null
                },
                placeholder = { Text("Confirm new password", color = TextGrayColor.copy(alpha = 0.5f)) },
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
                isError = confirmPasswordError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = if (confirmPasswordError != null) Color.Red else PrimaryBlue,
                    unfocusedBorderColor = if (confirmPasswordError != null) Color.Red else InputBorderColor,
                    cursorColor = PrimaryBlue
                )
            )
            if (confirmPasswordError != null) {
                Text(
                    text = confirmPasswordError!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Start).padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Password Requirements
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
                        if (newPassword.isEmpty()) {
                            passwordError = "Password cannot be empty"
                            isValid = false
                        }
                        if (confirmPassword.isEmpty()) {
                            confirmPasswordError = "Confirm password cannot be empty"
                            isValid = false
                        }
                        if (newPassword != confirmPassword) {
                            confirmPasswordError = "Passwords do not match"
                            isValid = false
                        }

                        if (isValid) {
                            isLoading = true
                            passwordError = null
                            confirmPasswordError = null
                            
                            scope.launch {
                                try {
                                    val response = RetrofitClient.instance.resetPassword(
                                        ResetPasswordRequest(email, newPassword, confirmPassword)
                                    )
                                    if (response.isSuccessful) {
                                        android.widget.Toast.makeText(context, "Password Reset Successfully", android.widget.Toast.LENGTH_SHORT).show()
                                        onResetPasswordClick()
                                    } else {
                                        val errorMsg = response.errorBody()?.string()?.let {
                                            try {
                                                org.json.JSONObject(it).getString("message")
                                            } catch (e: Exception) {
                                                "Failed to reset password"
                                            }
                                        } ?: "Failed to reset password"
                                        passwordError = errorMsg
                                    }
                                } catch (e: Exception) {
                                    passwordError = "Network error: ${e.message}"
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
                        Text(text = "Reset Password", color = Color.White, fontWeight = FontWeight.SemiBold)
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

            // Copyright
            Text(
                text = "© 2026 DrugsSearch. All rights reserved.",
                style = MaterialTheme.typography.bodySmall,
                color = TextGrayColor
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResetPasswordScreenPreview() {
    drugssearchTheme {
        ResetPasswordScreen(email = "test@example.com")
    }
}
