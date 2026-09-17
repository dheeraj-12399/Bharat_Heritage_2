package com.example.data.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.data.local.HeritageDatabase
import com.example.data.local.UserProfileEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

sealed class PhoneAuthState {
    object Idle : PhoneAuthState()
    object Loading : PhoneAuthState()
    data class CodeSent(val verificationId: String, val phoneNumber: String) : PhoneAuthState()
    data class Success(val user: FirebaseUser, val localProfile: UserProfileEntity) : PhoneAuthState()
    data class Error(val message: String, val code: String? = null) : PhoneAuthState()
}

/**
 * PhoneAuthManager manages Firebase Phone Authentication for Bharat Heritage:
 * - Real SMS OTP request via Firebase PhoneAuthProvider
 * - 60-second timeouts and ForceResendingToken support
 * - Instant verification / automatic SMS retrieval handling
 * - Credential verification and Firebase session sign-in
 * - Sync with local Room database for offline session persistence
 * - Safe diagnostic logging (NEVER logs OTP values, tokens, or credentials)
 */
class PhoneAuthManager(
    private val context: Context,
    customAuth: FirebaseAuth? = null
) {
    companion object {
        private const val TAG = "BharatHeritageAuth"

        fun maskPhone(phone: String): String {
            val clean = phone.replace(Regex("[^0-9+]"), "")
            return if (clean.length > 4) {
                clean.take(3) + "••••••" + clean.takeLast(4)
            } else {
                "••••"
            }
        }
    }

    private val db = HeritageDatabase.getDatabase(context)
    private val dao = db.heritageDao()
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _authState = MutableStateFlow<PhoneAuthState>(PhoneAuthState.Idle)
    val authState: StateFlow<PhoneAuthState> = _authState.asStateFlow()

    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var targetPhoneNumber: String? = null

    val firebaseAuth: FirebaseAuth? = customAuth ?: run {
        Log.d(TAG, "FIREBASE_INIT_STARTED: Initializing Firebase with application context")
        try {
            val app = if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            } else {
                FirebaseApp.getInstance()
            }
            if (app != null) {
                val auth = FirebaseAuth.getInstance(app)
                Log.d(TAG, "FIREBASE_INIT_SUCCESS: FirebaseAuth instance obtained.")
                auth
            } else {
                Log.i(TAG, "FIREBASE_INIT_STATUS: google-services.json is not present; Firebase initialization deferred.")
                null
            }
        } catch (e: Exception) {
            Log.i(TAG, "FIREBASE_INIT_STATUS: [${e.javaClass.simpleName}] ${e.message}. Awaiting google-services.json in app/ folder.")
            null
        }
    }

    val isFirebaseConfigured: Boolean
        get() = firebaseAuth != null

    val currentFirebaseUser: FirebaseUser?
        get() = firebaseAuth?.currentUser

    val isUserLoggedIn: Boolean
        get() = firebaseAuth?.currentUser != null

    /**
     * Dispatches real OTP SMS via Firebase Phone Authentication to the given phone number.
     * @param activity Hosting activity required by Firebase PhoneAuth
     * @param rawPhoneNumber E.164 or national phone number (e.g. "+91 9876543210")
     */
    fun sendVerificationCode(activity: Activity, rawPhoneNumber: String) {
        val cleanPhone = normalizePhoneNumber(rawPhoneNumber)
        if (cleanPhone.length < 10) {
            _authState.value = PhoneAuthState.Error("Please enter a valid 10-digit mobile number with country code.")
            return
        }

        val auth = firebaseAuth
        if (auth == null) {
            val errorMsg = "Firebase is not initialized. Please ensure google-services.json is present in the app/ folder."
            Log.w(TAG, "PHONE_AUTH_VERIFICATION_FAILED: $errorMsg")
            _authState.value = PhoneAuthState.Error(errorMsg, "MISSING_GOOGLE_SERVICES_JSON")
            return
        }

        targetPhoneNumber = cleanPhone
        _authState.value = PhoneAuthState.Loading
        Log.d(TAG, "PHONE_AUTH_REQUEST_STARTED: targetPhone=${maskPhone(cleanPhone)}")

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "PHONE_AUTH_VERIFICATION_COMPLETED: Instant verification or auto-retrieval succeeded.")
                signInWithCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                val mapped = mapFirebaseError(e)
                Log.e(TAG, "PHONE_AUTH_VERIFICATION_FAILED: errorCode=${e.javaClass.simpleName}, errorMessage=${e.message}")
                _authState.value = PhoneAuthState.Error(mapped, e.javaClass.simpleName)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "PHONE_AUTH_CODE_SENT: verificationIdReceived=true, resendTokenReceived=true")
                storedVerificationId = verificationId
                resendToken = token
                _authState.value = PhoneAuthState.CodeSent(
                    verificationId = verificationId,
                    phoneNumber = cleanPhone
                )
            }

            override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                Log.d(TAG, "PHONE_AUTH_TIMEOUT: Auto-retrieval timed out. User will enter SMS code manually.")
                storedVerificationId = verificationId
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(cleanPhone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    /**
     * Resends the SMS verification code using the cached resend token.
     */
    fun resendVerificationCode(activity: Activity) {
        val auth = firebaseAuth
        val phone = targetPhoneNumber
        val token = resendToken
        if (auth == null || phone == null || token == null) {
            _authState.value = PhoneAuthState.Error("Unable to resend OTP. Please restart verification.")
            return
        }

        _authState.value = PhoneAuthState.Loading
        Log.d(TAG, "PHONE_AUTH_REQUEST_STARTED: (resend) targetPhone=${maskPhone(phone)}")

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "PHONE_AUTH_VERIFICATION_COMPLETED: (resend)")
                signInWithCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e(TAG, "PHONE_AUTH_VERIFICATION_FAILED: (resend) errorCode=${e.javaClass.simpleName}, errorMessage=${e.message}")
                _authState.value = PhoneAuthState.Error(mapFirebaseError(e), e.javaClass.simpleName)
            }

            override fun onCodeSent(
                verificationId: String,
                newToken: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "PHONE_AUTH_CODE_SENT: (resend) verificationIdReceived=true, resendTokenReceived=true")
                storedVerificationId = verificationId
                resendToken = newToken
                _authState.value = PhoneAuthState.CodeSent(
                    verificationId = verificationId,
                    phoneNumber = phone
                )
            }

            override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                Log.d(TAG, "PHONE_AUTH_TIMEOUT: (resend)")
                storedVerificationId = verificationId
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .setForceResendingToken(token)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    /**
     * Verifies the 6-digit OTP entered by the user.
     */
    fun verifyCode(otpCode: String, onComplete: ((Result<FirebaseUser>) -> Unit)? = null) {
        val verificationId = storedVerificationId
        if (verificationId == null) {
            _authState.value = PhoneAuthState.Error("Verification session expired. Please request a new code.")
            onComplete?.invoke(Result.failure(IllegalStateException("Missing verification ID")))
            return
        }

        val cleanOtp = otpCode.trim()
        if (cleanOtp.length != 6 || !cleanOtp.all { it.isDigit() }) {
            _authState.value = PhoneAuthState.Error("Please enter a valid 6-digit verification code.")
            onComplete?.invoke(Result.failure(IllegalArgumentException("Invalid OTP length")))
            return
        }

        _authState.value = PhoneAuthState.Loading
        Log.d(TAG, "Verifying 6-digit code with Firebase...")

        val credential = PhoneAuthProvider.getCredential(verificationId, cleanOtp)
        signInWithCredential(credential, onComplete)
    }

    /**
     * Signs in with the PhoneAuthCredential and synchronizes the session with the local Room database.
     */
    private fun signInWithCredential(
        credential: PhoneAuthCredential,
        onComplete: ((Result<FirebaseUser>) -> Unit)? = null
    ) {
        val auth = firebaseAuth
        if (auth == null) {
            _authState.value = PhoneAuthState.Error("Firebase is not initialized.")
            onComplete?.invoke(Result.failure(IllegalStateException("Firebase is null")))
            return
        }

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        Log.d(TAG, "Firebase authentication successful for UID: ${user.uid}")
                        scope.launch {
                            val profile = syncWithLocalDatabase(user)
                            _authState.value = PhoneAuthState.Success(user, profile)
                            onComplete?.invoke(Result.success(user))
                        }
                    } else {
                        Log.w(TAG, "Authentication succeeded but no FirebaseUser returned.")
                        _authState.value = PhoneAuthState.Error("Authentication succeeded but no user returned.")
                        onComplete?.invoke(Result.failure(IllegalStateException("No user returned")))
                    }
                } else {
                    val exception = task.exception
                    val errorMsg = if (exception is FirebaseException) {
                        mapFirebaseError(exception)
                    } else {
                        exception?.localizedMessage ?: "Verification failed. Please check the code and try again."
                    }
                    Log.w(TAG, "signInWithCredential failed: $errorMsg")
                    _authState.value = PhoneAuthState.Error(errorMsg)
                    onComplete?.invoke(Result.failure(exception ?: Exception(errorMsg)))
                }
            }
    }

    /**
     * Persists or updates user profile in Room database when Firebase authentication succeeds.
     */
    private suspend fun syncWithLocalDatabase(user: FirebaseUser): UserProfileEntity = withContext(Dispatchers.IO) {
        val phone = user.phoneNumber ?: targetPhoneNumber ?: ""
        val existing = dao.getUserProfileDirect()

        val profile = if (existing != null && (existing.phoneNumber == phone || existing.phoneNumber.isEmpty())) {
            existing.copy(
                phoneNumber = phone,
                token = user.uid,
                isAuthenticated = true
            )
        } else {
            UserProfileEntity(
                id = 1,
                phoneNumber = phone,
                displayName = user.displayName ?: existing?.displayName ?: "Heritage Explorer",
                city = existing?.city ?: "New Delhi",
                favoriteRegion = existing?.favoriteRegion ?: "Pan-India",
                bio = existing?.bio ?: "Heritage lover exploring the monuments of Bharat.",
                token = user.uid,
                isAuthenticated = true
            )
        }
        dao.saveUserProfile(profile)
        profile
    }

    /**
     * Signs the user out of Firebase and updates local persistence.
     */
    fun signOut(onComplete: (() -> Unit)? = null) {
        firebaseAuth?.signOut()
        storedVerificationId = null
        resendToken = null
        targetPhoneNumber = null
        _authState.value = PhoneAuthState.Idle

        scope.launch(Dispatchers.IO) {
            val current = dao.getUserProfileDirect()
            if (current != null) {
                dao.saveUserProfile(current.copy(isAuthenticated = false, token = ""))
            }
            withContext(Dispatchers.Main) {
                onComplete?.invoke()
            }
        }
    }

    /**
     * Resets the authentication state back to Idle.
     */
    fun resetState() {
        _authState.value = PhoneAuthState.Idle
    }

    private fun normalizePhoneNumber(raw: String): String {
        val trimmed = raw.trim()
        val digitsOnly = trimmed.replace(Regex("[^0-9+]"), "")
        return when {
            digitsOnly.startsWith("+") -> digitsOnly
            digitsOnly.length == 10 -> "+91$digitsOnly"
            else -> "+$digitsOnly"
        }
    }

    private fun mapFirebaseError(e: FirebaseException): String {
        val msg = e.localizedMessage ?: ""
        val lower = msg.lowercase()
        return when {
            lower.contains("invalid-phone-number") || lower.contains("invalid phone") ->
                "The mobile number format is invalid. Please check the country code and digits."
            lower.contains("quota-exceeded") || lower.contains("quota exceeded") ->
                "SMS quota exceeded. Please check Firebase billing (Blaze plan) in the Firebase Console."
            lower.contains("billing") ->
                "Firebase Blaze (Pay as you go) plan is required for real SMS authentication."
            lower.contains("sms region") || lower.contains("region policy") || lower.contains("blocked") ->
                "SMS delivery is blocked by Firebase SMS Region Policy. Enable India (+91) in Firebase Console -> Authentication -> Settings -> SMS Region Policy."
            lower.contains("too-many-requests") || lower.contains("too many requests") ->
                "Too many requests from this device. Please wait a few minutes before trying again."
            lower.contains("session-expired") || lower.contains("session expired") ->
                "Verification session expired. Please tap 'Resend OTP' to receive a new code."
            lower.contains("invalid-verification-code") || lower.contains("invalid verification code") ->
                "Incorrect verification code. Please enter the 6-digit code received via SMS."
            lower.contains("app-not-authorized") || lower.contains("17010") ->
                "App not authorized: Ensure SHA-1 and SHA-256 certificate fingerprints are added to Firebase Console."
            lower.contains("play integrity") || lower.contains("recaptcha") || lower.contains("17028") ->
                "App verification failed. Ensure SHA-256 certificate fingerprint is registered in Firebase Console."
            else -> msg.ifEmpty { "Phone verification encountered an error. Please check your network and try again." }
        }
    }
}
