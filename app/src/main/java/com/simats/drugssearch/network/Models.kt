package com.simats.drugssearch.network

import com.google.gson.annotations.SerializedName

// Login
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("message") val message: String,
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("email") val email: String?
)

// Register
data class RegisterRequest(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirm_password") val confirmPassword: String
)

data class RegisterResponse(
    @SerializedName("message") val message: String
)

// Upload
data class UploadResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String?,
    @SerializedName("report_id") val reportId: Int?,
    @SerializedName("file_name") val fileName: String?,
    @SerializedName("extracted_text") val extractedText: String?
)

// OTP
data class RequestOtpRequest(
    @SerializedName("email") val email: String
)

data class VerifyOtpRequest(
    @SerializedName("email") val email: String,
    @SerializedName("otp") val otp: String
)

// Password Management
data class ResetPasswordRequest(
    @SerializedName("email") val email: String,
    @SerializedName("new_password") val new_password: String,
    @SerializedName("confirm_password") val confirm_password: String
)

data class ChangePasswordRequest(
    @SerializedName("user_id") val user_id: Int,
    @SerializedName("current_password") val current_password: String,
    @SerializedName("new_password") val new_password: String,
    @SerializedName("confirm_password") val confirm_password: String
)

// Profile
data class UpdateProfileRequest(
    @SerializedName("user_id") val user_id: Int,
    @SerializedName("full_name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("date_of_birth") val dob: String,
    @SerializedName("gender") val gender: String
)

data class CommonResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

// Save Report
data class ReportParameter(
    @SerializedName("name") val name: String,
    @SerializedName("value") val value: String,
    @SerializedName("unit") val unit: String,
    @SerializedName("is_normal") val isNormal: Boolean
)

data class SaveReportRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("category") val category: String,
    @SerializedName("parameters") val parameters: List<ReportParameter>
)

data class SaveReportResponse(
    @SerializedName("message") val message: String,
    @SerializedName("report_id") val reportId: Int?
)

data class UserReport(
    @SerializedName("id") val id: String,
    @SerializedName("category") val category: String,
    @SerializedName("date") val date: String,
    @SerializedName("is_normal") val isNormal: Boolean,
    @SerializedName("abnormal_count") val abnormalCount: Int,
    @SerializedName("parameters") val parameters: List<ReportParameter>
)

data class Drug(
    @SerializedName("name") val name: String,
    @SerializedName("condition") val condition: String,
    @SerializedName("dosages") val dosages: List<String>,
    @SerializedName("description") val description: String?,
    @SerializedName("side_effects") val sideEffects: String?,
    @SerializedName("warnings") val warnings: String?,
    @SerializedName("storage") val storage: String?
)
