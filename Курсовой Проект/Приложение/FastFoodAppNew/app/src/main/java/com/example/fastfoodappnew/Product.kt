package com.example.fastfoodappnew

data class Product(
    val id: Int,
    val name: String,
    val price: Int,
    val description: String,
    val category: String   // "burger", "pizza", "drink"
)