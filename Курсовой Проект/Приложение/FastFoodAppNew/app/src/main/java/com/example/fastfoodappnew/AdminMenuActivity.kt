package com.example.fastfoodappnew

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminMenuActivity : AppCompatActivity() {

    private lateinit var rvAdminMenu: RecyclerView
    private lateinit var btnBurgers: Button
    private lateinit var btnPizza: Button
    private lateinit var btnDrinks: Button
    private lateinit var btnAddProduct: Button
    private lateinit var btnAccount: Button
    private lateinit var btnBack: ImageButton
    private lateinit var adapter: AdminMenuAdapter

    private var currentCategory = "burger"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_menu)

        UserManager.init(this)
        MenuData.init(this)

        if (!UserManager.isAdmin()) {
            Toast.makeText(this, "Доступ запрещён", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        btnBack = findViewById(R.id.btnBack)
        btnAccount = findViewById(R.id.btnAccount)
        rvAdminMenu = findViewById(R.id.rvAdminMenu)
        btnBurgers = findViewById(R.id.btnBurgers)
        btnPizza = findViewById(R.id.btnPizza)
        btnDrinks = findViewById(R.id.btnDrinks)
        btnAddProduct = findViewById(R.id.btnAddProduct)

        setupAccount()

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        rvAdminMenu.layoutManager = LinearLayoutManager(this)
        adapter = AdminMenuAdapter(
            items = emptyList(),
            onToggle = { product ->
                MenuData.toggleProduct(product.id)
                val status = if (product.enabled) "выключена" else "включена"
                Toast.makeText(this, "${product.name} $status", Toast.LENGTH_SHORT).show()
                showCategory(currentCategory)
            },
            onEdit = { product ->
                val intent = Intent(this, EditProductActivity::class.java)
                intent.putExtra("product_id", product.id)
                startActivity(intent)
            },
            onDelete = { product ->
                MenuData.deleteProduct(product.id)
                Toast.makeText(this, "${product.name} удалена", Toast.LENGTH_SHORT).show()
                showCategory(currentCategory)
            }
        )
        rvAdminMenu.adapter = adapter

        btnBurgers.setOnClickListener { showCategory("burger") }
        btnPizza.setOnClickListener { showCategory("pizza") }
        btnDrinks.setOnClickListener { showCategory("drink") }

        btnAddProduct.setOnClickListener {
            val intent = Intent(this, EditProductActivity::class.java)
            intent.putExtra("category", currentCategory)
            startActivity(intent)
        }

        showCategory(currentCategory)
    }

    private fun setupAccount() {
        val currentUser = UserManager.currentUser
        if (currentUser != null) {
            btnAccount.text = "📱 ${currentUser.phone} (Админ)"
            btnAccount.visibility = View.VISIBLE
        }

        btnAccount.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menu.add("Выйти из аккаунта")
            popup.setOnMenuItemClickListener { menuItem ->
                if (menuItem.title == "Выйти из аккаунта") {
                    UserManager.logout()
                    CartManager.clearCart()
                    Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                    true
                } else false
            }
            popup.show()
        }
    }

    override fun onResume() {
        super.onResume()
        MenuData.init(this)
        showCategory(currentCategory)
    }

    private fun showCategory(category: String) {
        currentCategory = category
        val filtered = MenuData.getProductsByCategory(category, adminMode = true)
        adapter.updateList(filtered)
    }
}