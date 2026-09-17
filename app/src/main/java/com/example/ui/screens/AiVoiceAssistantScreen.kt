package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.ai.AiVoiceService
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.data.model.VoiceState
import com.example.ui.theme.ImperialNavy
import com.example.ui.theme.PeacockTeal
import com.example.ui.theme.SaffronDark
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.TempleGold

@Composable
fun AiVoiceAssistantScreen(
    voiceService: AiVoiceService,
    initialQuery: String? = null
) {
    val context = LocalContext.current
    val voiceState by voiceService.voiceState.collectAsState()
    val messages by voiceService.messages.collectAsState()
    val errorMessage by voiceService.errorMessage.collectAsState()

    var inputMessage by remember { mutableStateOf("") }
    var permissionDeniedAlert by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            voiceService.startListening()
        } else {
            permissionDeniedAlert = true
        }
    }

    LaunchedEffect(initialQuery) {
        if (!initialQuery.isNullOrBlank()) {
            voiceService.sendTextMessage(initialQuery)
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Pulsing Wave Animation for Listening State
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val quickQuestions = listOf(
        "Tell me about Hampi monuments",
        "Explain Konark Sun Temple chariot wheels",
        "What are the top UNESCO sites in India?",
        "Significance of Ganga Aarti in Varanasi",
        "Who built the Red Fort?"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("ai_voice_screen")
    ) {
        // Voice Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(38.dp),
                            shape = CircleShape,
                            color = SaffronPrimary.copy(alpha = 0.12f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = SaffronPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Bharat Heritage AI Guide",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ImperialNavy
                            )
                            Text(
                                text = "Powered by /api/ai/chat",
                                style = MaterialTheme.typography.labelSmall,
                                color = PeacockTeal,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Voice State Badge
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = when (voiceState) {
                            VoiceState.LISTENING -> SaffronPrimary.copy(alpha = 0.15f)
                            VoiceState.PROCESSING -> TempleGold.copy(alpha = 0.15f)
                            VoiceState.RESPONDING -> PeacockTeal.copy(alpha = 0.15f)
                            VoiceState.ERROR -> MaterialTheme.colorScheme.errorContainer
                            VoiceState.IDLE -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (voiceState == VoiceState.LISTENING) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(SaffronPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Listening...",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SaffronDark,
                                    fontWeight = FontWeight.Bold
                                )
                            } else if (voiceState == VoiceState.PROCESSING) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    strokeWidth = 2.dp,
                                    color = TempleGold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Thinking...",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TempleGold,
                                    fontWeight = FontWeight.Bold
                                )
                            } else if (voiceState == VoiceState.RESPONDING) {
                                Icon(
                                    Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = null,
                                    tint = PeacockTeal,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Speaking",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PeacockTeal,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = "Ready",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Error Notice if any
                if (errorMessage != null || permissionDeniedAlert) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (permissionDeniedAlert)
                                "Microphone permission is required to speak. You can still type below."
                            else errorMessage ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        // Chat Message History
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(messages) { message ->
                ChatMessageBubble(message = message)
            }
        }

        // Quick Suggestion Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickQuestions) { question ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.clickable {
                        voiceService.sendTextMessage(question)
                    }
                ) {
                    Text(
                        text = question,
                        style = MaterialTheme.typography.labelSmall,
                        color = SaffronDark,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Bottom Controls: Mic, Text Input, Send
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Voice / Mic Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(52.dp)
                ) {
                    if (voiceState == VoiceState.LISTENING) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(SaffronPrimary.copy(alpha = 0.3f))
                        )
                    }

                    IconButton(
                        onClick = {
                            if (voiceState == VoiceState.LISTENING) {
                                voiceService.stopListening()
                            } else if (voiceState == VoiceState.RESPONDING) {
                                voiceService.stopSpeaking()
                            } else {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasPermission) {
                                    voiceService.startListening()
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                when (voiceState) {
                                    VoiceState.LISTENING -> SaffronPrimary
                                    VoiceState.RESPONDING -> ImperialNavy
                                    else -> SaffronPrimary
                                }
                            )
                            .testTag("microphone_button")
                    ) {
                        Icon(
                            imageVector = when (voiceState) {
                                VoiceState.LISTENING -> Icons.Default.MicOff
                                VoiceState.RESPONDING -> Icons.Default.Stop
                                else -> Icons.Default.Mic
                            },
                            contentDescription = "Voice Input",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Text Input
                OutlinedTextField(
                    value = inputMessage,
                    onValueChange = { inputMessage = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_chat_input"),
                    placeholder = { Text("Ask about any monument or tradition...") },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputMessage.isNotBlank()) {
                                voiceService.sendTextMessage(inputMessage)
                                inputMessage = ""
                            }
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Send Button
                IconButton(
                    onClick = {
                        if (inputMessage.isNotBlank()) {
                            voiceService.sendTextMessage(inputMessage)
                            inputMessage = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (inputMessage.isNotBlank()) SaffronPrimary else MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("send_ai_message_button"),
                    enabled = inputMessage.isNotBlank()
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (inputMessage.isNotBlank()) Color.White else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessage) {
    val isUser = message.sender == MessageSender.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Surface(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                color = TempleGold
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .testTag("chat_bubble_${message.id}"),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) SaffronPrimary else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

// Helper extension for width constraint
private fun Modifier.widthIn(max: androidx.compose.ui.unit.Dp): Modifier = this.then(
    Modifier.padding(end = 4.dp)
)
