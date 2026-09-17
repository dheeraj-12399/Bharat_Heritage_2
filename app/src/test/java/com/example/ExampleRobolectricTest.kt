package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.auth.AuthResult
import com.example.data.auth.OtpAuthManager
import com.example.data.datasource.HeritageRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context verifies app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Bharat Heritage", appName)
    }

    @Test
    fun `verify heritage repository has items and search works`() {
        val items = HeritageRepository.heritageItems
        assertTrue("Repository should have heritage items", items.isNotEmpty())

        val taj = HeritageRepository.getById("taj_mahal")
        assertNotNull("Taj Mahal should exist in repository", taj)
        assertEquals("Agra", taj?.city)

        val searchResults = HeritageRepository.search("Hampi")
        assertTrue("Search for Hampi should return results", searchResults.isNotEmpty())
        assertEquals("hampi", searchResults.first().id)
    }

    @Test
    fun `verify otp generation and validation lifecycle`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val authManager = OtpAuthManager(context)

        // Invalid phone number test
        val badPhoneResult = authManager.requestOtp("+91", "123")
        assertTrue("Short phone number should be rejected", badPhoneResult.isFailure)

        // Request valid OTP
        val result = authManager.requestOtp("+91", "9876543210")
        assertTrue("Request OTP should succeed", result.isSuccess)

        val hint = authManager.getTestingOtpHint()
        assertNotNull("Cryptographic OTP hint should be generated", hint)
        assertEquals(6, hint?.length)

        // Invalid code test
        val failResult = authManager.verifyOtp("+91", "9876543210", "000000")
        assertTrue("Wrong code should fail", failResult is AuthResult.Error)

        // Valid code test
        val successResult = authManager.verifyOtp("+91", "9876543210", hint!!)
        assertTrue("Correct code should succeed", successResult is AuthResult.Success)

        // Single-use guarantee: Re-using the same OTP must fail
        val reuseResult = authManager.verifyOtp("+91", "9876543210", hint)
        assertTrue("Re-using the same OTP must fail", reuseResult is AuthResult.Error)
    }

    @Test
    fun `verify user profile preservation on re-login`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val authManager = OtpAuthManager(context)

        // First login
        authManager.requestOtp("+91", "9123456780")
        val code1 = authManager.getTestingOtpHint()!!
        authManager.verifyOtp("+91", "9123456780", code1)

        // Update profile
        authManager.updateProfile(
            name = "Aarav Sharma",
            city = "Varanasi",
            region = "Northern India",
            bio = "Historian researching temples"
        )

        val currentUser = authManager.currentUser.value
        assertEquals("Aarav Sharma", currentUser?.displayName)
        assertEquals("Varanasi", currentUser?.city)

        // Logout
        authManager.logout()
        assertEquals(null, authManager.currentUser.value)

        // Second login with same phone number
        authManager.requestOtp("+91", "9123456780")
        val code2 = authManager.getTestingOtpHint()!!
        val reLoginResult = authManager.verifyOtp("+91", "9123456780", code2)
        assertTrue(reLoginResult is AuthResult.Success)

        // Verified preserved profile
        val restoredUser = (reLoginResult as AuthResult.Success).user
        assertEquals("Aarav Sharma", restoredUser.displayName)
        assertEquals("Varanasi", restoredUser.city)
        assertEquals("Northern India", restoredUser.favoriteRegion)
    }

    @Test
    fun `verify rate limiting on rapid repeated requests`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val authManager = OtpAuthManager(context)

        val first = authManager.requestOtp("+91", "9555544444")
        assertTrue("First request should succeed", first.isSuccess)

        val immediateSecond = authManager.requestOtp("+91", "9555544444")
        assertTrue("Immediate second request should be rate-limited", immediateSecond.isFailure)
        assertTrue(immediateSecond.exceptionOrNull()?.message?.contains("wait") == true)
    }
}
