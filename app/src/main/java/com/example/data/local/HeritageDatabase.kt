package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BookmarkEntity::class, UserProfileEntity::class, ChatMessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class HeritageDatabase : RoomDatabase() {
    abstract fun heritageDao(): HeritageDao

    companion object {
        @Volatile
        private var INSTANCE: HeritageDatabase? = null

        fun getDatabase(context: Context): HeritageDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HeritageDatabase::class.java,
                    "bharat_heritage_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
