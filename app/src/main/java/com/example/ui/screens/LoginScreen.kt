package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.example.data.auth.OtpAuthManager
import com.example.data.auth.PhoneAuthManager
import com.example.data.auth.PhoneAuthState
import com.example.ui.theme.ImperialNavy
import com.example.ui.theme.SaffronDark
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.TempleGold
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authManager: OtpAuthManager,
    phoneAuthManager: PhoneAuthManager? = null,
    onNavigateToOtp: (countryCode: String, phone: String) -> Unit
) {
    val context = LocalContext.current
    var countryCode by remember { mutableStateOf("+91") }
    var phoneNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val phoneAuthState by (phoneAuthManager?.authState?.collectAsState() ?: remember { mutableStateOf(null) })

    LaunchedEffect(phoneAuthState) {
        when (val state = phoneAuthState) {
            is PhoneAuthState.Loading -> {
                isLoading = true
                errorMessage = null
            }
            is PhoneAuthState.CodeSent -> {
                isLoading = false
                errorMessage = null
                keyboardController?.hide()
                onNavigateToOtp(countryCode, phoneNumber)
            }
            is PhoneAuthState.Error -> {
                isLoading = false
                errorMessage = state.message
            }
            is PhoneAuthState.Success -> {
                isLoading = false
            }
            else -> {}
        }
    }

    val canSend = phoneNumber.filter { it.isDigit() }.length >= 10 && !isLoading

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Emblem & Header
            Surface(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape),
                color = SaffronPrimary.copy(alpha = 0.12f),
                shadowElevation = 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = "Bharat Heritage Emblem",
                        modifier = Modifier.size(52.dp),
                        tint = SaffronPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "BHARAT HERITAGE",
                style = MaterialTheme.typography.titleMedium,
                color = TempleGold,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Text(
                text = "भारत की धरोहर",
                style = MaterialTheme.typography.headlineMedium,
                color = ImperialNavy,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Explore 5,000 years of timeless monuments, sacred temples, historic forts, and living traditions.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Phone Authentication",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "We will send a 6-digit secure verification code to your number.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Phone Input Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Country Code
                        OutlinedTextField(
                            value = countryCode,
                            onValueChange = { if (it.length <= 4) countryCode = it },
                            modifier = Modifier
                                .width(88.dp)
                                .testTag("country_code_input"),
                            label = { Text("Code") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SaffronPrimary,
                                focusedLabelColor = SaffronPrimary
                            )
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        // Phone Number
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = {
                                val filtered = it.filter { ch -> ch.isDigit() }
                                if (filtered.length <= 10) {
                                    phoneNumber = filtered
                                    errorMessage = null
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("phone_number_input"),
                            label = { Text("Mobile Number") },
                            placeholder = { Text("9876543210") },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = SaffronPrimary)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SaffronPrimary,
                                focusedLabelColor = SaffronPrimary
                            )
                        )
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = errorMessage!!,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (phoneAuthManager?.isFirebaseConfigured == false) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Place your 'google-services.json' (for package com.bharat.heritage) in the app/ folder of this project.",
                                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Send OTP Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            isLoading = true
                            errorMessage = null
                            val activity = context as? Activity
                            if (phoneAuthManager != null && activity != null) {
                                phoneAuthManager.sendVerificationCode(activity, "$countryCode$phoneNumber")
                            } else {
                                isLoading = false
                                errorMessage = "Phone authentication service is not available."
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("send_otp_button"),
                        enabled = canSend,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SaffronPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Sending Secure OTP...", fontWeight = FontWeight.SemiBold)
                        } else {
                            Text("Send OTP", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Security assurance pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = SaffronDark
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "End-to-End Cryptographic Authentication",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
