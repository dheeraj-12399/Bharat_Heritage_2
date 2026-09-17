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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.datasource.HeritageRepository
import com.example.ui.theme.ImperialNavy
import com.example.ui.theme.SaffronDark
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.TempleGold

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedState by remember { mutableStateOf("All States") }

    val results = remember(searchQuery, selectedCategory, selectedState) {
        HeritageRepository.search(searchQuery, selectedCategory, selectedState)
    }

    val suggestions = listOf("Taj Mahal", "Hampi", "Konark", "Ajanta", "Varanasi", "Nalanda", "Amer Fort", "Golden Temple")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("search_screen")
    ) {
        // Top Search Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("search_back_button")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ImperialNavy)
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_input_field"),
                placeholder = { Text("Search monuments, temples, forts...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = SaffronPrimary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SaffronPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }

        // Category Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(HeritageRepository.categories) { category ->
                val isSelected = category == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = category },
                    label = { Text(category, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SaffronPrimary,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        // Suggestions pills if search query is empty
        if (searchQuery.isEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Explore, contentDescription = null, tint = TempleGold, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Popular Heritage Searches",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestions) { item ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { searchQuery = item }
                    ) {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = SaffronDark
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Results Section
        if (results.isEmpty()) {
            // Empty State
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
                        modifier = Modifier.size(72.dp),
                        shape = CircleShape,
                        color = SaffronPrimary.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                tint = SaffronPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Heritage Sites Found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "We couldn't find any sites matching \"$searchQuery\". Try checking the spelling or exploring another category or state.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Found ${results.size} Heritage Location${if (results.size > 1) "s" else ""}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                items(results) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToDetail(item.id) }
                            .testTag("search_result_${item.id}"),
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
                                    .size(76.dp)
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
                                    text = item.hindiName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SaffronDark,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
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
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
