package com.example.ui.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object OtpVerify : Screen("otp_verify/{countryCode}/{phone}") {
        fun createRoute(countryCode: String, phone: String): String {
            val safeCc = countryCode.replace("+", "plus_")
            return "otp_verify/$safeCc/$phone"
        }
    }
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object AiVoice : Screen("ai_voice")
    data object Bookmarks : Screen("bookmarks")
    data object Profile : Screen("profile")
    data object Detail : Screen("detail/{id}") {
        fun createRoute(id: String): String = "detail/$id"
    }
}
