package com.example.shoppinglist.models

data class ShoppingItem(
    val id: Long = System.currentTimeMillis(), // id simples
    var nome: String,
    var quantidade: Double,
    var unidade: String,
    var categoria: String
)