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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.OtpAuthManager
import com.example.network.BackendConfig
import com.example.ui.theme.ImperialNavy
import com.example.ui.theme.PeacockTeal
import com.example.ui.theme.SaffronDark
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.TempleGold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(
    authManager: OtpAuthManager,
    onLoggedOut: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val userProfile by authManager.currentUser.collectAsState()

    var isCheckingHealth by remember { mutableStateOf(false) }
    var healthStatus by remember { mutableStateOf<String?>("Checking status...") }
    var isHealthy by remember { mutableStateOf(false) }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editCity by remember { mutableStateOf("") }
    var editRegion by remember { mutableStateOf("") }
    var editBio by remember { mutableStateOf("") }

    var showServerConfigDialog by remember { mutableStateOf(false) }
    var customUrlInput by remember { mutableStateOf(BackendConfig.currentBaseUrl) }

    fun runHealthCheck() {
        scope.launch {
            isCheckingHealth = true
            healthStatus = "Pinging /api/health..."
            val startTime = System.currentTimeMillis()
            try {
                val response = withContext(Dispatchers.IO) {
                    BackendConfig.apiService.checkHealth()
                }
                val duration = System.currentTimeMillis() - startTime
                if (response.isSuccessful) {
                    isHealthy = true
                    healthStatus = "Online (HTTP 200 • ${duration}ms)"
                } else {
                    isHealthy = false
                    healthStatus = "Server responded with HTTP ${response.code()}"
                }
            } catch (e: Exception) {
                isHealthy = false
                healthStatus = "Offline / Cold-start (${e.javaClass.simpleName}). Using Smart Offline Fallback."
            } finally {
                isCheckingHealth = false
            }
        }
    }

    LaunchedEffect(Unit) {
        runHealthCheck()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("profile_screen")
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = SaffronPrimary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = userProfile?.displayName ?: "Heritage Explorer",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ImperialNavy
                )

                Text(
                    text = userProfile?.phoneNumber ?: "+91 Verified User",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SaffronDark,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        editName = userProfile?.displayName ?: ""
                        editCity = userProfile?.city ?: "New Delhi"
                        editRegion = userProfile?.favoriteRegion ?: "Pan-India"
                        editBio = userProfile?.bio ?: ""
                        showEditProfileDialog = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = ImperialNavy, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Profile", color = ImperialNavy, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Details Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Explorer Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ImperialNavy
                )
                Spacer(modifier = Modifier.height(12.dp))

                ProfileItemRow(label = "Home City", value = userProfile?.city ?: "New Delhi")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                ProfileItemRow(label = "Favorite Heritage Region", value = userProfile?.favoriteRegion ?: "Pan-India")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                ProfileItemRow(label = "About / Bio", value = userProfile?.bio ?: "Exploring India's 5,000 years of heritage.")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                ProfileItemRow(label = "Session Security", value = "Authenticated via Cryptographic OTP")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Backend Connectivity & Health Check Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isHealthy) Icons.Default.CloudDone else Icons.Default.Cloud,
                            contentDescription = null,
                            tint = if (isHealthy) PeacockTeal else SaffronPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Backend Connectivity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ImperialNavy
                        )
                    }

                    IconButton(
                        onClick = { runHealthCheck() },
                        enabled = !isCheckingHealth
                    ) {
                        if (isCheckingHealth) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = SaffronPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isHealthy) PeacockTeal.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isHealthy) Icons.Default.CheckCircle else Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (isHealthy) PeacockTeal else SaffronDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = healthStatus ?: "Status unknown",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isHealthy) PeacockTeal else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "API Base URL: ${BackendConfig.currentBaseUrl}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        customUrlInput = BackendConfig.currentBaseUrl
                        showServerConfigDialog = true
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Configure Custom Server Endpoint")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Button
        Button(
            onClick = {
                scope.launch {
                    authManager.logout()
                    onLoggedOut()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(50.dp)
                .testTag("logout_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log Out", fontWeight = FontWeight.Bold)
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Explorer Profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editCity,
                        onValueChange = { editCity = it },
                        label = { Text("City") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editRegion,
                        onValueChange = { editRegion = it },
                        label = { Text("Favorite Heritage Region") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Bio") },
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            authManager.updateProfile(editName, editCity, editRegion, editBio)
                            showEditProfileDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Server Config Dialog
    if (showServerConfigDialog) {
        AlertDialog(
            onDismissRequest = { showServerConfigDialog = false },
            title = { Text("Configure Backend URL") },
            text = {
                Column {
                    Text(
                        text = "Enter your custom production Render backend URL or staging endpoint:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customUrlInput,
                        onValueChange = { customUrlInput = it },
                        label = { Text("Base URL") },
                        placeholder = { Text("https://your-backend.onrender.com/") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customUrlInput.isNotBlank()) {
                            BackendConfig.updateBaseUrl(customUrlInput)
                            showServerConfigDialog = false
                            runHealthCheck()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text("Update & Reconnect")
                }
            },
            dismissButton = {
                TextButton(onClick = { showServerConfigDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileItemRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
