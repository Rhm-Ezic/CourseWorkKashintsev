package com.example.fastfoodappnew

data class CartItem(
    val product: Product,
    var quantity: Int
)

object CartManager {
    private val _items = mutableListOf<CartItem>()
    val items: List<CartItem> get() = _items

    fun addProduct(product: Product, quantity: Int = 1) {
        val existing = _items.find { it.product.id == product.id }
        if (existing != null) {
            existing.quantity += quantity
        } else {
            _items.add(CartItem(product, quantity))
        }
    }

    fun removeProduct(productId: Int) {
        _items.removeIf { it.product.id == productId }
    }

    fun updateQuantity(productId: Int, newQuantity: Int) {
        val item = _items.find { it.product.id == productId }
        if (item != null) {
            if (newQuantity <= 0) {
                removeProduct(productId)
            } else {
                item.quantity = newQuantity
            }
        }
    }

    fun clearCart() {
        _items.clear()
    }

    fun getTotalPrice(): Int {
        return _items.sumOf { it.product.price * it.quantity }
    }

    fun getItemCount(): Int {
        return _items.sumOf { it.quantity }
    }
}