package com.example.fastfoodappnew

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest

data class User(
    val phone: String,
    val passwordHash: String,
    val role: String // "client" или "admin"
)

object UserManager {
    private const val PREFS_NAME = "fastfood_users"
    private const val KEY_LOGGED_IN_PHONE = "logged_in_phone"
    private const val KEY_USER_PREFIX = "user_"
    private const val KEY_ROLE_PREFIX = "role_"
    private const val KEY_INITIALIZED = "test_accounts_initialized_v3"

    private lateinit var prefs: SharedPreferences

    var currentUser: User? = null
        private set

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        addTestAccounts()
        val savedPhone = prefs.getString(KEY_LOGGED_IN_PHONE, null)
        if (savedPhone != null) {
            val hash = prefs.getString(KEY_USER_PREFIX + savedPhone, null)
            val role = prefs.getString(KEY_ROLE_PREFIX + savedPhone, "client") ?: "client"
            if (hash != null) {
                currentUser = User(savedPhone, hash, role)
            }
        }
    }

    private fun addTestAccounts() {
        if (!prefs.getBoolean(KEY_INITIALIZED, false)) {
            prefs.edit()
                .remove(KEY_USER_PREFIX + "+79991234567")
                .remove(KEY_USER_PREFIX + "+79997654321")
                .remove(KEY_ROLE_PREFIX + "+79991234567")
                .remove(KEY_ROLE_PREFIX + "+79997654321")
                .apply()

            val editor = prefs.edit()
            // Админский аккаунт
            editor.putString(KEY_USER_PREFIX + "+79991234567", hashPassword("admin123"))
            editor.putString(KEY_ROLE_PREFIX + "+79991234567", "admin")
            // Клиентский аккаунт
            editor.putString(KEY_USER_PREFIX + "+79997654321", hashPassword("demo456"))
            editor.putString(KEY_ROLE_PREFIX + "+79997654321", "client")
            editor.putBoolean(KEY_INITIALIZED, true)
            editor.apply()
        }
    }

    fun getTestAccounts(): List<Triple<String, String, String>> {
        return listOf(
            Triple("+79991234567", "admin123", "Админ"),
            Triple("+79997654321", "demo456", "Клиент")
        )
    }

    fun register(phone: String, password: String): Boolean {
        val key = KEY_USER_PREFIX + phone
        if (prefs.contains(key)) {
            return false
        }
        val hash = hashPassword(password)
        prefs.edit()
            .putString(key, hash)
            .putString(KEY_ROLE_PREFIX + phone, "client")
            .apply()
        return true
    }

    fun login(phone: String, password: String): Boolean {
        val key = KEY_USER_PREFIX + phone
        val storedHash = prefs.getString(key, null)
        val inputHash = hashPassword(password)
        if (storedHash != null && storedHash == inputHash) {
            val role = prefs.getString(KEY_ROLE_PREFIX + phone, "client") ?: "client"
            currentUser = User(phone, storedHash, role)
            prefs.edit().putString(KEY_LOGGED_IN_PHONE, phone).apply()
            return true
        }
        return false
    }

    fun logout() {
        currentUser = null
        prefs.edit().remove(KEY_LOGGED_IN_PHONE).apply()
    }

    fun isLoggedIn(): Boolean {
        return currentUser != null
    }

    fun isAdmin(): Boolean {
        return currentUser?.role == "admin"
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}