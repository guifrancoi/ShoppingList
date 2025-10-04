package com.example.shoppinglist.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShoppingList(
    val id: Long = System.currentTimeMillis(),
    var titulo: String,
    var imageUri: String? = null,
    val itens: MutableList<ShoppingItem> = mutableListOf(),
    val userId: String = ""
) : Parcelable