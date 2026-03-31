package com.example.fastfoodappnew

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(
    private val items: List<CartItem>,
    private val onQuantityChanged: (CartItem, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val btnMinus: Button = itemView.findViewById(R.id.btnMinus)
        private val btnPlus: Button = itemView.findViewById(R.id.btnPlus)

        fun bind(item: CartItem) {
            tvName.text = item.product.name
            tvQuantity.text = item.quantity.toString()
            val totalItemPrice = item.product.price * item.quantity
            tvPrice.text = "${totalItemPrice} ₽"

            btnMinus.setOnClickListener {
                val newQty = item.quantity - 1
                onQuantityChanged(item, newQty)
            }
            btnPlus.setOnClickListener {
                val newQty = item.quantity + 1
                onQuantityChanged(item, newQty)
            }
        }
    }
}