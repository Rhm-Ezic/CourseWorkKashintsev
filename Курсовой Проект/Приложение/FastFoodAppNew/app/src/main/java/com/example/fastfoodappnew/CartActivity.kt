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

class CartActivity : AppCompatActivity() {

    private lateinit var rvCart: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var btnOrder: Button
    private lateinit var btnGoToMenuFromCart: Button
    private lateinit var emptyCartPanel: LinearLayout
    private lateinit var cartNotEmptyPanel: LinearLayout
    private lateinit var adapter: CartAdapter
    private lateinit var btnAccount: Button
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        UserManager.init(this)

        btnBack = findViewById(R.id.btnBack)
        btnAccount = findViewById(R.id.btnAccount)
        rvCart = findViewById(R.id.rvCart)
        tvTotal = findViewById(R.id.tvTotal)
        btnOrder = findViewById(R.id.btnOrder)
        btnGoToMenuFromCart = findViewById(R.id.btnGoToMenuFromCart)
        emptyCartPanel = findViewById(R.id.emptyCartPanel)
        cartNotEmptyPanel = findViewById(R.id.cartNotEmptyPanel)

        setupAccount()

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupRecyclerView()
        updateUI()

        btnOrder.setOnClickListener {
            if (CartManager.items.isEmpty()) {
                Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Заказ оформлен! Спасибо!", Toast.LENGTH_SHORT).show()
                CartManager.clearCart()
                adapter.notifyDataSetChanged()
                updateUI()
            }
        }

        btnGoToMenuFromCart.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }
    }

    private fun setupAccount() {
        val currentUser = UserManager.currentUser
        if (currentUser != null) {
            btnAccount.text = "📱 ${currentUser.phone}"
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
        adapter.notifyDataSetChanged()
        updateUI()
    }

    private fun setupRecyclerView() {
        adapter = CartAdapter(CartManager.items) { cartItem, newQuantity ->
            CartManager.updateQuantity(cartItem.product.id, newQuantity)
            adapter.notifyDataSetChanged()
            updateUI()
        }
        rvCart.layoutManager = LinearLayoutManager(this)
        rvCart.adapter = adapter
    }

    private fun updateUI() {
        val isCartEmpty = CartManager.items.isEmpty()
        emptyCartPanel.visibility = if (isCartEmpty) View.VISIBLE else View.GONE
        cartNotEmptyPanel.visibility = if (isCartEmpty) View.GONE else View.VISIBLE
        if (!isCartEmpty) {
            tvTotal.text = "${CartManager.getTotalPrice()} ₽"
        }
    }
}