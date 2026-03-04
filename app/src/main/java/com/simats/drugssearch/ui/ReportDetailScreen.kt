package com.simats.drugssearch.ui

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import com.simats.drugssearch.R

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.ui.theme.drugssearchTheme
import com.simats.drugssearch.network.ReportParameter
import android.graphics.pdf.PdfDocument
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

// Colors matching DrugsSearch design system
private val PrimaryBlue = Color(0xFF2196F3)
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextDarkColor = Color(0xFF1E293B)
private val TextGrayColor = Color(0xFF64748B)
private val CardBorderColor = Color(0xFFE2E8F0)
private val GreenColor = Color(0xFF10B981)
private val RedColor = Color(0xFFEF4444)
private val LightGreenBg = Color(0xFFECFDF5)
private val LightRedBg = Color(0xFFFEF2F2)

@Composable
fun ReportDetailScreen(
    report: HealthReport,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onNavigationHomeClick: () -> Unit = {},
    onUploadClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val context = LocalContext.current
    Scaffold(
        bottomBar = {
            DrugSearchBottomNav(
                selectedTab = "History",
                onHomeClick = onNavigationHomeClick,
                onUploadClick = onUploadClick,
                onSearchClick = onSearchClick,
                onHistoryClick = {},
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
            ReportDetailTopBar(
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${report.type} Report",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                            color = TextDarkColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = report.date,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = TextGrayColor
                        )
                    }
                    
                    IconButton(onClick = {
                        try {
                            val file = generateReportPdf(context, report)
                            Toast.makeText(context, "PDF saved: ${file.name}", Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, "Failed to export PDF", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.Download, contentDescription = "Download", tint = PrimaryBlue)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Status Card
                StatusSummaryCard(
                    isNormal = report.isNormal,
                    parameterCount = report.parameters.size,
                    abnormalCount = report.abnormalCount
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Test Results Card
                TestResultsCard(parameters = report.parameters)

                Spacer(modifier = Modifier.height(32.dp))

                // Medical Disclaimer
                MedicalDisclaimerCard()

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ReportDetailTopBar(
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
private fun StatusSummaryCard(
    isNormal: Boolean,
    parameterCount: Int,
    abnormalCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isNormal) LightGreenBg else LightRedBg
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isNormal) GreenColor.copy(alpha = 0.15f) else RedColor.copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isNormal) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isNormal) GreenColor else RedColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = if (isNormal) "All Values Normal" else "$abnormalCount Abnormal Values",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = TextDarkColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isNormal) "$parameterCount parameters within range" else "$abnormalCount of $parameterCount parameters out of range",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = if (isNormal) GreenColor else RedColor
                )
            }
        }
    }
}

