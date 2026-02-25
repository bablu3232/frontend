package com.simats.drugssearch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import com.simats.drugssearch.R
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.ui.theme.DrugsSearchTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.simats.drugssearch.network.LoginRequest
import com.simats.drugssearch.network.RetrofitClient

import androidx.compose.foundation.BorderStroke

// Login Screen Colors
private val PrimaryBlue = Color(0xFF3B82F6)
private val BackgroundColor = Color(0xFFF8FAFC)
private val CardBorderColor = Color(0xFFE2E8F0)
private val TextGrayColor = Color(0xFF64748B)
private val TextDarkColor = Color(0xFF1E293B)
private val InputBorderColor = Color(0xFFE2E8F0)
private val DisclaimerBackground = Color(0xFFF1F5F9)
private val ErrorColor = Color(0xFFEF4444)

// Email validation function
private fun isValidEmail(email: String): Boolean {
    val emailPattern = android.util.Patterns.EMAIL_ADDRESS
    return email.isNotEmpty() && emailPattern.matcher(email).matches()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onBackClick: () -> Unit = {},
    onLoginSuccess: (userId: Int, fullName: String, email: String, phone: String, dob: String, gender: String) -> Unit = { _, _, _, _, _, _ -> },
    onForgotPasswordClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    onTermsOfServiceClick: () -> Unit = {},
    onContactUsClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var apiError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .statusBarsPadding() // Respects the system status bar
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
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

            Spacer(modifier = Modifier.width(12.dp))

        // App Logo
        // App Logo
        Image(
            painter = painterResource(id = R.drawable.logo_drugsearch),
            contentDescription = "Logo",
            modifier = Modifier
                .size(60.dp) // Updated to 60dp
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(4.dp))

            // App Title
            Column {
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
        }

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Welcome Back Title
            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = TextDarkColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Login to your account",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp
                ),
                color = TextGrayColor
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Login Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = CardBorderColor,
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Email Label
                    Text(
                        text = "Email",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        ),
                        color = TextDarkColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Email Input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { 
                            email = it
                            if (emailError != null) emailError = null
                            if (apiError != null) apiError = null
                        },
                        placeholder = {
                            Text(
                                text = "Enter your email",
                                color = TextGrayColor.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = if (emailError != null) ErrorColor else PrimaryBlue,
                            unfocusedBorderColor = if (emailError != null) ErrorColor else InputBorderColor,
                            cursorColor = PrimaryBlue,
                            errorBorderColor = ErrorColor
                        ),
                        singleLine = true,
                        isError = emailError != null
                    )
                    
                    // Email Error Message
                    if (emailError != null) {
                        Text(
                            text = emailError!!,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp
                            ),
                            color = ErrorColor,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Password Label
                    Text(
                        text = "Password",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        ),
                        color = TextDarkColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            if (apiError != null) apiError = null
                        },
                        placeholder = {
                            Text(
                                text = "Enter your password",
                                color = TextGrayColor.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = InputBorderColor,
                            cursorColor = PrimaryBlue
                        ),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = TextGrayColor
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Remember Me & Forgot Password Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Remember Me Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { rememberMe = !rememberMe }
                        ) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = PrimaryBlue,
                                    uncheckedColor = InputBorderColor
                                ),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Remember me",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp
                                ),
                                color = TextDarkColor
                            )
                        }

                        // Forgot Password
                        Text(
                            text = "Forgot password?",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = PrimaryBlue,
                            modifier = Modifier.clickable { onForgotPasswordClick() }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // API Error Message
                    if (apiError != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = ErrorColor.copy(alpha = 0.1f)
                            ),
                            border = BorderStroke(1.dp, ErrorColor.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = apiError!!,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 13.sp
                                ),
                                color = ErrorColor,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Login Button
                    Button(
                        onClick = { 
                            emailError = null
                            apiError = null
                            if (!isValidEmail(email)) {
                                emailError = "Please enter a valid email address"
                                return@Button
                            }
                            if (password.isEmpty()) {
                                emailError = "Please enter your password"
                                return@Button
                            }
                            isLoading = true
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val response = RetrofitClient.instance.loginUser(LoginRequest(email.trim(), password))
                                    withContext(Dispatchers.Main) {
                                        isLoading = false
                                        if (response.isSuccessful && response.body() != null) {
                                            val body = response.body()!!
                                            // Assume successful login if we get a valid response
                                            // You might want to store the token/user_id here
                                            onLoginSuccess(body.userId ?: 0, body.fullName ?: "", body.email ?: email, body.phone ?: "", body.dob ?: "", body.gender ?: "")
                                        } else {
                                            val errorBody = response.errorBody()?.string()
                                            // simple parsing of error message from JSON manually or via simple string check if gson not set up for error
                                            // A simple regex or string check for "message"
                                            apiError = "Login failed: ${response.message()}"
                                        }
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        isLoading = false
                                        apiError = "Network error: ${e.message}"
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLoading) PrimaryBlue.copy(alpha = 0.6f) else PrimaryBlue
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Login",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                ),
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Register Link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Don't have an account?",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp
                            ),
                            color = TextDarkColor
                        )
                        Text(
                            text = "Register",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = PrimaryBlue,
                            modifier = Modifier.clickable { onRegisterClick() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Medical Disclaimer
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
                            text = "This application is designed for educational and informational purposes only. It is not a substitute for professional medical advice, diagnosis, or treatment. Always seek the advice of your physician or other qualified health provider with any questions you may have regarding a medical condition.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            ),
                            color = TextGrayColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer Links
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
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = PrimaryBlue,
                    modifier = Modifier.clickable { onTermsOfServiceClick() }
                )
                Text(
                    text = "  •  ",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = TextGrayColor
                )
                Text(
                    text = "Contact Us",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = PrimaryBlue,
                    modifier = Modifier.clickable { onContactUsClick() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Copyright
            Text(
                text = "© 2026 DrugSearch. All rights reserved.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp
                ),
                color = TextGrayColor
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// API response for login
// Retrofit logic is now used directly in the composable


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    DrugsSearchTheme {
        LoginScreen()
    }
}
