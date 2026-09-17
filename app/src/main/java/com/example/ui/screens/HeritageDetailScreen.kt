package com.example.ui.screens

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.datasource.HeritageRepository
import com.example.data.local.BookmarkEntity
import com.example.data.local.HeritageDatabase
import com.example.ui.theme.ImperialNavy
import com.example.ui.theme.PeacockTeal
import com.example.ui.theme.SaffronDark
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.TempleGold
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun HeritageDetailScreen(
    heritageId: String,
    onBack: () -> Unit,
    onAskAi: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dao = remember { HeritageDatabase.getDatabase(context).heritageDao() }

    val item = remember(heritageId) { HeritageRepository.getById(heritageId) }
    val isBookmarkedFlow = remember(heritageId) { dao.isBookmarked(heritageId) }
    val isBookmarked by isBookmarkedFlow.collectAsState(initial = false)

    // TextToSpeech for Audio Narrative Guide
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isNarrating by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        var textToSpeechInstance: TextToSpeech? = null
        textToSpeechInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeechInstance?.setLanguage(Locale("en", "IN"))
                tts = textToSpeechInstance
            }
        }
        onDispose {
            textToSpeechInstance?.stop()
            textToSpeechInstance?.shutdown()
        }
    }

    if (item == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Heritage site details not found.")
        }
        return
    }

    val audioNarrativeText = "${item.name}. ${item.hindiName}. Located in ${item.city}, ${item.state}. " +
        "Built during ${item.period} by ${item.builtBy}. ${item.description} ${item.culturalSignificance}"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .testTag("detail_scroll_column")
        ) {
            // Hero Image with Overlaid Navigation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(item.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Gradient for bar controls
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.65f), Color.Transparent)
                            )
                        )
                )

                // Navigation Bar Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.45f))
                            .testTag("detail_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    IconButton(
                        onClick = {
                            scope.launch {
                                if (isBookmarked) {
                                    dao.deleteBookmark(item.id)
                                } else {
                                    dao.insertBookmark(
                                        BookmarkEntity(
                                            id = item.id,
                                            name = item.name,
                                            category = item.category,
                                            state = item.state,
                                            city = item.city,
                                            imageUrl = item.imageUrl
                                        )
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.45f))
                            .testTag("detail_bookmark_button")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) TempleGold else Color.White
                        )
                    }
                }

                // UNESCO tag if applicable
                if (item.isUnesco) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = PeacockTeal.copy(alpha = 0.95f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "UNESCO World Heritage Site",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Content Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = item.hindiName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SaffronDark
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = SaffronPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${item.city}, ${item.state}, India",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Audio Guide Button
                Button(
                    onClick = {
                        if (isNarrating) {
                            tts?.stop()
                            isNarrating = false
                        } else {
                            val params = Bundle()
                            tts?.speak(audioNarrativeText, TextToSpeech.QUEUE_FLUSH, params, "heritage_guide_${item.id}")
                            isNarrating = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("audio_guide_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isNarrating) ImperialNavy else SaffronPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = if (isNarrating) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isNarrating) "Stop Audio Guide" else "Listen to Audio Heritage Guide",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Quick Facts Matrix Cards
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailInfoRow(icon = Icons.Default.Person, label = "Built By", value = item.builtBy)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        DetailInfoRow(icon = Icons.Default.DateRange, label = "Period / Era", value = item.period)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        DetailInfoRow(icon = Icons.Default.AccountBalance, label = "Architecture", value = item.architecturalStyle)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Overview Section
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ImperialNavy
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 26.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Historical Background Section
                Text(
                    text = "Historical Background",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ImperialNavy
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.history,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 26.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Cultural Significance Section
                Text(
                    text = "Cultural & Spiritual Significance",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ImperialNavy
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.culturalSignificance,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 26.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Key Facts List
                Text(
                    text = "Fascinating Facts",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ImperialNavy
                )
                Spacer(modifier = Modifier.height(10.dp))
                item.facts.forEach { fact ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(8.dp)
                                .padding(top = 8.dp),
                            shape = CircleShape,
                            color = SaffronPrimary
                        ) {}
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = fact,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Ask AI Button
                Button(
                    onClick = {
                        onAskAi("Tell me more about the history and architecture of ${item.name} in ${item.city}")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("ask_ai_monument_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TempleGold,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ask AI Guide About ${item.name}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun DetailInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = SaffronPrimary.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
