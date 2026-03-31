package com.example.fastfoodappnew

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MenuAdapter(
    private var items: List<Product>,
    private val onAddClick: (Product) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun updateList(newItems: List<Product>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvDesc: TextView = itemView.findViewById(R.id.tvProductDesc)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        private val btnAdd: Button = itemView.findViewById(R.id.btnAdd)

        fun bind(product: Product) {
            tvName.text = product.name
            tvDesc.text = product.description
            tvPrice.text = "${product.price} ₽"
            btnAdd.setOnClickListener { onAddClick(product) }
        }
    }
}