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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.datasource.HeritageRepository
import com.example.data.local.BookmarkEntity
import com.example.data.local.HeritageDatabase
import com.example.data.model.HeritageItem
import com.example.ui.theme.ImperialNavy
import com.example.ui.theme.PeacockTeal
import com.example.ui.theme.SaffronDark
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.TempleGold
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToSearch: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dao = remember { HeritageDatabase.getDatabase(context).heritageDao() }
    val bookmarks by dao.getAllBookmarks().collectAsState(initial = emptyList())
    val bookmarkedIds = remember(bookmarks) { bookmarks.map { it.id }.toSet() }

    var selectedCategory by remember { mutableStateOf("All") }
    var selectedState by remember { mutableStateOf("All States") }

    val filteredItems = remember(selectedCategory, selectedState) {
        HeritageRepository.search("", selectedCategory, selectedState)
    }

    val featuredMonument = HeritageRepository.heritageItems.first()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Custom Heritage Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape),
                        color = SaffronPrimary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "BHARAT HERITAGE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = SaffronDark,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Discover India's Glorious History",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onNavigateToSearch,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("home_search_icon_button")
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search Heritage", tint = ImperialNavy)
                }
            }
        }

        // Hero Monument Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(230.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { onNavigateToDetail(featuredMonument.id) }
                    .testTag("hero_monument_banner")
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(featuredMonument.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = featuredMonument.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dramatic gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.85f)
                                ),
                                startY = 80f
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SaffronPrimary)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "WONDER OF BHARAT",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${featuredMonument.name} • ${featuredMonument.hindiName}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = TempleGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${featuredMonument.city}, ${featuredMonument.state}",
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Category Filter Section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Heritage Categories",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ImperialNavy,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(HeritageRepository.categories) { category ->
                    val isSelected = category == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = { Text(category, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SaffronPrimary,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("category_chip_$category")
                    )
                }
            }
        }

        // State Region Filter Section
        item {
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(HeritageRepository.states) { state ->
                    val isSelected = state == selectedState
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedState = state },
                        label = { Text(state, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TempleGold,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("state_chip_$state")
                    )
                }
            }
        }

        // Heritage Items Header
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Treasures of Bharat (${filteredItems.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ImperialNavy
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Items List
        items(filteredItems) { item ->
            val isBookmarked = bookmarkedIds.contains(item.id)
            HeritageListItemCard(
                item = item,
                isBookmarked = isBookmarked,
                onClick = { onNavigateToDetail(item.id) },
                onBookmarkToggle = {
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
                }
            )
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
fun HeritageListItemCard(
    item: HeritageItem,
    isBookmarked: Boolean,
    onClick: () -> Unit,
    onBookmarkToggle: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag("heritage_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
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

                // Category pill
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(12.dp),
                    color = ImperialNavy.copy(alpha = 0.85f)
                ) {
                    Text(
                        text = item.category,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // UNESCO Pill if applicable
                if (item.isUnesco) {
                    Surface(
                        modifier = Modifier
                            .padding(top = 12.dp, start = 120.dp)
                            .align(Alignment.TopStart),
                        shape = RoundedCornerShape(12.dp),
                        color = PeacockTeal.copy(alpha = 0.9f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "UNESCO",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Bookmark icon button
                IconButton(
                    onClick = onBookmarkToggle,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .size(36.dp)
                        .testTag("bookmark_toggle_${item.id}")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save to bookmarks",
                        tint = if (isBookmarked) TempleGold else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.hindiName,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = SaffronDark
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = SaffronPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${item.city}, ${item.state}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "• ${item.period}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
