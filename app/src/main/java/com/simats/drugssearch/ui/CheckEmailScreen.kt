package com.simats.drugssearch.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.R
import com.simats.drugssearch.ui.theme.drugssearchTheme
import com.simats.drugssearch.network.RetrofitClient
import com.simats.drugssearch.network.VerifyOtpRequest
import kotlinx.coroutines.launch

// Check Email Screen Colors
private val PrimaryBlue = Color(0xFF3B82F6)
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextDarkColor = Color(0xFF1E293B)
private val TextGrayColor = Color(0xFF64748B)
private val CardBorderColor = Color(0xFFE2E8F0)
private val ErrorColor = Color(0xFFEF4444)

@Composable
fun CheckEmailScreen(
    email: String = "your email",
    onVerifyOtpClick: (otp: String) -> Unit = {},
    onBackToLoginClick: () -> Unit = {},
    onResendOtpClick: () -> Unit = {}
) {
    var otpValue by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
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
                    onClick = onBackToLoginClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextDarkColor
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

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
            }
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(PrimaryBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Check your email",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = TextDarkColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "We've sent a 6-digit verification code to $email",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGrayColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            OtpInputField(
                otpValue = otpValue,
                onOtpChange = {
                    if (it.length <= 6) {
                        otpValue = it
                        otpError = null
                    }
                },
                isError = otpError != null
            )

            if (otpError != null) {
                Text(
                    text = otpError!!,
                    color = ErrorColor,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (otpValue.length != 6) {
                        otpError = "Please enter a valid 6-digit OTP"
                    } else {
                        isLoading = true
                        scope.launch {
                            try {
                                val response = RetrofitClient.instance.verifyOtp(VerifyOtpRequest(email, otpValue))
                                if (response.isSuccessful) {
                                    onVerifyOtpClick(otpValue)
                                } else {
                                    val errorMsg = response.errorBody()?.string()?.let {
                                        try {
                                            org.json.JSONObject(it).getString("message")
                                        } catch (e: Exception) {
                                            "Invalid OTP"
                                        }
                                    } ?: "Invalid OTP"
                                    otpError = errorMsg
                                }
                            } catch (e: Exception) {
                                otpError = "Network error: ${e.message}"
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
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Verify OTP", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Resend OTP",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.clickable {
                    if (!isLoading) {
                        isLoading = true
                        scope.launch {
                            try {
                                val response = RetrofitClient.instance.requestOtp(com.simats.drugssearch.network.RequestOtpRequest(email))
                                if (response.isSuccessful) {
                                    android.widget.Toast.makeText(context, "OTP Resent Successfully", android.widget.Toast.LENGTH_SHORT).show()
                                    onResendOtpClick()
                                } else {
                                    android.widget.Toast.makeText(context, "Failed to resend OTP: ${response.message()}", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(context, "Network Error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Back to Login",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextGrayColor,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.clickable { onBackToLoginClick() }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun OtpInputField(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    isError: Boolean = false
) {
    BasicTextField(
        value = otpValue,
        onValueChange = onOtpChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(6) { index ->
                    val char = otpValue.getOrNull(index)?.toString() ?: ""
                    val isFocused = otpValue.length == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(
                                width = if (isFocused) 2.dp else 1.dp,
                                color = when {
                                    isError -> ErrorColor
                                    isFocused -> PrimaryBlue
                                    char.isNotEmpty() -> PrimaryBlue.copy(alpha = 0.5f)
                                    else -> CardBorderColor
                                },
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkColor,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CheckEmailScreenPreview() {
    drugssearchTheme {
        CheckEmailScreen(email = "test@example.com")
    }
}
