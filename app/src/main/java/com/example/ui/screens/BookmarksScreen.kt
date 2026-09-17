package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.HeritageDatabase
import com.example.ui.theme.ImperialNavy
import com.example.ui.theme.SaffronDark
import com.example.ui.theme.SaffronPrimary
import kotlinx.coroutines.launch

@Composable
fun BookmarksScreen(
    onNavigateToDetail: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dao = remember { HeritageDatabase.getDatabase(context).heritageDao() }
    val bookmarks by dao.getAllBookmarks().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("bookmarks_screen")
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Saved Heritage Treasures",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = ImperialNavy
                )
                Text(
                    text = "${bookmarks.size} site${if (bookmarks.size != 1) "s" else ""} saved for offline exploration",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (bookmarks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = SaffronPrimary.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = SaffronPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Saved Sites Yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Explore the Discover tab and tap the bookmark icon on any monument, fort, or temple to save it here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bookmarks, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToDetail(item.id) }
                            .testTag("bookmark_item_${item.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(item.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = item.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = item.category,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SaffronDark,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = SaffronPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${item.city}, ${item.state}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    scope.launch {
                                        dao.deleteBookmark(item.id)
                                    }
                                },
                                modifier = Modifier.testTag("remove_bookmark_${item.id}")
                            ) {
                                Icon(
                                    Icons.Default.DeleteOutline,
                                    contentDescription = "Remove Bookmark",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
