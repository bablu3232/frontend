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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.ui.theme.DrugsSearchTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.simats.drugssearch.network.RetrofitClient
import com.simats.drugssearch.network.VerifyOtpRequest
import com.simats.drugssearch.network.RequestOtpRequest

// Verify Email Screen Colors
private val PrimaryBlue = Color(0xFF3B82F6)
private val LightBlue = Color(0xFFDDEAFF)
private val BackgroundColor = Color(0xFFF8FAFC)
private val CardBorderColor = Color(0xFFE2E8F0)
private val TextGrayColor = Color(0xFF64748B)
private val TextDarkColor = Color(0xFF1E293B)
private val DisclaimerBackground = Color(0xFFF1F5F9)
private val ErrorColor = Color(0xFFEF4444)

@Composable
fun VerifyEmailScreen(
    email: String = "your****@email.com",
    onBackClick: () -> Unit = {},
    onVerifyClick: (code: String) -> Unit = {},
    onResendClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    onTermsOfServiceClick: () -> Unit = {},
    onContactUsClick: () -> Unit = {}
) {
    var otpCode by remember { mutableStateOf("") }
    var timeRemaining by remember { mutableStateOf(120) } // 2 minutes in seconds
    var otpError by remember { mutableStateOf<String?>(null) }
    
    // Loading and error states
    var isLoading by remember { mutableStateOf(false) }
    var apiError by remember { mutableStateOf<String?>(null) }
    var verificationSuccess by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Countdown timer
    LaunchedEffect(key1 = timeRemaining) {
        if (timeRemaining > 0) {
            delay(1000L)
            timeRemaining--
        }
    }
    
    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val timerText = String.format("%02d:%02d", minutes, seconds)

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
            Spacer(modifier = Modifier.height(40.dp))

            // Email Icon Circle
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = LightBlue,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = "Email",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "Verify Your Email",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                color = TextDarkColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle
            Text(
                text = "We've sent a 6-digit code to",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp
                ),
                color = TextGrayColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Email
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                ),
                color = TextDarkColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // OTP Input Card
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
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // OTP Input Boxes
                    OtpInputField(
                        otpLength = 6,
                        otpValue = otpCode,
                        onOtpChange = { 
                            otpCode = it
                            if (otpError != null) otpError = null
                        },
                        hasError = otpError != null
                    )
                    
                    if (otpError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = otpError!!,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = ErrorColor
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
                            border = androidx.compose.foundation.BorderStroke(1.dp, ErrorColor.copy(alpha = 0.3f))
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
                    
                    // Verify Button
                    Button(
                        onClick = { 
                            if (otpCode.length != 6) {
                                otpError = "Please enter all 6 digits"
                            } else {
                                otpError = null
                                apiError = null
                                isLoading = true
                                
                                // Make API call in coroutine
                                scope.launch {
                                    try {
                                        val response = RetrofitClient.instance.verifyOtp(VerifyOtpRequest(email, otpCode))
                                        isLoading = false
                                        if (response.isSuccessful) {
                                            verificationSuccess = true
                                            onVerifyClick(otpCode)
                                        } else {
                                            apiError = "Invalid OTP or Expired"
                                        }
                                    } catch (e: Exception) {
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
                                text = "Verify",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                ),
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Resend Link
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Didn't receive the code?",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp
                            ),
                            color = TextDarkColor
                        )
                        Text(
                            text = "Resend",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = PrimaryBlue,
                            modifier = Modifier.clickable { 
                                if (timeRemaining == 0 && !isLoading) {
                                    isLoading = true
                                    scope.launch {
                                        try {
                                            val response = RetrofitClient.instance.requestOtp(RequestOtpRequest(email))
                                            isLoading = false
                                            if (response.isSuccessful) {
                                                android.widget.Toast.makeText(context, "OTP Resent", android.widget.Toast.LENGTH_SHORT).show()
                                                timeRemaining = 120
                                                onResendClick()
                                            } else {
                                                android.widget.Toast.makeText(context, "Failed to resend", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            isLoading = false
                                            android.widget.Toast.makeText(context, "Error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Timer
                    Text(
                        text = "Time remaining",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.sp
                        ),
                        color = TextGrayColor
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = timerText,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        ),
                        color = TextDarkColor
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
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

@Composable
private fun OtpInputField(
    otpLength: Int,
    otpValue: String,
    onOtpChange: (String) -> Unit,
    hasError: Boolean = false
) {
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }

    BasicTextField(
        value = otpValue,
        onValueChange = { value ->
            if (value.length <= otpLength && value.all { it.isDigit() }) {
                onOtpChange(value)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(otpLength) { index ->
                    val char = otpValue.getOrNull(index)?.toString() ?: ""
                    val isFocused = otpValue.length == index

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .border(
                                width = if (isFocused) 2.dp else 1.dp,
                                color = if (hasError) ErrorColor else if (isFocused) PrimaryBlue else CardBorderColor,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                            color = TextDarkColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VerifyEmailScreenPreview() {
    DrugsSearchTheme {
        VerifyEmailScreen(email = "test@example.com")
    }
}
