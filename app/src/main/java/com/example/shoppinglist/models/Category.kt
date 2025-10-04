package com.example.shoppinglist.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: Long = 0L,
    var nome: String,
    val isPadrao: Boolean = false,
    val userId: String? = null
) : Parcelable
