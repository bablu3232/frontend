package com.simats.drugssearch.network

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @POST("register.php")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("login.php")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    @Multipart
    @POST("upload_report.php")
    suspend fun uploadReport(
        @Part("user_id") userId: okhttp3.RequestBody,
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>

    @Multipart
    @POST("upload_report_gemini.php")
    suspend fun uploadReportGemini(
        @Part("user_id") userId: okhttp3.RequestBody,
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>

    @Multipart
    @POST("upload_report_ocrspace.php")
    suspend fun uploadReportOcrSpace(
        @Part("user_id") userId: okhttp3.RequestBody,
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>

    @GET("test_connection.php")
    suspend fun testConnection(): Response<Void>

    @POST("request_otp.php")
    suspend fun requestOtp(@Body request: RequestOtpRequest): Response<CommonResponse>

    @POST("verify_otp.php")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<CommonResponse>

    @POST("reset_password.php")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<CommonResponse>

    @POST("change_password.php")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<RegisterResponse>

    @POST("update_profile.php")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<CommonResponse>

    @POST("save_report_data.php")
    suspend fun saveReportData(@Body request: SaveReportRequest): Response<SaveReportResponse>

    @GET("get_user_reports.php")
    suspend fun getUserReports(@Query("user_id") userId: Int): Response<List<UserReport>>

    @GET("search_drugs.php")
    suspend fun searchDrugs(
        @Query("query") query: String,
        @Query("category") category: String? = null
    ): Response<List<Drug>>

    @POST("update_security.php")
    suspend fun updateSecurity(@Body request: Map<String, Any>): Response<CommonResponse>

    @POST("delete_account.php")
    suspend fun deleteAccount(@Body request: Map<String, Int>): Response<CommonResponse>
}

// RegisterRequest, RegisterResponse, LoginRequest, LoginResponse, UploadResponse are in Models.kt
// User is not needed if LoginResponse in Models.kt is used

// RequestOtpRequest, VerifyOtpRequest, ResetPasswordRequest, ChangePasswordRequest, UpdateProfileRequest, CommonResponse are in Models.kt
