package com.example.fastfoodappnew

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest

data class User(
    val phone: String,
    val passwordHash: String
)

object UserManager {
    private const val PREFS_NAME = "fastfood_users"
    private const val KEY_LOGGED_IN_PHONE = "logged_in_phone"
    private const val KEY_USER_PREFIX = "user_"
    private const val KEY_INITIALIZED = "test_accounts_initialized_v2"

    private lateinit var prefs: SharedPreferences

    var currentUser: User? = null
        private set

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        addTestAccounts()
        val savedPhone = prefs.getString(KEY_LOGGED_IN_PHONE, null)
        if (savedPhone != null) {
            val hash = prefs.getString(KEY_USER_PREFIX + savedPhone, null)
            if (hash != null) {
                currentUser = User(savedPhone, hash)
            }
        }
    }

    private fun addTestAccounts() {
        if (!prefs.getBoolean(KEY_INITIALIZED, false)) {
            // Удаляем старые тестовые аккаунты без хеширования
            prefs.edit()
                .remove(KEY_USER_PREFIX + "+79991234567")
                .remove(KEY_USER_PREFIX + "+79997654321")
                .apply()

            // Создаём с хешированием
            val editor = prefs.edit()
            editor.putString(KEY_USER_PREFIX + "+79991234567", hashPassword("test123"))
            editor.putString(KEY_USER_PREFIX + "+79997654321", hashPassword("demo456"))
            editor.putBoolean(KEY_INITIALIZED, true)
            editor.apply()
        }
    }

    fun getTestAccounts(): List<Pair<String, String>> {
        return listOf(
            Pair("+79991234567", "test123"),
            Pair("+79997654321", "demo456")
        )
    }

    fun register(phone: String, password: String): Boolean {
        val key = KEY_USER_PREFIX + phone
        if (prefs.contains(key)) {
            return false
        }
        val hash = hashPassword(password)
        prefs.edit().putString(key, hash).apply()
        return true
    }

    fun login(phone: String, password: String): Boolean {
        val key = KEY_USER_PREFIX + phone
        val storedHash = prefs.getString(key, null)
        val inputHash = hashPassword(password)
        if (storedHash != null && storedHash == inputHash) {
            currentUser = User(phone, storedHash)
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

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}