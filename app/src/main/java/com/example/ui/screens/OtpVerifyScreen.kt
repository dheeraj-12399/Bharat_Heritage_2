package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.example.data.auth.OtpAuthManager
import com.example.data.auth.PhoneAuthManager
import com.example.data.auth.PhoneAuthState
import com.example.ui.theme.ImperialNavy
import com.example.ui.theme.SaffronDark
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.TempleGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OtpVerifyScreen(
    authManager: OtpAuthManager,
    phoneAuthManager: PhoneAuthManager? = null,
    countryCode: String,
    phoneNumber: String,
    onBack: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val cleanCountryCode = countryCode.replace("plus_", "+")
    val fullPhone = OtpAuthManager.cleanPhoneNumber(cleanCountryCode, phoneNumber)
    val maskedPhone = OtpAuthManager.maskPhone(fullPhone)

    var otpValue by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var countdownSeconds by remember { mutableIntStateOf(60) }
    var canResend by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val phoneAuthState by (phoneAuthManager?.authState?.collectAsState() ?: remember { mutableStateOf(null) })

    LaunchedEffect(phoneAuthState) {
        when (val state = phoneAuthState) {
            is PhoneAuthState.Loading -> {
                isLoading = true
                errorMessage = null
            }
            is PhoneAuthState.Success -> {
                isLoading = false
                errorMessage = null
                keyboardController?.hide()
                onAuthSuccess()
            }
            is PhoneAuthState.Error -> {
                isLoading = false
                errorMessage = state.message
            }
            is PhoneAuthState.CodeSent -> {
                isLoading = false
            }
            else -> {}
        }
    }

    // Countdown Timer for Resend
    LaunchedEffect(countdownSeconds) {
        if (countdownSeconds > 0) {
            delay(1000)
            countdownSeconds -= 1
        } else {
            canResend = true
        }
    }

    LaunchedEffect(Unit) {
        delay(250) // Allow previous IME animation and navigation transition to settle
        focusRequester.requestFocus()
    }

    fun triggerVerification(code: String) {
        if (code.length == 6 && !isLoading) {
            isLoading = true
            errorMessage = null
            keyboardController?.hide()
            if (phoneAuthManager != null) {
                phoneAuthManager.verifyCode(code) { result ->
                    result.onFailure { error ->
                        isLoading = false
                        errorMessage = error.localizedMessage ?: "Verification failed. Please check the OTP."
                    }
                }
            } else {
                isLoading = false
                errorMessage = "Authentication service unavailable."
            }
        }
    }

    // Auto-verify when all 6 digits entered
    LaunchedEffect(otpValue) {
        if (otpValue.length == 6) {
            triggerVerification(otpValue)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        keyboardController?.hide()
                        onBack()
                    },
                    modifier = Modifier.testTag("back_to_login_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ImperialNavy)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "STEP 2 OF 2",
                    style = MaterialTheme.typography.labelMedium,
                    color = TempleGold,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Lock Icon
            Surface(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                color = SaffronPrimary.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = SaffronPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Enter Verification Code",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "We sent a 6-digit code to",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = maskedPhone,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SaffronDark
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit",
                    style = MaterialTheme.typography.bodySmall,
                    color = TempleGold,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onBack() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 6-digit visual OTP boxes backed by invisible TextField
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("otp_input_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // OTP Box Row
                    BasicTextField(
                        value = otpValue,
                        onValueChange = { input ->
                            val digitsOnly = input.filter { it.isDigit() }
                            if (digitsOnly.length <= 6) {
                                otpValue = digitsOnly
                                errorMessage = null
                            }
                        },
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .testTag("otp_hidden_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        decorationBox = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                repeat(6) { index ->
                                    val char = otpValue.getOrNull(index)?.toString() ?: ""
                                    val isFocused = otpValue.length == index || (index == 5 && otpValue.length == 6)

                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (char.isNotEmpty()) SaffronPrimary.copy(alpha = 0.08f)
                                                else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                            .border(
                                                width = if (isFocused) 2.dp else 1.dp,
                                                color = if (isFocused) SaffronPrimary
                                                else if (char.isNotEmpty()) SaffronDark
                                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = char,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.errorContainer)
                                .padding(10.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = errorMessage!!,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Verify Button
                    Button(
                        onClick = {
                            triggerVerification(otpValue)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("verify_otp_button"),
                        enabled = otpValue.length == 6 && !isLoading,
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
                            Text("Verifying...")
                        } else {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Verify & Explore Bharat", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Resend Timer Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (canResend) {
                            TextButton(
                                onClick = {
                                    canResend = false
                                    countdownSeconds = 60
                                    errorMessage = null
                                    val activity = context as? Activity
                                    if (phoneAuthManager != null && activity != null) {
                                        phoneAuthManager.resendVerificationCode(activity)
                                    } else {
                                        errorMessage = "Authentication service unavailable."
                                    }
                                },
                                modifier = Modifier.testTag("resend_otp_button")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Resend OTP",
                                    color = SaffronPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Text(
                                text = "Resend OTP in $countdownSeconds seconds",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
