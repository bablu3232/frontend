package com.simats.drugssearch

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("drug_search_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_EMAIL = "email"
        private const val KEY_PHONE = "phone"
        private const val KEY_DOB = "dob"
        private const val KEY_GENDER = "gender"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_TWO_FACTOR_ENABLED = "two_factor_enabled"
        private const val KEY_IS_ADMIN_LOGGED_IN = "is_admin_logged_in"
    }

    fun saveSession(
        userId: Int,
        userName: String,
        email: String,
        phone: String,
        dob: String,
        gender: String
    ) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, userName)
            putString(KEY_EMAIL, email)
            putString(KEY_PHONE, phone)
            putString(KEY_DOB, dob)
            putString(KEY_GENDER, gender)
            apply()
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)
    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "") ?: ""
    fun getEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""
    fun getPhone(): String = prefs.getString(KEY_PHONE, "") ?: ""
    fun getDob(): String = prefs.getString(KEY_DOB, "") ?: ""
    fun getGender(): String = prefs.getString(KEY_GENDER, "") ?: ""

    fun isBiometricEnabled(): Boolean = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun isTwoFactorEnabled(): Boolean = prefs.getBoolean(KEY_TWO_FACTOR_ENABLED, false)
    fun setTwoFactorEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_TWO_FACTOR_ENABLED, enabled).apply()
    }

    fun saveAdminSession() {
        prefs.edit().putBoolean(KEY_IS_ADMIN_LOGGED_IN, true).apply()
    }

    fun isAdminLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_ADMIN_LOGGED_IN, false)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
