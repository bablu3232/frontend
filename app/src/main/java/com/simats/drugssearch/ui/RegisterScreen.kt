package com.simats.drugssearch.ui

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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.R
import com.simats.drugssearch.ui.theme.DrugsSearchTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.simats.drugssearch.network.RegisterRequest
import com.simats.drugssearch.network.RetrofitClient
import androidx.compose.foundation.BorderStroke

// Register Screen Colors
private val PrimaryBlue = Color(0xFF3B82F6)
private val BackgroundColor = Color(0xFFF8FAFC)
private val CardBorderColor = Color(0xFFE2E8F0)
private val TextGrayColor = Color(0xFF64748B)
private val TextDarkColor = Color(0xFF1E293B)
private val InputBorderColor = Color(0xFFE2E8F0)
private val DisclaimerBackground = Color(0xFFF1F5F9)
private val ErrorColor = Color(0xFFEF4444)

// Validation functions
private fun isValidEmail(email: String): Boolean {
    val emailPattern = android.util.Patterns.EMAIL_ADDRESS
    // Strict validation: Must be a valid email format AND contain "gmail" (assuming @gmail.com)
    return email.isNotEmpty() && emailPattern.matcher(email).matches() && email.lowercase().contains("gmail")
}

private fun isValidName(name: String): Boolean {
    // Name must allow only letters and spaces and be rigidly > 4 characters (min 5)
    val namePattern = "^[a-zA-Z\\s]{5,}$".toRegex()
    return name.isNotEmpty() && namePattern.matches(name)
}

private fun isValidPhone(phone: String): Boolean {
    // Phone must be exactly 10 digits
    val phonePattern = "^[0-9]{10}$".toRegex()
    return phone.isNotEmpty() && phonePattern.matches(phone)
}

