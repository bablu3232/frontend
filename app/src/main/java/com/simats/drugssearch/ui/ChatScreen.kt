package com.simats.drugssearch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.drugssearch.network.ChatMessage
import com.simats.drugssearch.network.ChatRequest
import com.simats.drugssearch.network.RetrofitClient
import kotlinx.coroutines.launch

private val PrimaryBlue = Color(0xFF2196F3)
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextDarkColor = Color.Black
private val TextGrayColor = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    userId: Int?,
    onBackClick: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val chatMessages = remember { mutableStateListOf<ChatMessage>() }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Initial greeting
    LaunchedEffect(Unit) {
        if (chatMessages.isEmpty()) {
            chatMessages.add(ChatMessage(ai = """
                Hello! I am your DrugsSearch Assistant. Here is what I can do for you:
                
                • 📊 Summarize Report: Ask "Check my latest report"
                • 🧪 Explain Parameters: Ask "Explain LDL" or "What is WBC?"
                • 📈 Trend Analysis: Ask "Compare my reports" to see your progress
                • 🍎 Health Tips: Ask "Give me a health tip"
                
                How can I help you today?
            """.trimIndent()))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Report Assistant", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                        Text("Active (Local NLP)", style = MaterialTheme.typography.bodySmall, color = Color.Black)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Chat Messages List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                state = listState,
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(chatMessages) { msg ->
                    if (msg.user != null) {
                        UserMessageBubble(msg.user)
                    }
                    if (msg.ai != null) {
                        AiMessageBubble(msg.ai)
                    }
                }
                if (isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = PrimaryBlue)
                        }
                    }
                }
            }

            // Scroll to bottom when new messages arrive
            LaunchedEffect(chatMessages.size, isLoading) {
                if (chatMessages.isNotEmpty()) {
                    listState.animateScrollToItem(chatMessages.size - 1)
                }
            }

            // Input Field
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ask me anything...", color = Color.Black) },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color(0xFFF1F5F9),
                            unfocusedContainerColor = Color(0xFFF1F5F9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            if (messageText.isNotBlank() && !isLoading) {
                                val userMsg = messageText.trim()
                                messageText = ""
                                chatMessages.add(ChatMessage(user = userMsg))
                                isLoading = true
                                keyboardController?.hide()
                                
                                coroutineScope.launch {
                                    try {
                                        val history = chatMessages.takeLast(10).toList()
                                        val response = RetrofitClient.instance.sendMessage(
                                            ChatRequest(userId = userId, message = userMsg, chatHistory = history)
                                        )
                                        if (response.isSuccessful && response.body() != null) {
                                            chatMessages.add(ChatMessage(ai = response.body()?.reply ?: "Sorry, I encountered an error."))
                                        } else {
                                            chatMessages.add(ChatMessage(ai = "Error: " + (response.body()?.error ?: "Could not connect to AI server.")))
                                        }
                                    } catch (e: Exception) {
                                        chatMessages.add(ChatMessage(ai = "Connection error. Please check your internet."))
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        })
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FloatingActionButton(
                        onClick = {
                            if (messageText.isNotBlank() && !isLoading) {
                                val userMsg = messageText.trim()
                                messageText = ""
                                chatMessages.add(ChatMessage(user = userMsg))
                                isLoading = true
                                
                                coroutineScope.launch {
                                    try {
                                        val history = chatMessages.takeLast(10).toList()
                                        val response = RetrofitClient.instance.sendMessage(
                                            ChatRequest(userId = userId, message = userMsg, chatHistory = history)
                                        )
                                        if (response.isSuccessful && response.body() != null) {
                                            chatMessages.add(ChatMessage(ai = response.body()?.reply ?: "Sorry, I encountered an error."))
                                        } else {
                                            chatMessages.add(ChatMessage(ai = "Error: " + (response.body()?.error ?: "Could not connect to AI server.")))
                                        }
                                    } catch (e: Exception) {
                                        chatMessages.add(ChatMessage(ai = "Connection error. Please check your internet."))
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        },
                        containerColor = PrimaryBlue,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun UserMessageBubble(text: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Surface(
            color = PrimaryBlue,
            shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(12.dp),
                color = Color.White,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun AiMessageBubble(text: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp),
            shadowElevation = 1.dp
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(12.dp),
                color = TextDarkColor,
                fontSize = 15.sp
            )
        }
    }
}
