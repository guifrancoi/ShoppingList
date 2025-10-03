package com.example.shoppinglist.models

data class ShoppingItem(
    val id: Long = System.currentTimeMillis(),
    var nome: String,
    var quantidade: Double,
    var unidade: String,
    var categoryId: Long
)