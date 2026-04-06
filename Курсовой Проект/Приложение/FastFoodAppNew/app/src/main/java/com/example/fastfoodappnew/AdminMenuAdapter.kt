package com.example.fastfoodappnew

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class AdminMenuAdapter(
    private var items: List<Product>,
    private val onToggle: (Product) -> Unit,
    private val onEdit: (Product) -> Unit,
    private val onDelete: (Product) -> Unit
) : RecyclerView.Adapter<AdminMenuAdapter.AdminViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_menu, parent, false)
        return AdminViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun updateList(newItems: List<Product>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class AdminViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: CardView = itemView.findViewById(R.id.card)
        private val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvDesc: TextView = itemView.findViewById(R.id.tvProductDesc)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val btnToggle: Button = itemView.findViewById(R.id.btnToggle)
        private val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(product: Product) {
            tvName.text = product.name
            tvDesc.text = product.description
            tvPrice.text = "${product.price} ₽"

            if (product.enabled) {
                tvStatus.text = "✅ Активна"
                tvStatus.setTextColor(0xFF4CAF50.toInt())
                btnToggle.text = "Выключить"
                card.alpha = 1.0f
            } else {
                tvStatus.text = "❌ Выключена"
                tvStatus.setTextColor(0xFFF44336.toInt())
                btnToggle.text = "Включить"
                card.alpha = 0.6f
            }

            btnToggle.setOnClickListener { onToggle(product) }
            btnEdit.setOnClickListener { onEdit(product) }
            btnDelete.setOnClickListener { onDelete(product) }
        }
    }
}