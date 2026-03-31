package com.example.fastfoodappnew

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var etPhone: EditText
    private lateinit var etPassword: EditText
    private lateinit var etPasswordConfirm: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnGoToLogin: Button

    private var isPhoneFormatting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm)
        btnRegister = findViewById(R.id.btnRegister)
        btnGoToLogin = findViewById(R.id.btnGoToLogin)

        setupPhoneFormatter()

        btnRegister.setOnClickListener {
            val phoneFormatted = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val passwordConfirm = etPasswordConfirm.text.toString().trim()

            // Извлекаем только цифры из номера
            val phoneDigits = phoneFormatted.replace(Regex("[^0-9]"), "")

            if (phoneFormatted.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (phoneDigits.length != 11) {
                Toast.makeText(this, "Введите полный номер телефона (11 цифр)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!phoneDigits.startsWith("7") && !phoneDigits.startsWith("8")) {
                Toast.makeText(this, "Номер должен начинаться с +7 или 8", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 4) {
                Toast.makeText(this, "Пароль минимум 4 символа", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != passwordConfirm) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Сохраняем в формате +7XXXXXXXXXX
            val phoneToSave = "+7" + phoneDigits.substring(phoneDigits.length - 10)

            if (UserManager.register(phoneToSave, password)) {
                UserManager.login(phoneToSave, password)
                Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            } else {
                Toast.makeText(this, "Этот номер уже зарегистрирован", Toast.LENGTH_SHORT).show()
            }
        }

        btnGoToLogin.setOnClickListener {
            finish()
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
}