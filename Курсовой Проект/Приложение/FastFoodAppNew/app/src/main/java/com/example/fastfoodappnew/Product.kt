package com.example.fastfoodappnew

data class Product(
    val id: Int,
    var name: String,
    var price: Int,
    var description: String,
    var category: String,
    var enabled: Boolean = true
)