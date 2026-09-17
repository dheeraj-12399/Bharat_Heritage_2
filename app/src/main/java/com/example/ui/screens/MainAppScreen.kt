package com.example.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.ai.AiVoiceService
import com.example.data.auth.OtpAuthManager
import com.example.ui.theme.ImperialNavy
import com.example.ui.theme.SaffronPrimary

enum class MainTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    DISCOVER("Discover", Icons.Filled.Explore, Icons.Outlined.Explore, "nav_discover"),
    SEARCH("Search", Icons.Filled.Search, Icons.Outlined.Search, "nav_search"),
    AI_GUIDE("AI Guide", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "nav_ai_guide"),
    BOOKMARKS("Saved", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder, "nav_bookmarks"),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile")
}

@Composable
fun MainAppScreen(
    authManager: OtpAuthManager,
    voiceService: AiVoiceService,
    onNavigateToDetail: (String) -> Unit,
    onLogout: () -> Unit
) {
    var currentTab by remember { mutableStateOf(MainTab.DISCOVER) }
    var preloadedAiQuery by remember { mutableStateOf<String?>(null) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                MainTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SaffronPrimary,
                            selectedTextColor = SaffronPrimary,
                            indicatorColor = SaffronPrimary.copy(alpha = 0.12f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainTab.DISCOVER -> {
                    HomeScreen(
                        onNavigateToDetail = onNavigateToDetail,
                        onNavigateToSearch = { currentTab = MainTab.SEARCH }
                    )
                }
                MainTab.SEARCH -> {
                    SearchScreen(
                        onBack = { currentTab = MainTab.DISCOVER },
                        onNavigateToDetail = onNavigateToDetail
                    )
                }
                MainTab.AI_GUIDE -> {
                    AiVoiceAssistantScreen(
                        voiceService = voiceService,
                        initialQuery = preloadedAiQuery
                    )
                }
                MainTab.BOOKMARKS -> {
                    BookmarksScreen(
                        onNavigateToDetail = onNavigateToDetail
                    )
                }
                MainTab.PROFILE -> {
                    ProfileScreen(
                        authManager = authManager,
                        onLoggedOut = onLogout
                    )
                }
            }
        }
    }
}
