package com.example.shoppinglist.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShoppingItem(
    val id: Long = System.currentTimeMillis(),
    var nome: String,
    var quantidade: Double,
    var unidade: String,
    var categoryId: Long,
    var isChecked: Boolean = false
) : Parcelable