private fun isValidPassword(password: String): Boolean {
    // Password: Min 8 chars, allows letters, numbers, and special characters
    val lengthValid = password.length >= 8
    val hasNoWhitespace = !password.contains("\\s".toRegex())
    return password.isNotEmpty() && lengthValid && hasNoWhitespace
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBackClick: () -> Unit = {},
    onCreateAccountClick: (fullName: String, email: String, phone: String, password: String) -> Unit = { _, _, _, _ -> },
    onLoginClick: () -> Unit = {},
    onTermsOfServiceClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    onContactUsClick: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var agreeToTerms by remember { mutableStateOf(false) }
    
    // Loading and error states
    var isLoading by remember { mutableStateOf(false) }
    var apiError by remember { mutableStateOf<String?>(null) }
    var registrationSuccess by remember { mutableStateOf(false) }
    
    // Error states
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var termsError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)

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
            Column {
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
            Spacer(modifier = Modifier.height(24.dp))

            // Create Account Title
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = TextDarkColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Sign up to get started",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp
                ),
                color = TextGrayColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Registration Form Card
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
                    // Full Name Label
                    Text(
                        text = "Full Name",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        ),
                        color = TextDarkColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Full Name Input
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { 
                            fullName = it
                            if (fullNameError != null) fullNameError = null
                        },
                        placeholder = {
                            Text(
                                text = "Enter your full name",
                                color = TextGrayColor.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = if (fullNameError != null) ErrorColor else PrimaryBlue,
                            unfocusedBorderColor = if (fullNameError != null) ErrorColor else InputBorderColor,
                            cursorColor = PrimaryBlue
                        ),
                        singleLine = true,
                        isError = fullNameError != null
                    )
                    
                    if (fullNameError != null) {
                        Text(
                            text = fullNameError!!,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = ErrorColor,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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
                            cursorColor = PrimaryBlue
                        ),
                        singleLine = true,
                        isError = emailError != null
                    )
                    
                    if (emailError != null) {
                        Text(
                            text = emailError!!,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = ErrorColor,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Phone Number Label
                    Text(
                        text = "Phone Number",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        ),
                        color = TextDarkColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Phone Number Input
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { 
                            phoneNumber = it
                            if (phoneError != null) phoneError = null
                        },
                        placeholder = {
                            Text(
                                text = "+1 (555) 000-0000",
                                color = TextGrayColor.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = if (phoneError != null) ErrorColor else PrimaryBlue,
                            unfocusedBorderColor = if (phoneError != null) ErrorColor else InputBorderColor,
                            cursorColor = PrimaryBlue
                        ),
                        singleLine = true,
                        isError = phoneError != null
                    )
                    
                    if (phoneError != null) {
                        Text(
                            text = phoneError!!,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = ErrorColor,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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
                            if (passwordError != null) passwordError = null
                        },
                        placeholder = {
                            Text(
                                text = "Create a password",
                                color = TextGrayColor.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = if (passwordError != null) ErrorColor else PrimaryBlue,
                            unfocusedBorderColor = if (passwordError != null) ErrorColor else InputBorderColor,
                            cursorColor = PrimaryBlue
                        ),
                        singleLine = true,
                        isError = passwordError != null,
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
                    
                    if (passwordError != null) {
                        Text(
                            text = passwordError!!,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = ErrorColor,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Password Label
                    Text(
                        text = "Confirm Password",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        ),
                        color = TextDarkColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Confirm Password Input
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { 
                            confirmPassword = it
                            if (confirmPasswordError != null) confirmPasswordError = null
                        },
                        placeholder = {
                            Text(
                                text = "Confirm your password",
                                color = TextGrayColor.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = if (confirmPasswordError != null) ErrorColor else PrimaryBlue,
                            unfocusedBorderColor = if (confirmPasswordError != null) ErrorColor else InputBorderColor,
                            cursorColor = PrimaryBlue
                        ),
                        singleLine = true,
                        isError = confirmPasswordError != null,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                    tint = TextGrayColor
                                )
                            }
                        }
                    )
                    
                    if (confirmPasswordError != null) {
                        Text(
                            text = confirmPasswordError!!,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = ErrorColor,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Terms Agreement
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Checkbox(
                            checked = agreeToTerms,
                            onCheckedChange = { 
                                agreeToTerms = it
                                if (termsError != null) termsError = null
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = PrimaryBlue,
                                uncheckedColor = if (termsError != null) ErrorColor else InputBorderColor
                            ),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(color = TextDarkColor)) {
                                    append("I agree to the ")
                                }
                                withStyle(style = SpanStyle(color = PrimaryBlue, fontWeight = FontWeight.Medium)) {
                                    append("Terms of Service")
                                }
                                withStyle(style = SpanStyle(color = TextDarkColor)) {
                                    append(" and ")
                                }
                                withStyle(style = SpanStyle(color = PrimaryBlue, fontWeight = FontWeight.Medium)) {
                                    append("Privacy Policy")
                                }
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        )
                    }

                    // Terms Error
                    if (termsError != null) {
                        Text(
                            text = termsError!!,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = ErrorColor,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // API Error Message
                    if (apiError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
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
                                    fontSize = 13.sp,
                                    color = ErrorColor
                                ),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // Create Account Button
                    Button(
                        onClick = { 
                            // Clear all errors first
                            fullNameError = null
                            emailError = null
                            phoneError = null
                            passwordError = null
                            confirmPasswordError = null
                            termsError = null
                            apiError = null
                            
                            var hasError = false
                            
                            // Validate full name
                            if (fullName.trim().isEmpty()) {
                                fullNameError = "Please enter your full name"
                                hasError = true
                            } else if (!isValidName(fullName.trim())) {
                                fullNameError = "Name must be more than 4 characters (letters/spaces only)"
                                hasError = true
                            }
                            
                            // Validate email
                            if (!isValidEmail(email)) {
                                emailError = "Please enter a valid Gmail address (e.g., user@gmail.com)"
                                hasError = true
                            }
                            
                            // Validate phone
                            if (!isValidPhone(phoneNumber)) {
                                phoneError = "Please enter a valid 10-digit phone number"
                                hasError = true
                            }
                            
                            // Validate password
                            if (!isValidPassword(password)) {
                                passwordError = "Password must be at least 8 characters with no spaces"
                                hasError = true
                            }
                            
                            // Validate confirm password
                            if (confirmPassword != password) {
                                confirmPasswordError = "Passwords do not match"
                                hasError = true
                            } else if (confirmPassword.isEmpty()) {
                                confirmPasswordError = "Please confirm your password"
                                hasError = true
                            }
                            
                            // Validate terms agreement
                            if (!agreeToTerms) {
                                termsError = "You must agree to the Terms of Service"
                                hasError = true
                            }
                            
                            // If no errors, proceed with API call
                            if (!hasError) {
                                isLoading = true
                                apiError = null
                                
                                // Make API call in coroutine
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val response = RetrofitClient.instance.registerUser(
                                            RegisterRequest(
                                                fullName = fullName.trim(),
                                                email = email.trim(),
                                                phone = phoneNumber.trim(),
                                                password = password.trim(),
                                                confirmPassword = confirmPassword.trim()
                                            )
                                        )
                                        
                                        withContext(Dispatchers.Main) {
                                            isLoading = false
                                            if (response.isSuccessful) {
                                                registrationSuccess = true
                                                // Call the original callback for navigation
                                                onCreateAccountClick(fullName, email, phoneNumber, password)
                                            } else {
                                                apiError = "Registration failed: ${response.message()}"
                                                // You could parse error body here if needed
                                            }
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            isLoading = false
                                            apiError = "Network error: ${e.message}"
                                        }
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
                                text = "Create Account",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                ),
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Login Link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Already have an\naccount?",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp
                            ),
                            color = TextDarkColor,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Login",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = PrimaryBlue,
                            modifier = Modifier.clickable { onLoginClick() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Copyright
            Text(
                text = "© 2026 DrugsSearch. All rights reserved.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp
                ),
                color = TextGrayColor
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Retrofit logic is now used directly in the composable

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    DrugsSearchTheme {
        RegisterScreen()
    }
}
