package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val state: String,
    val city: String,
    val imageUrl: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val phoneNumber: String,
    val displayName: String,
    val city: String,
    val favoriteRegion: String,
    val bio: String,
    val token: String,
    val isAuthenticated: Boolean = true
)

@Entity(tableName = "chat_history")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val sender: String, // USER or AI
    val text: String,
    val timestamp: Long,
    val isVoiceInput: Boolean
)
