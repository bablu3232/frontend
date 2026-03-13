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
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.R
import com.simats.drugssearch.ui.theme.DrugsSearchTheme
import com.simats.drugssearch.network.RequestOtpRequest
import com.simats.drugssearch.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Forgot Password Screen Colors
private val PrimaryBlue = Color(0xFF3B82F6)
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextDarkColor = Color(0xFF1E293B)
private val TextGrayColor = Color(0xFF64748B)
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
fun ForgotPasswordScreen(
    onBackClick: () -> Unit = {},
    onSendOtpClick: (email: String) -> Unit = {},
    onBackToLoginClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var apiError by remember { mutableStateOf<String?>(null) }
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

            // Back Button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .background(Color.White, CircleShape)
                        .size(40.dp)
                        .border(1.dp, InputBorderColor, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextDarkColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(PrimaryBlue.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Forgot Password?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                color = TextDarkColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Enter your email address and we'll send you instructions to reset your password.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    lineHeight = 24.sp
                ),
                color = TextGrayColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Email Address",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = TextDarkColor,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Email Input
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    if (emailError != null) emailError = null
                    apiError = null
                },
                placeholder = {
                    Text(
                        text = "john.doe@email.com",
                        color = TextGrayColor.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Black.copy(alpha = 0.6f),
                    focusedBorderColor = if (emailError != null) ErrorColor else PrimaryBlue,
                    unfocusedBorderColor = if (emailError != null) ErrorColor else InputBorderColor,
                    disabledBorderColor = InputBorderColor,
                    disabledLeadingIconColor = TextGrayColor.copy(alpha = 0.6f),
                    disabledTrailingIconColor = TextGrayColor.copy(alpha = 0.6f),
                    disabledLabelColor = TextGrayColor.copy(alpha = 0.6f),
                    disabledPlaceholderColor = TextGrayColor.copy(alpha = 0.6f),
                    cursorColor = PrimaryBlue
                ),
                singleLine = true,
                isError = emailError != null || apiError != null,
                enabled = !isLoading
            )
            
            if (emailError != null || apiError != null) {
                Text(
                    text = emailError ?: apiError!!,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = ErrorColor,
                    modifier = Modifier.fillMaxWidth().padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Send OTP Button
            Button(
                onClick = { 
                    if (!isValidEmail(email)) {
                        emailError = "Please enter a valid email address"
                    } else {
                        emailError = null
                        apiError = null
                        isLoading = true
                        
                        scope.launch {
                            try {
                                val response = RetrofitClient.instance.requestOtp(RequestOtpRequest(email))
                                if (response.isSuccessful) {
                                    android.widget.Toast.makeText(context, "OTP Sent Successfully", android.widget.Toast.LENGTH_SHORT).show()
                                    onSendOtpClick(email)
                                } else {
                                    val errorMsg = response.errorBody()?.string()?.let {
                                        try {
                                            org.json.JSONObject(it).getString("message")
                                        } catch (e: Exception) {
                                            "Failed to send OTP"
                                        }
                                    } ?: "Failed to send OTP"
                                    apiError = errorMsg
                                }
                            } catch (e: Exception) {
                                apiError = "Network Error: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    disabledContainerColor = PrimaryBlue.copy(alpha = 0.6f),
                    disabledContentColor = Color.White
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Send OTP",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Back to Login
            Text(
                text = "Back to Login",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = TextDarkColor,
                modifier = Modifier.clickable { onBackToLoginClick() }
            )

            Spacer(modifier = Modifier.height(40.dp))

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

            // Copyright
            Text(
                text = "2026 © Powered by SIMATS Engineering",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp
                ),
                color = TextGrayColor
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordScreenPreview() {
    DrugsSearchTheme {
        ForgotPasswordScreen()
    }
}
