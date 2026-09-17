package com.example.network

import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class HealthResponse(
    val status: String = "ok",
    val timestamp: String? = null,
    val service: String? = "Bharat Heritage AI Backend"
)

@JsonClass(generateAdapter = true)
data class ChatHistoryItem(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class ChatRequest(
    val message: String,
    val history: List<ChatHistoryItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ChatResponse(
    val reply: String,
    val source: String? = "Bharat Heritage Intelligence"
)

@JsonClass(generateAdapter = true)
data class SendOtpRequest(
    val phoneNumber: String
)

@JsonClass(generateAdapter = true)
data class SendOtpResponse(
    val success: Boolean,
    val message: String
)

@JsonClass(generateAdapter = true)
data class VerifyOtpRequest(
    val phoneNumber: String,
    val otp: String
)

@JsonClass(generateAdapter = true)
data class VerifyOtpResponse(
    val success: Boolean,
    val token: String? = null,
    val message: String? = null
)

interface HeritageApiService {

    @GET("api/health")
    suspend fun checkHealth(): Response<HealthResponse>

    @POST("api/ai/chat")
    suspend fun chat(@Body request: ChatRequest): Response<ChatResponse>

    @POST("api/auth/send-otp")
    suspend fun sendOtp(@Body request: SendOtpRequest): Response<SendOtpResponse>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<VerifyOtpResponse>
}

object BackendConfig {
    // Cloud Run and Render backend endpoints, configurable in App Settings
    private const val DEFAULT_BASE_URL = "https://ais-dev-fb2yjbmjlvedjotz4seyvd-1048874962618.asia-southeast1.run.app/"
    const val FALLBACK_BASE_URL = "https://bharat-heritage-api.onrender.com/"

    var currentBaseUrl: String = DEFAULT_BASE_URL
        private set

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(25, TimeUnit.SECONDS)
            .readTimeout(35, TimeUnit.SECONDS)
            .writeTimeout(25, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private var _apiService: HeritageApiService? = null

    val apiService: HeritageApiService
        get() {
            if (_apiService == null) {
                _apiService = createRetrofit(currentBaseUrl).create(HeritageApiService::class.java)
            }
            return _apiService!!
        }

    fun updateBaseUrl(newUrl: String) {
        val sanitized = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        currentBaseUrl = sanitized
        _apiService = createRetrofit(sanitized).create(HeritageApiService::class.java)
    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
}
