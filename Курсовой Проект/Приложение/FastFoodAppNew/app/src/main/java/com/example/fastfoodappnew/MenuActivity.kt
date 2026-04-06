package com.example.fastfoodappnew

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MenuActivity : AppCompatActivity() {

    private lateinit var rvMenu: RecyclerView
    private lateinit var btnBurgers: Button
    private lateinit var btnPizza: Button
    private lateinit var btnDrinks: Button
    private lateinit var cartPanel: LinearLayout
    private lateinit var tvCartSummary: TextView
    private lateinit var btnGoToCart: Button
    private lateinit var btnAccount: Button
    private lateinit var btnBack: ImageButton

    private var currentCategory = "burger"
    private lateinit var adapter: MenuAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        UserManager.init(this)
        MenuData.init(this)

        btnBack = findViewById(R.id.btnBack)
        btnAccount = findViewById(R.id.btnAccount)
        rvMenu = findViewById(R.id.rvMenu)
        btnBurgers = findViewById(R.id.btnBurgers)
        btnPizza = findViewById(R.id.btnPizza)
        btnDrinks = findViewById(R.id.btnDrinks)
        cartPanel = findViewById(R.id.cartPanel)
        tvCartSummary = findViewById(R.id.tvCartSummary)
        btnGoToCart = findViewById(R.id.btnGoToCart)

        setupAccount()

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        rvMenu.layoutManager = LinearLayoutManager(this)
        adapter = MenuAdapter(emptyList()) { product ->
            CartManager.addProduct(product)
            Toast.makeText(this, "${product.name} добавлен!", Toast.LENGTH_SHORT).show()
            updateCartPanel()
        }
        rvMenu.adapter = adapter

        btnBurgers.setOnClickListener { showCategory("burger") }
        btnPizza.setOnClickListener { showCategory("pizza") }
        btnDrinks.setOnClickListener { showCategory("drink") }

        btnGoToCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        showCategory(currentCategory)
        updateCartPanel()
    }

    private fun setupAccount() {
        val currentUser = UserManager.currentUser
        if (currentUser != null) {
            val role = if (UserManager.isAdmin()) " (Админ)" else ""
            btnAccount.text = "📱 ${currentUser.phone}$role"
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
        showCategory(currentCategory)
        updateCartPanel()
    }

    private fun showCategory(category: String) {
        currentCategory = category
        val filtered = MenuData.getProductsByCategory(category, adminMode = false)
        adapter.updateList(filtered)
    }

    private fun updateCartPanel() {
        val itemCount = CartManager.getItemCount()
        val total = CartManager.getTotalPrice()
        if (itemCount > 0) {
            cartPanel.visibility = View.VISIBLE
            tvCartSummary.text = "$itemCount товаров, $total ₽"
        } else {
            cartPanel.visibility = View.GONE
        }
    }
}