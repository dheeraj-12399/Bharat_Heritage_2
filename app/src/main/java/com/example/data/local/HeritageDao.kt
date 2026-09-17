package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HeritageDao {

    // Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY savedAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE id = :id)")
    fun isBookmarked(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmark(id: String)

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileDirect(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    @Query("DELETE FROM user_profile")
    suspend fun clearUserProfile()

    // Chat History
    @Query("SELECT * FROM chat_history ORDER BY timestamp ASC")
    fun getChatHistory(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_history")
    suspend fun clearChatHistory()
}
