package com.simats.drugssearch.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.R
import com.simats.drugssearch.network.RetrofitClient
import com.simats.drugssearch.network.UpdateProfileRequest
import com.simats.drugssearch.ui.theme.DrugsSearchTheme
import kotlinx.coroutines.launch
import java.util.Calendar

// Colors
private val PrimaryBlue = Color(0xFF2196F3)
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextDarkColor = Color(0xFF1E293B)
private val TextGrayColor = Color(0xFF64748B)
private val CardBorderColor = Color(0xFFE2E8F0)

@Composable
fun PersonalInformationScreen(
    userId: Int,
    initialName: String = "John Doe",
    initialEmail: String = "john.doe@email.com",
    initialPhone: String = "+1 (555) 123-4567",
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSaveClick: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onCancelClick: () -> Unit = {}
) {
    var name by remember { mutableStateOf(initialName) }
    var email by remember { mutableStateOf(initialEmail) }
    var phone by remember { mutableStateOf(initialPhone) }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var apiMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PersonalInformationTopBar(
                onBackClick = onBackClick,
                onHomeClick = onHomeClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Edit Personal Information",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = TextDarkColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Update your profile details",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextGrayColor
                    ),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Full Name
                        ProfileInputField(
                            label = "Full Name",
                            value = name,
                            onValueChange = { name = it }
                        )

                        // Email
                        ProfileInputField(
                            label = "Email",
                            value = email,
                            onValueChange = { email = it }
                        )

                        // Phone Number
                        ProfileInputField(
                            label = "Phone Number",
                            value = phone,
                            onValueChange = { phone = it }
                        )

                        // Date of Birth
                        val calendar = Calendar.getInstance()
                        val year = calendar.get(Calendar.YEAR)
                        val month = calendar.get(Calendar.MONTH)
                        val day = calendar.get(Calendar.DAY_OF_MONTH)

                        val datePickerDialog = DatePickerDialog(
                            context,
                            { _, selectedYear, selectedMonth, selectedDay ->
                                dob = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                            },
                            year, month, day
                        )

                        Text(
                            text = "Date of Birth",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextGrayColor,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        OutlinedTextField(
                            value = dob,
                            onValueChange = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { datePickerDialog.show() },
                            shape = RoundedCornerShape(12.dp),
                            enabled = false,
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Select Date",
                                    modifier = Modifier.clickable { datePickerDialog.show() }
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = TextDarkColor,
                                disabledBorderColor = CardBorderColor,
                                disabledPlaceholderColor = TextGrayColor,
                                disabledLeadingIconColor = TextGrayColor,
                                disabledTrailingIconColor = TextGrayColor,
                                disabledContainerColor = Color.White
                            ),
                            singleLine = true
                        )

                        // Gender
                        Text(
                            text = "Gender",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextGrayColor,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = gender == "Male",
                                onClick = { gender = "Male" },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                            )
                            Text(
                                text = "Male",
                                modifier = Modifier.clickable { gender = "Male" },
                                color = TextDarkColor
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            RadioButton(
                                selected = gender == "Female",
                                onClick = { gender = "Female" },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                            )
                            Text(
                                text = "Female",
                                modifier = Modifier.clickable { gender = "Female" },
                                color = TextDarkColor
                            )
                            Spacer(modifier = Modifier.width(16.dp))

                            RadioButton(
                                selected = gender == "Other",
                                onClick = { gender = "Other" },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                            )
                            Text(
                                text = "Other",
                                modifier = Modifier.clickable { gender = "Other" },
                                color = TextDarkColor
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = onCancelClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE2E8F0)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isLoading
                            ) {
                                Text(
                                    text = "Cancel",
                                    color = TextDarkColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Button(
                                onClick = { 
                                    isLoading = true
                                    apiMessage = null
                                    scope.launch {
                                        try {
                                            // Parse the display date (dd/MM/yyyy)
                                            // Let's assume the API expects YYYY-MM-DD.
                                            val parts = dob.split("/")
                                            val apiDob = if (parts.size == 3) {
                                                "${parts[2]}-${parts[1].padStart(2, '0')}-${parts[0].padStart(2, '0')}"
                                            } else {
                                                dob
                                            }

                                            if (userId == 0) {
                                                apiMessage = "Error: Invalid User ID. Please log in again."
                                                isLoading = false
                                                return@launch
                                            }
                                            
                                            val response = RetrofitClient.instance.updateProfile(
                                                UpdateProfileRequest(
                                                    user_id = userId,
                                                    name = name,
                                                    email = email,
                                                    phone = phone,
                                                    dob = apiDob,
                                                    gender = gender
                                                )
                                            )
                                            
                                            if (response.isSuccessful) {
                                                android.widget.Toast.makeText(context, "Profile Updated Successfully", android.widget.Toast.LENGTH_SHORT).show()
                                                onSaveClick(name, email, phone, dob, gender)
                                            } else {
                                                apiMessage = response.body()?.message ?: "Failed to update profile"
                                                if (apiMessage == null && response.errorBody() != null) {
                                                    apiMessage = "Error: ${response.message()}"
                                                }
                                            }
                                        } catch (e: Exception) {
                                            apiMessage = "Network error: ${e.message}"
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryBlue
                                ),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Text(
                                        text = "Save Changes",
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        if (apiMessage != null) {
                            Text(
                                text = apiMessage!!,
                                color = Color.Red,
                                fontSize = 14.sp,
                                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ProfileInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextGrayColor,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = CardBorderColor,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = TextDarkColor,
                unfocusedTextColor = TextDarkColor
            ),
            singleLine = true
        )
    }
}

@Composable
private fun PersonalInformationTopBar(
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
                text = "DrugSearch",
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PersonalInformationScreenPreview() {
    DrugsSearchTheme {
        PersonalInformationScreen(userId = 1)
    }
}
