package com.simats.drugssearch.ui

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import com.simats.drugssearch.R

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.ui.theme.DrugsSearchTheme

private val PrimaryBlue = Color(0xFF2196F3)
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextDarkColor = Color(0xFF1E293B)
private val TextGrayColor = Color(0xFF64748B)

@Composable
fun HelpSupportScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onFaqClick: () -> Unit = {},
    onCallClick: () -> Unit = {},
    onEmailClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    onAboutClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)

        ) {
            // Top Bar
            HelpSupportTopBar(
                onBackClick = onBackClick,
                onHomeClick = onHomeClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Help & Support",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = TextDarkColor
                )

                Text(
                    text = "We're here to help you",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextGrayColor
                    ),
                    modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                )

                // Support Options
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SupportOptionCard(
                        icon = Icons.Default.Info, // FAQ icon replacement
                        title = "FAQs",
                        color = Color(0xFFF3E8FF), // Light Purple
                        iconTint = Color(0xFF9C27B0),
                        onClick = onFaqClick
                    )
                    SupportOptionCard(
                        icon = Icons.Default.Call,
                        title = "Call Us",
                        subtitle = "1-800-HEALTH",
                        color = Color(0xFFECFDF5), // Light Green
                        iconTint = Color(0xFF10B981),
                        onClick = onCallClick
                    )
                    SupportOptionCard(
                        icon = Icons.Default.Email,
                        title = "Email Us",
                        subtitle = "support@healthcare.com",
                        color = Color(0xFFE3F2FD), // Light Blue
                        iconTint = Color(0xFF2196F3),
                        onClick = onEmailClick
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // About Button
                Button(
                    onClick = onAboutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    )
                ) {
                    Text(
                        text = "About HealthCare",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Disclaimer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info, // Placeholder
                        contentDescription = "Info",
                        tint = TextGrayColor,
                        modifier = Modifier.size(20.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "This platform is designed for educational and informational purposes only. It is not a substitute for professional medical advice, diagnosis, or treatment.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrayColor
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Links
                 Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Privacy Policy",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryBlue
                    )
                    Text(
                        text = "  •  ",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrayColor
                    )
                    Text(
                        text = "Terms of Service",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryBlue
                    )
                     Text(
                        text = "  •  ",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrayColor
                    )
                    Text(
                        text = "Contact Us",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryBlue
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                     text = "© 2026 DrugSearch. All rights reserved.",
                     style = MaterialTheme.typography.bodySmall,
                     color = TextGrayColor
                )
                 Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SupportOptionCard(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    color: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = TextDarkColor
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGrayColor
                    )
                }
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextGrayColor
            )
        }
    }
}

@Composable
private fun HelpSupportTopBar(
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
fun HelpSupportScreenPreview() {
    DrugsSearchTheme {
        HelpSupportScreen()
    }
}
