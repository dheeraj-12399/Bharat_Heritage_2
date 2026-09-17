package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.ai.AiVoiceService
import com.example.data.auth.OtpAuthManager
import com.example.data.auth.PhoneAuthManager
import com.example.ui.navigation.Screen
import com.example.ui.screens.HeritageDetailScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainAppScreen
import com.example.ui.screens.OtpVerifyScreen
import com.example.ui.theme.BharatHeritageTheme
import com.example.ui.theme.SaffronPrimary

class MainActivity : ComponentActivity() {

    private lateinit var authManager: OtpAuthManager
    private lateinit var phoneAuthManager: PhoneAuthManager
    private lateinit var voiceService: AiVoiceService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        authManager = OtpAuthManager(this)
        phoneAuthManager = PhoneAuthManager(this)
        voiceService = AiVoiceService(this)

        setContent {
            BharatHeritageTheme {
                BharatHeritageApp(
                    authManager = authManager,
                    phoneAuthManager = phoneAuthManager,
                    voiceService = voiceService
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceService.destroy()
    }
}

@Composable
fun BharatHeritageApp(
    authManager: OtpAuthManager,
    phoneAuthManager: PhoneAuthManager? = null,
    voiceService: AiVoiceService
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    var isCheckingSession by remember { mutableStateOf(true) }
    val currentUser by authManager.currentUser.collectAsState()

    LaunchedEffect(Unit) {
        authManager.loadInitialSession()
        isCheckingSession = false
    }

    if (isCheckingSession) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = SaffronPrimary)
        }
        return
    }

    val isFirebaseLoggedIn = phoneAuthManager?.isUserLoggedIn == true
    val startDestination = if (currentUser != null || isFirebaseLoggedIn) Screen.Home.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize()
    ) {
        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                authManager = authManager,
                phoneAuthManager = phoneAuthManager,
                onNavigateToOtp = { countryCode, phone ->
                    navController.navigate(Screen.OtpVerify.createRoute(countryCode, phone))
                }
            )
        }

        // OTP Verify Screen
        composable(
            route = Screen.OtpVerify.route,
            arguments = listOf(
                navArgument("countryCode") { type = NavType.StringType },
                navArgument("phone") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val countryCode = backStackEntry.arguments?.getString("countryCode") ?: "+91"
            val phone = backStackEntry.arguments?.getString("phone") ?: ""

            OtpVerifyScreen(
                authManager = authManager,
                phoneAuthManager = phoneAuthManager,
                countryCode = countryCode,
                phoneNumber = phone,
                onBack = { navController.popBackStack() },
                onAuthSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Main App Screen (Hosts Discover, Search, AI Voice, Bookmarks, Profile)
        composable(Screen.Home.route) {
            MainAppScreen(
                authManager = authManager,
                voiceService = voiceService,
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.Detail.createRoute(id))
                },
                onLogout = {
                    phoneAuthManager?.signOut()
                    scope.launch {
                        authManager.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        // Heritage Monument Detail Screen
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            HeritageDetailScreen(
                heritageId = id,
                onBack = { navController.popBackStack() },
                onAskAi = { query ->
                    voiceService.sendTextMessage(query)
                    navController.navigate(Screen.Home.route)
                }
            )
        }
    }
}