@Composable
private fun TestResultsCard(
    parameters: List<ReportParameter>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Text(
                text = "Test Results",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = TextDarkColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            parameters.forEachIndexed { index: Int, parameter: ReportParameter ->
                ParameterRow(parameter = parameter)
                if (index < parameters.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = CardBorderColor.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ParameterRow(
    parameter: ReportParameter
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = parameter.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = TextDarkColor
            )
            
            // Show recommendation if present
            if (!parameter.recommendation.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = parameter.recommendation,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    color = PrimaryBlue,
                    lineHeight = 16.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = "${parameter.value} ${parameter.unit}",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            ),
            color = if (parameter.isNormal) GreenColor else RedColor
        )
    }
}

private fun generateReportPdf(context: android.content.Context, report: HealthReport): File {
    val document = PdfDocument()
    val pageWidth = 595  // A4 width in points
    val pageHeight = 842 // A4 height in points

    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
    val page = document.startPage(pageInfo)
    val canvas = page.canvas

    // Paints
    val titlePaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 22f
        color = android.graphics.Color.parseColor("#1E293B")
    }
    val subtitlePaint = Paint().apply {
        textSize = 14f
        color = android.graphics.Color.parseColor("#64748B")
    }
    val headerPaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 16f
        color = android.graphics.Color.parseColor("#1E293B")
    }
    val bodyPaint = Paint().apply {
        textSize = 13f
        color = android.graphics.Color.parseColor("#1E293B")
    }
    val normalPaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 13f
        color = android.graphics.Color.parseColor("#10B981")
    }
    val abnormalPaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 13f
        color = android.graphics.Color.parseColor("#EF4444")
    }
    val linePaint = Paint().apply {
        color = android.graphics.Color.parseColor("#E2E8F0")
        strokeWidth = 1f
    }
    val brandPaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 18f
        color = android.graphics.Color.parseColor("#2196F3")
    }

    var yPos = 50f
    val leftMargin = 40f
    val rightEdge = pageWidth - 40f

    // App branding
    canvas.drawText("DrugsSearch Health Report", leftMargin, yPos, brandPaint)
    yPos += 30f
    canvas.drawLine(leftMargin, yPos, rightEdge, yPos, linePaint)
    yPos += 25f

    // Report Title & Date
    canvas.drawText("${report.type} Report", leftMargin, yPos, titlePaint)
    yPos += 22f
    canvas.drawText("Date: ${report.date}", leftMargin, yPos, subtitlePaint)
    yPos += 30f

    // Patient Details Section
    canvas.drawText("Patient Details", leftMargin, yPos, headerPaint)
    yPos += 10f
    canvas.drawLine(leftMargin, yPos + 5, rightEdge, yPos + 5, linePaint)
    yPos += 25f

    val name = report.patientName ?: "N/A"
    val age = report.patientAge?.toString() ?: "N/A"
    val gender = report.patientGender ?: "N/A"

    canvas.drawText("Name: $name", leftMargin, yPos, bodyPaint)
    yPos += 20f
    canvas.drawText("Age: $age", leftMargin, yPos, bodyPaint)
    yPos += 20f
    canvas.drawText("Gender: $gender", leftMargin, yPos, bodyPaint)
    yPos += 35f

    // Status
    val statusText = if (report.isNormal) "Overall Status: Normal" else "Overall Status: Abnormal (${report.abnormalCount} parameters)"
    val statusPaint = if (report.isNormal) normalPaint else abnormalPaint
    canvas.drawText(statusText, leftMargin, yPos, statusPaint)
    yPos += 10f
    canvas.drawLine(leftMargin, yPos + 5, rightEdge, yPos + 5, linePaint)
    yPos += 25f

    // Table Header
    canvas.drawText("Parameter", leftMargin, yPos, headerPaint)
    canvas.drawText("Value", leftMargin + 200, yPos, headerPaint)
    canvas.drawText("Unit", leftMargin + 330, yPos, headerPaint)
    canvas.drawText("Status", leftMargin + 430, yPos, headerPaint)
    yPos += 5f
    canvas.drawLine(leftMargin, yPos + 5, rightEdge, yPos + 5, linePaint)
    yPos += 22f

    // Table Rows
    for (param in report.parameters) {
        if (yPos > pageHeight - 80) break // Prevent overflow

        canvas.drawText(param.name, leftMargin, yPos, bodyPaint)
        canvas.drawText(param.value, leftMargin + 200, yPos, bodyPaint)
        canvas.drawText(param.unit, leftMargin + 330, yPos, bodyPaint)
        val statusLabel = if (param.isNormal) "Normal" else "Abnormal"
        val paint = if (param.isNormal) normalPaint else abnormalPaint
        canvas.drawText(statusLabel, leftMargin + 430, yPos, paint)
        yPos += 24f
    }

    // Footer
    yPos = pageHeight - 50f
    canvas.drawLine(leftMargin, yPos - 15, rightEdge, yPos - 15, linePaint)
    val footerPaint = Paint().apply {
        textSize = 10f
        color = android.graphics.Color.parseColor("#94A3B8")
    }
    canvas.drawText("Generated by DrugsSearch App • For informational purposes only • Consult your healthcare provider", leftMargin, yPos, footerPaint)

    document.finishPage(page)

    // Save to Downloads directory
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    if (!downloadsDir.exists()) downloadsDir.mkdirs()

    val fileName = "${report.type.replace(" ", "_")}_Report_${report.date.replace(" ", "_").replace(",", "")}.pdf"
    val file = File(downloadsDir, fileName)

    FileOutputStream(file).use { fos ->
        document.writeTo(fos)
    }
    document.close()

    return file
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReportDetailScreenPreview() {
    val sampleReport = HealthReport(
        id = "1",
        type = "Blood Count",
        date = "January 20, 2026",
        uploadedAt = "10:30 AM",
        isNormal = true,
        patientName = "John Doe",
        patientAge = 35,
        patientGender = "Male",
        parameters = listOf(
            ReportParameter("Hemoglobin", "14.2", "g/dL", true),
            ReportParameter("WBC", "7500", "/μL", true),
            ReportParameter("RBC", "4.8", "M/μL", true),
            ReportParameter("Platelets", "250000", "/μL", true)
        )
    )
    drugssearchTheme {
        ReportDetailScreen(report = sampleReport)
    }
}
