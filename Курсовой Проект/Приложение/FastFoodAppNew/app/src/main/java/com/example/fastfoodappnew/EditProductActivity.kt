package com.example.fastfoodappnew

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditProductActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPrice: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnBack: ImageButton

    private var productId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        UserManager.init(this)
        MenuData.init(this)

        if (!UserManager.isAdmin()) {
            Toast.makeText(this, "Доступ запрещён", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        btnBack = findViewById(R.id.btnBack)
        etName = findViewById(R.id.etName)
        etPrice = findViewById(R.id.etPrice)
        etDescription = findViewById(R.id.etDescription)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnSave = findViewById(R.id.btnSave)

        val categories = arrayOf("burger", "pizza", "drink")
        val categoryNames = arrayOf("🍔 Бургеры", "🍕 Пицца", "🥤 Напитки")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = spinnerAdapter

        productId = intent.getIntExtra("product_id", -1)

        if (productId != -1) {
            // Режим редактирования
            val product = MenuData.products.find { it.id == productId }
            if (product != null) {
                etName.setText(product.name)
                etPrice.setText(product.price.toString())
                etDescription.setText(product.description)
                val catIndex = categories.indexOf(product.category)
                if (catIndex >= 0) spinnerCategory.setSelection(catIndex)
            }
            btnSave.text = "Сохранить изменения"
        } else {
            // Режим добавления
            val category = intent.getStringExtra("category") ?: "burger"
            val catIndex = categories.indexOf(category)
            if (catIndex >= 0) spinnerCategory.setSelection(catIndex)
            btnSave.text = "Добавить позицию"
        }

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val priceStr = etPrice.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val category = categories[spinnerCategory.selectedItemPosition]

            if (name.isEmpty()) {
                Toast.makeText(this, "Введите название", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (priceStr.isEmpty()) {
                Toast.makeText(this, "Введите цену", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceStr.toIntOrNull()
            if (price == null || price <= 0) {
                Toast.makeText(this, "Некорректная цена", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (description.isEmpty()) {
                Toast.makeText(this, "Введите описание", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (productId != -1) {
                MenuData.updateProduct(productId, name, price, description, category)
                Toast.makeText(this, "Позиция обновлена", Toast.LENGTH_SHORT).show()
            } else {
                MenuData.addProduct(name, price, description, category)
                Toast.makeText(this, "Позиция добавлена", Toast.LENGTH_SHORT).show()
            }

            finish()
        }
    }
}