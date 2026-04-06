package com.example.fastfoodappnew

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etPhone: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoToRegister: Button
    private lateinit var testAccountsContainer: LinearLayout

    private var isPhoneFormatting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UserManager.init(this)
        MenuData.init(this)

        if (UserManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoToRegister = findViewById(R.id.btnGoToRegister)
        testAccountsContainer = findViewById(R.id.testAccountsContainer)

        setupPhoneFormatter()
        setupTestAccounts()

        btnLogin.setOnClickListener {
            val phoneFormatted = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (phoneFormatted.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val phoneDigits = phoneFormatted.replace(Regex("[^0-9]"), "")
            val phoneToCheck = if (phoneDigits.length == 11) {
                "+7" + phoneDigits.substring(phoneDigits.length - 10)
            } else {
                phoneFormatted
            }

            if (UserManager.login(phoneToCheck, password)) {
                val role = if (UserManager.isAdmin()) "Админ" else "Клиент"
                Toast.makeText(this, "Добро пожаловать! ($role)", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Неверный номер или пароль", Toast.LENGTH_SHORT).show()
            }
        }

        btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun setupPhoneFormatter() {
        etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isPhoneFormatting) return
                isPhoneFormatting = true
                val digits = s.toString().replace(Regex("[^0-9]"), "")
                val formatted = formatPhoneNumber(digits)
                etPhone.setText(formatted)
                etPhone.setSelection(formatted.length)
                isPhoneFormatting = false
            }
        })
    }

    private fun formatPhoneNumber(digits: String): String {
        val d = if (digits.startsWith("8") && digits.length > 1) {
            "7" + digits.substring(1)
        } else if (digits.startsWith("7")) {
            digits
        } else if (digits.isEmpty()) {
            return ""
        } else {
            "7$digits"
        }

        val sb = StringBuilder("+7")
        if (d.length > 1) {
            sb.append(" (")
            sb.append(d.substring(1, minOf(d.length, 4)))
        }
        if (d.length >= 4) {
            sb.append(") ")
            sb.append(d.substring(4, minOf(d.length, 7)))
        }
        if (d.length >= 7) {
            sb.append("-")
            sb.append(d.substring(7, minOf(d.length, 9)))
        }
        if (d.length >= 9) {
            sb.append("-")
            sb.append(d.substring(9, minOf(d.length, 11)))
        }
        return sb.toString()
    }

    private fun setupTestAccounts() {
        val testAccounts = UserManager.getTestAccounts()
        for (account in testAccounts) {
            val btn = Button(this).apply {
                text = "📱 ${account.first} / ${account.second} (${account.third})"
                textSize = 12f
                isAllCaps = false
                setBackgroundColor(0x22000000)
                setTextColor(0xFF333333.toInt())
                setPadding(16, 8, 16, 8)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.bottomMargin = 8
                layoutParams = params
            }
            btn.setOnClickListener {
                etPhone.setText(account.first)
                etPassword.setText(account.second)
            }
            testAccountsContainer.addView(btn)
        }
    }
}