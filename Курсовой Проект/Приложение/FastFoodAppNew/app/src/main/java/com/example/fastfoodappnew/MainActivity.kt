package com.example.fastfoodappnew

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnMenu = findViewById<Button>(R.id.btnMenu)
        val btnCart = findViewById<Button>(R.id.btnCart)
        val btnAbout = findViewById<Button>(R.id.btnAbout)
        val btnAccount = findViewById<Button>(R.id.btnAccount)

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

        btnMenu.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }

        btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        btnAbout.setOnClickListener {
            Toast.makeText(this, "FastFood App v1.0", Toast.LENGTH_SHORT).show()
        }
    }
}