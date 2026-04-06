package com.example.fastfoodappnew

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

object MenuData {
    private const val PREFS_NAME = "fastfood_menu"
    private const val KEY_PRODUCTS = "products_json"
    private const val KEY_NEXT_ID = "next_id"

    private lateinit var prefs: SharedPreferences
    private val _products = mutableListOf<Product>()
    val products: List<Product> get() = _products

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadProducts()
    }

    private fun loadProducts() {
        _products.clear()
        val json = prefs.getString(KEY_PRODUCTS, null)
        if (json == null) {
            // Первый запуск — создаём начальное меню
            _products.addAll(getDefaultProducts())
            saveProducts()
        } else {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                _products.add(
                    Product(
                        id = obj.getInt("id"),
                        name = obj.getString("name"),
                        price = obj.getInt("price"),
                        description = obj.getString("description"),
                        category = obj.getString("category"),
                        enabled = obj.getBoolean("enabled")
                    )
                )
            }
        }
    }

    private fun saveProducts() {
        val arr = JSONArray()
        for (p in _products) {
            val obj = JSONObject()
            obj.put("id", p.id)
            obj.put("name", p.name)
            obj.put("price", p.price)
            obj.put("description", p.description)
            obj.put("category", p.category)
            obj.put("enabled", p.enabled)
            arr.put(obj)
        }
        prefs.edit().putString(KEY_PRODUCTS, arr.toString()).apply()
    }

    private fun getDefaultProducts(): List<Product> {
        return listOf(
            Product(1, "Классический бургер", 299, "Сочная котлета, салат, томаты", "burger"),
            Product(2, "Чизбургер", 329, "Двойной сыр, котлета, соус", "burger"),
            Product(3, "Бургер с беконом", 359, "Бекон, сыр, соус BBQ", "burger"),
            Product(4, "Пепперони", 499, "Пикантная пепперони, сыр моцарелла", "pizza"),
            Product(5, "Маргарита", 449, "Томаты, моцарелла, базилик", "pizza"),
            Product(6, "Четыре сыра", 549, "Моцарелла, пармезан, горгонзола, фета", "pizza"),
            Product(7, "Кола", 99, "Coca-Cola 0.5л", "drink"),
            Product(8, "Спрайт", 99, "Sprite 0.5л", "drink"),
            Product(9, "Фанта", 99, "Fanta 0.5л", "drink"),
            Product(10, "Морс", 129, "Домашний клюквенный морс 0.5л", "drink")
        )
    }

    fun getEnabledProducts(): List<Product> {
        return _products.filter { it.enabled }
    }

    fun getProductsByCategory(category: String, adminMode: Boolean = false): List<Product> {
        return if (adminMode) {
            _products.filter { it.category == category }
        } else {
            _products.filter { it.category == category && it.enabled }
        }
    }

    fun addProduct(name: String, price: Int, description: String, category: String): Product {
        val nextId = (prefs.getInt(KEY_NEXT_ID, 100)) + 1
        prefs.edit().putInt(KEY_NEXT_ID, nextId).apply()
        val product = Product(nextId, name, price, description, category)
        _products.add(product)
        saveProducts()
        return product
    }

    fun updateProduct(id: Int, name: String, price: Int, description: String, category: String) {
        val product = _products.find { it.id == id }
        if (product != null) {
            product.name = name
            product.price = price
            product.description = description
            product.category = category
            saveProducts()
        }
    }

    fun toggleProduct(id: Int) {
        val product = _products.find { it.id == id }
        if (product != null) {
            product.enabled = !product.enabled
            saveProducts()
        }
    }

    fun deleteProduct(id: Int) {
        _products.removeAll { it.id == id }
        saveProducts()
    }
}