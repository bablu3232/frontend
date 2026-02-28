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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.ui.theme.DrugsSearchTheme

// Liver Function Entry Screen Colors
private val PrimaryBlue = Color(0xFF3B82F6)
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextDarkColor = Color(0xFF1E293B)
private val TextGrayColor = Color(0xFF64748B)
private val CardBorderColor = Color(0xFFE2E8F0)
private val DisclaimerBackground = Color(0xFFF1F5F9)
private val WarningBackground = Color(0xFFFFFBEB)
private val WarningBorder = Color(0xFFFDE68A)
private val WarningText = Color(0xFFB45309)

data class LiverFunctionValues(
    val sgot: String = "",
    val sgpt: String = "",
    val alp: String = "",
    val totalBilirubin: String = "",
    val directBilirubin: String = "",
    val albumin: String = "",
    val totalProtein: String = "",
    val ggt: String = ""
)

@Composable
fun LiverFunctionEntryScreen(
    initialValues: LiverFunctionValues = LiverFunctionValues(),
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSubmitClick: (LiverFunctionValues) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var sgot by remember { mutableStateOf(initialValues.sgot) }
    var sgpt by remember { mutableStateOf(initialValues.sgpt) }
    var alp by remember { mutableStateOf(initialValues.alp) }
    var totalBilirubin by remember { mutableStateOf(initialValues.totalBilirubin) }
    var directBilirubin by remember { mutableStateOf(initialValues.directBilirubin) }
    var albumin by remember { mutableStateOf(initialValues.albumin) }
    var totalProtein by remember { mutableStateOf(initialValues.totalProtein) }
    var ggt by remember { mutableStateOf(initialValues.ggt) }

    val isFormValid = sgot.isNotBlank() && 
                      sgpt.isNotBlank() && 
                      alp.isNotBlank() && 
                      totalBilirubin.isNotBlank()

    Scaffold(
        bottomBar = {
            LiverFunctionBottomNav(
                onHomeClick = onHomeClick,
                onUploadClick = { },
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
            LiverFunctionTopBar(
                onBackClick = onBackClick,
                onHomeClick = onHomeClick
            )

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = "Liver Function",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = TextDarkColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Enter your liver function test values",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp
                    ),
                    color = TextGrayColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Form Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // SGOT / AST
                        LiverFunctionTextField(
                            label = "SGOT / AST (U/L) *",
                            value = sgot,
                            onValueChange = { sgot = it },
                            placeholder = "e.g., 25",
                            helperText = "Normal: 10–40 U/L"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // SGPT / ALT
                        LiverFunctionTextField(
                            label = "SGPT / ALT (U/L) *",
                            value = sgpt,
                            onValueChange = { sgpt = it },
                            placeholder = "e.g., 30",
                            helperText = "Normal: 7–56 U/L"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // ALP
                        LiverFunctionTextField(
                            label = "ALP (U/L) *",
                            value = alp,
                            onValueChange = { alp = it },
                            placeholder = "e.g., 80",
                            helperText = "Normal: 44–147 U/L"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Total Bilirubin
                        LiverFunctionTextField(
                            label = "Total Bilirubin (mg/dL) *",
                            value = totalBilirubin,
                            onValueChange = { totalBilirubin = it },
                            placeholder = "e.g., 0.8",
                            helperText = "Normal: 0.1–1.2 mg/dL"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Direct Bilirubin (Optional)
                        LiverFunctionTextField(
                            label = "Direct Bilirubin (mg/dL)",
                            value = directBilirubin,
                            onValueChange = { directBilirubin = it },
                            placeholder = "e.g., 0.2",
                            helperText = "Normal: 0.0–0.3 mg/dL",
                            isOptional = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Albumin (Optional)
                        LiverFunctionTextField(
                            label = "Albumin (g/dL)",
                            value = albumin,
                            onValueChange = { albumin = it },
                            placeholder = "e.g., 4.2",
                            helperText = "Normal: 3.5–5.5 g/dL",
                            isOptional = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Total Protein (Optional)
                        LiverFunctionTextField(
                            label = "Total Protein (g/dL)",
                            value = totalProtein,
                            onValueChange = { totalProtein = it },
                            placeholder = "e.g., 7.0",
                            helperText = "Normal: 6.0–8.3 g/dL",
                            isOptional = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // GGT (Optional)
                        LiverFunctionTextField(
                            label = "GGT (U/L)",
                            value = ggt,
                            onValueChange = { ggt = it },
                            placeholder = "e.g., 25",
                            helperText = "Normal: 0–45 U/L",
                            isOptional = true
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Required fields note
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, WarningBorder, RoundedCornerShape(8.dp)),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = WarningBackground),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = "* Required fields must be filled to submit",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp
                                ),
                                color = WarningText,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onBackClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Back",
                                    fontWeight = FontWeight.Medium,
                                    color = TextDarkColor
                                )
                            }

                            Button(
                                onClick = {
                                    onSubmitClick(
                                        LiverFunctionValues(
                                            sgot = sgot,
                                            sgpt = sgpt,
                                            alp = alp,
                                            totalBilirubin = totalBilirubin,
                                            directBilirubin = directBilirubin,
                                            albumin = albumin,
                                            totalProtein = totalProtein,
                                            ggt = ggt
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                enabled = isFormValid,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryBlue,
                                    disabledContainerColor = CardBorderColor
                                )
                            ) {
                                Text(
                                    text = "Submit",
                                    fontWeight = FontWeight.Medium,
                                    color = if (isFormValid) Color.White else TextGrayColor
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Medical Disclaimer
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DisclaimerBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                                text = "This platform is designed for educational and informational purposes only. It is not a substitute for professional medical advice, diagnosis, or treatment.",
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
            }
        }
    }
}

@Composable
private fun LiverFunctionTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    helperText: String,
    isOptional: Boolean = false
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = TextDarkColor
            )
            if (isOptional) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Optional",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp
                    ),
                    color = TextGrayColor
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = TextGrayColor.copy(alpha = 0.6f)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = CardBorderColor,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = helperText,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp
            ),
            color = TextGrayColor
        )
    }
}

@Composable
private fun LiverFunctionTopBar(
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

@Composable
private fun LiverFunctionBottomNav(
    onHomeClick: () -> Unit,
    onUploadClick: () -> Unit,
    onSearchClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = false,
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                unselectedIconColor = TextDarkColor,
                                     unselectedTextColor = TextDarkColor,
                                     indicatorColor = Color.White
            )
        )
        NavigationBarItem(
            selected = true,
            onClick = onUploadClick,
            icon = { Icon(Icons.Default.Upload, "Upload") },
            label = { Text("Upload") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                unselectedIconColor = TextDarkColor,
                                     unselectedTextColor = TextDarkColor,
                                     indicatorColor = Color.White
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onSearchClick,
            icon = { Icon(Icons.Default.Search, "Search") },
            label = { Text("Search") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                unselectedIconColor = TextDarkColor,
                                     unselectedTextColor = TextDarkColor,
                                     indicatorColor = Color.White
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onHistoryClick,
            icon = { Icon(Icons.Default.History, "History") },
            label = { Text("History") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                unselectedIconColor = TextDarkColor,
                                     unselectedTextColor = TextDarkColor,
                                     indicatorColor = Color.White
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onProfileClick,
            icon = { Icon(Icons.Default.Person, "Profile") },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                unselectedIconColor = TextDarkColor,
                                     unselectedTextColor = TextDarkColor,
                                     indicatorColor = Color.White
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LiverFunctionEntryScreenPreview() {
    DrugsSearchTheme {
        LiverFunctionEntryScreen()
    }
}
