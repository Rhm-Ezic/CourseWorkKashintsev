package com.example.fastfoodappnew

object MenuData {
    val products = listOf(
        // Бургеры
        Product(1, "Классический бургер", 299, "Сочная котлета, салат, томаты", "burger"),
        Product(2, "Чизбургер", 329, "Двойной сыр, котлета, соус", "burger"),
        Product(3, "Бургер с беконом", 359, "Бекон, сыр, соус BBQ", "burger"),
        // Пицца
        Product(4, "Пепперони", 499, "Пикантная пепперони, сыр моцарелла", "pizza"),
        Product(5, "Маргарита", 449, "Томаты, моцарелла, базилик", "pizza"),
        Product(6, "Четыре сыра", 549, "Моцарелла, пармезан, горгонзола, фета", "pizza"),
        // Напитки
        Product(7, "Кола", 99, "Coca-Cola 0.5л", "drink"),
        Product(8, "Спрайт", 99, "Sprite 0.5л", "drink"),
        Product(9, "Фанта", 99, "Fanta 0.5л", "drink"),
        Product(10, "Морс", 129, "Домашний клюквенный морс 0.5л", "drink")
    )
}