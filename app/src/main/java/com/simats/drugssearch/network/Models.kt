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
    @SerializedName("email") val email: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("date_of_birth") val dob: String?,
    @SerializedName("gender") val gender: String?
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
    @SerializedName("is_normal") val isNormal: Boolean,
    @SerializedName("recommendation") val recommendation: String? = null
)


data class SaveReportRequest(
    @SerializedName("report_id") val reportId: Int? = null,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("category") val category: String,
    @SerializedName("parameters") val parameters: List<ReportParameter>,
    @SerializedName("patient_name") val patientName: String? = null,
    @SerializedName("patient_age") val patientAge: String? = null,
    @SerializedName("patient_gender") val patientGender: String? = null,
    @SerializedName("remarks") val remarks: String? = null
)

data class SaveReportResponse(
    @SerializedName("message") val message: String,
    @SerializedName("report_id") val reportId: Int?,
    @SerializedName("analysis") val analysis: OcrResponse? // Reusing OcrResponse structure
)

data class UserReport(
    @SerializedName("id") val id: String,
    @SerializedName("category") val category: String,
    @SerializedName("date") val date: String,
    @SerializedName("uploaded_at") val uploadedAt: String?,
    @SerializedName("is_normal") val isNormal: Boolean,
    @SerializedName("abnormal_count") val abnormalCount: Int,
    @SerializedName("parameters") val parameters: List<ReportParameter>,
    @SerializedName("patient_name") val patientName: String?,
    @SerializedName("patient_age") val patientAge: Int?,
    @SerializedName("patient_gender") val patientGender: String?,
    @SerializedName("remarks") val remarks: String?
)

data class PatientDetails(
    @SerializedName("name") val name: String?,
    @SerializedName("age") val age: String?,
    @SerializedName("gender") val gender: String?
)

// Add OcrResponse class if it's missing or update it
data class OcrResponse(
    @SerializedName("report_category") val reportCategory: String?,
    @SerializedName("parameters") val parameters: Map<String, DetectedParameter>?,
    @SerializedName("patient_details") val patientDetails: PatientDetails?
)

data class DetectedParameter(
    @SerializedName("value") val value: Any?, // Can be Double or String
    @SerializedName("unit") val unit: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("risk_level") val riskLevel: String? = "None",
    @SerializedName("deviation") val deviation: Double? = 0.0,
    @SerializedName("category") val category: String?,
    @SerializedName("condition") val condition: String?,
    @SerializedName("summary") val summary: String?,
    @SerializedName("recommendation") val recommendation: Recommendation?
)

data class Recommendation(
    @SerializedName("category") val category: String?,
    @SerializedName("drugs") val drugs: String?
)

data class Drug(
    @SerializedName("drug_name") val drugName: String,
    @SerializedName("generic_name") val genericName: String?,
    @SerializedName("drug_category") val drugCategory: String?,
    @SerializedName("indication") val indication: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("common_dosage") val commonDosage: String?,
    @SerializedName("side_effects") val sideEffects: String?,
    @SerializedName("safety_warnings") val safetyWarnings: String?,
    @SerializedName("storage_details") val storageDetails: String?
)
