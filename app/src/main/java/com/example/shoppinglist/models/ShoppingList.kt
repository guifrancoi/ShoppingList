package com.example.shoppinglist.models

data class ShoppingList(
    val id: Long = System.currentTimeMillis(),
    var titulo: String,
    val itens: MutableList<ShoppingItem> = mutableListOf()
)