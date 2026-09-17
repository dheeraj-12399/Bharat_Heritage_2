package com.example.data.model

data class HeritageItem(
    val id: String,
    val name: String,
    val hindiName: String,
    val category: String, // Monuments, Sacred Temples, Historic Forts, Ancient Caves, Palaces, UNESCO Sites, Living Traditions
    val state: String,
    val city: String,
    val period: String, // e.g., "17th Century (1632–1653 CE)"
    val builtBy: String, // e.g., "Mughal Emperor Shah Jahan"
    val architecturalStyle: String,
    val description: String,
    val history: String,
    val culturalSignificance: String,
    val imageUrl: String,
    val facts: List<String>,
    val bestTimeToVisit: String,
    val isUnesco: Boolean = true,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

data class UserProfile(
    val phoneNumber: String,
    val displayName: String,
    val city: String = "New Delhi",
    val favoriteRegion: String = "Pan-India",
    val bio: String = "Heritage enthusiast and traveler exploring the timeless glory of Bharat.",
    val memberSince: String = "2026"
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isVoiceInput: Boolean = false
)

enum class MessageSender {
    USER,
    AI
}

enum class VoiceState {
    IDLE,
    LISTENING,
    PROCESSING,
    RESPONDING,
    ERROR
}
