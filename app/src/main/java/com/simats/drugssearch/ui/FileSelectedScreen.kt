package com.simats.drugssearch.ui

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import com.simats.drugssearch.R
import com.simats.drugssearch.ui.theme.*

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.simats.drugssearch.network.RetrofitClient
import android.widget.Toast
import com.simats.drugssearch.ui.theme.DrugsSearchTheme
import android.graphics.Bitmap
import android.graphics.BitmapFactory

// File Selected Screen Colors
private val PrimaryBlue = Color(0xFF3B82F6)
private val LightBlue = Color(0xFFDDEAFF)
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextDarkColor = Color(0xFF1E293B)
private val TextGrayColor = Color(0xFF64748B)
private val CardBorderColor = Color(0xFFE2E8F0)
private val DisclaimerBackground = Color(0xFFF1F5F9)
private val GreenBg = Color(0xFFDCFCE7)
private val GreenColor = Color(0xFF22C55E)
private val GreenBorder = Color(0xFF86EFAC)

@Composable
fun FileSelectedScreen(
    userId: Int? = null,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onChooseDifferentFileClick: () -> Unit = {},
    onUploadSuccess: (Map<String, com.simats.drugssearch.network.DetectedParameter>, String, List<com.simats.drugssearch.ui.DrugRecommendation>, com.simats.drugssearch.network.PatientDetails?, Int?) -> Unit = { _, _, _, _, _ -> },
    onSearchClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var isFileUploaded by remember { mutableStateOf(false) }
    var selectedFileName by remember { mutableStateOf("") }
    var selectedFileSize by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            selectedFileUri = it
            // Get file name and size from URI
            context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    
                    if (nameIndex >= 0) {
                        selectedFileName = cursor.getString(nameIndex)
                    }
                    if (sizeIndex >= 0) {
                        val sizeBytes = cursor.getLong(sizeIndex)
                        selectedFileSize = formatFileSize(sizeBytes)
                    }
                }
            }
            isFileUploaded = true
        }
    }

    // Function to open file picker
    fun openFilePicker() {
        filePickerLauncher.launch(arrayOf(
            "application/pdf",
            "image/jpeg",
            "image/png",
            "image/heic",
            "image/heif"
        ))
    }


    Scaffold(
        bottomBar = {
            FileSelectedBottomNav(
                currentScreen = "Upload",
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
            FileSelectedTopBar(
                onBackClick = onBackClick,
                onHomeClick = onHomeClick
            )

            // File Size Note
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "For faster scanning, preferably upload files less than 1 MB.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        ),
                        color = Color(0xFF1E40AF)
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

                // File Status Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = if (isFileUploaded) GreenBorder else CardBorderColor,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isFileUploaded) GreenBg else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isFileUploaded) {
                            // Green Checkmark for Uploaded State
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Uploaded",
                                    tint = GreenColor,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        } else {
                            // Upload Icon for Initial State
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(LightBlue, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Upload,
                                    contentDescription = "Upload",
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (isFileUploaded) "File Uploaded" else "Upload File",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = TextDarkColor
                        )

                        if (isFileUploaded) {
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = selectedFileName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp
                                ),
                                color = TextGrayColor
                            )

                            Text(
                                text = selectedFileSize,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp
                                ),
                                color = TextGrayColor
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action to reset/change file
                            OutlinedButton(
                                onClick = {
                                    openFilePicker()
                                },
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text(
                                    text = "Choose Different File",
                                    color = TextDarkColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Select a report to upload and analyze",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp
                                ),
                                color = TextGrayColor,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Open File Picker
                            Button(
                                onClick = { openFilePicker() },
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                            ) {
                                Text(
                                    text = "Select File",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Supported Formats Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderColor, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Supported Formats",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = TextDarkColor
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "• PDF documents (.pdf)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = TextGrayColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Image files (JPG, PNG, HEIC)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = TextGrayColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Maximum file size: 10MB",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = TextGrayColor
                        )
                    }
                }



                // Final Action Button (Enabled only after selection)
                Button(
                    onClick = {
                        if (!isLoading && isFileUploaded && selectedFileUri != null && userId != null) {
                            isLoading = true
                            
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    var file = getFileFromUri(context, selectedFileUri!!, selectedFileName)
                                    if (file != null) {
                                        // Compress image files for faster OCR
                                        val lowerName = selectedFileName.lowercase()
                                        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") ||
                                            lowerName.endsWith(".png") || lowerName.endsWith(".heic") ||
                                            lowerName.endsWith(".heif")
                                        ) {
                                            val compressed = compressImageFile(context, file)
                                            if (compressed != null) {
                                                file = compressed
                                            }
                                        }
                                        
                                        // Use Kotlin extension functions for OkHttp 4 compatibility
                                        val mediaType = "multipart/form-data".toMediaTypeOrNull()
                                        val requestFile = file.asRequestBody(mediaType)
                                        val body = MultipartBody.Part.createFormData("report", file.name, requestFile)
                                        
                                        val textMediaType = "text/plain".toMediaTypeOrNull()
                                        val userIdBody = userId.toString().toRequestBody(textMediaType)

                                        val response = RetrofitClient.instance.uploadReportOcrSpace(userIdBody, body)
                                        
                                        withContext(Dispatchers.Main) {
                                            if (response.isSuccessful && response.body()?.status == "success") {
                                                val uploadResponse = response.body()!!
                                                val extractedTextJson = uploadResponse.extractedText ?: ""
                                                val reportId = uploadResponse.reportId // Capture reportId

                                                var values = emptyMap<String, com.simats.drugssearch.network.DetectedParameter>()
                                                var category = "General"
                                                var recommendations = mutableListOf<com.simats.drugssearch.ui.DrugRecommendation>()
                                                var patientDetails: com.simats.drugssearch.network.PatientDetails? = null

                                                var hasError = false
                                                if (extractedTextJson.isNotEmpty()) {
                                                    try {
                                                        // Check if the response is an error JSON
                                                        if (extractedTextJson.contains("\"error\"")) {
                                                            try {
                                                                val jsonObject = org.json.JSONObject(extractedTextJson)
                                                                if (jsonObject.has("error")) {
                                                                    val errorMessage = jsonObject.getString("error")
                                                                    withContext(Dispatchers.Main) {
                                                                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                                                                    }
                                                                    hasError = true
                                                                }
                                                            } catch (e: Exception) {
                                                                // Ignore and let Gson handle parsing exception
                                                            }
                                                        }

                                                        if (!hasError) {
                                                            val gson = com.google.gson.Gson()
                                                            val ocrResponse = gson.fromJson(extractedTextJson, com.simats.drugssearch.network.OcrResponse::class.java)
                                                            
                                                            category = ocrResponse.reportCategory ?: "General"
                                                            patientDetails = ocrResponse.patientDetails
                                                            
                                                            // Extract values from parameters map
                                                            values = ocrResponse.parameters ?: emptyMap()

                                                            // Extract recommendations
                                                            ocrResponse.parameters?.forEach { (name, detail) ->
                                                                if (detail.recommendation != null) {
                                                                    val rec = detail.recommendation
                                                                    val drugsList = rec.drugs?.split(",")?.map { it.trim() } ?: emptyList()
                                                                    
                                                                    // Assign a color based on status for now (red for high/low)
                                                                    val catColor = if (detail.status == "Normal") com.simats.drugssearch.ui.theme.GreenColor else com.simats.drugssearch.ui.theme.RedColor
                                                                    val catBg = if (detail.status == "Normal") com.simats.drugssearch.ui.theme.GreenBg else com.simats.drugssearch.ui.theme.RedBg

                                                                    recommendations.add(
                                                                        com.simats.drugssearch.ui.DrugRecommendation(
                                                                            parameterName = name, // Pass the parameter name (map key)
                                                                            name = rec.category ?: "General Recommendation",
                                                                            condition = detail.condition ?: "Abnormal Value",
                                                                            category = detail.category ?: "General",
                                                                            commonDrugs = drugsList,
                                                                            categoryColor = catColor,
                                                                            categoryBg = catBg
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                        }
                                                        
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                        withContext(Dispatchers.Main) {
                                                            isLoading = false
                                                            val preview = if (extractedTextJson.length > 60) extractedTextJson.substring(0, 60) + "..." else extractedTextJson
                                                            Toast.makeText(context, "Error parsing: $preview", Toast.LENGTH_LONG).show()
                                                        }
                                                        hasError = true
                                                    }
                                                }
                                                
                                                if (!hasError) {
                                                    withContext(Dispatchers.Main) {
                                                        isLoading = false
                                                        if (values.isEmpty()) {
                                                            Toast.makeText(
                                                                context,
                                                                "No parameters could be extracted from this report. Please try a clearer image or use manual entry.",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        } else {
                                                            Toast.makeText(context, "Analysis Complete!", Toast.LENGTH_SHORT).show()
                                                            onUploadSuccess(values, category, recommendations, patientDetails, reportId)
                                                        }
                                                    }
                                                }
                                            } else {
                                                withContext(Dispatchers.Main) {
                                                    isLoading = false
                                                    Toast.makeText(context, "Upload Failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    } else {
                                        withContext(Dispatchers.Main) {
                                            isLoading = false
                                            Toast.makeText(context, "Error processing file", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        isLoading = false
                                        Toast.makeText(context, "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else if (userId == null) {
                             Toast.makeText(context, "User not logged in!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = isFileUploaded && !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        disabledContainerColor = PrimaryBlue.copy(alpha = 0.5f)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Analyzing Report...",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            color = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Upload & Analyze Report",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tips Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderColor, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Tips for Best Results",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = TextDarkColor
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "• Ensure all test values are clearly visible",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = TextGrayColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Avoid blurry or low-quality images",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = TextGrayColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Include report header with patient info",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = TextGrayColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Make sure the report is complete",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = TextGrayColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Medical Disclaimer Card
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
                                text = "This platform is designed for educational and informational purposes only. It is not a substitute for professional medical advice, diagnosis, or treatment. Always seek the advice of your physician or other qualified health provider with any questions you may have regarding a medical condition.",
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
private fun FileSelectedTopBar(
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
}

@Composable
private fun FileSelectedBottomNav(
    @Suppress("UNUSED_PARAMETER") currentScreen: String,
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
fun FileSelectedScreenPreview() {
    DrugsSearchTheme {
        FileSelectedScreen()
    }
}

// Helper function to format file size
private fun formatFileSize(bytes: Long): String {
    return when {
        bytes >= 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
        bytes >= 1024 -> String.format("%.1f KB", bytes / 1024.0)
        else -> "$bytes B"
    }
}

// Helper function to get File from Uri
private fun getFileFromUri(context: Context, uri: Uri, originalName: String): File? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_upload_file_${System.currentTimeMillis()}_$originalName")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Helper function to compress image files for faster OCR upload
private fun compressImageFile(context: Context, originalFile: File): File? {
    return try {
        // Decode with inJustDecodeBounds first to get dimensions
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(originalFile.absolutePath, options)
        
        val origWidth = options.outWidth
        val origHeight = options.outHeight
        if (origWidth <= 0 || origHeight <= 0) return null
        
        // Calculate sample size to scale down to max 1500px
        val maxDim = 1500
        var sampleSize = 1
        if (origWidth > maxDim || origHeight > maxDim) {
            val halfWidth = origWidth / 2
            val halfHeight = origHeight / 2
            while ((halfWidth / sampleSize) >= maxDim || (halfHeight / sampleSize) >= maxDim) {
                sampleSize *= 2
            }
        }
        
        // Decode the actual bitmap with sample size
        val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath, decodeOptions) ?: return null
        
        // Compress to JPEG 80% quality
        val compressedFile = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
        FileOutputStream(compressedFile).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
        }
        bitmap.recycle()
        
        compressedFile
    } catch (e: Exception) {
        e.printStackTrace()
        null // Fall back to original file
    }
}
