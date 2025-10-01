package com.example.shoppinglist.repository

import com.example.shoppinglist.models.ShoppingItem
import com.example.shoppinglist.models.ShoppingList

object ShoppingRepository {
    private val listas = mutableListOf<ShoppingList>()

    // Exposição imutável para ViewModels (você pode expor LiveData se quiser)
    fun getAllLists(): List<ShoppingList> = listas.toList()

    fun addList(list: ShoppingList) {
        listas.add(0, list) // adiciona no topo
    }

    fun updateList(updated: ShoppingList) {
        val idx = listas.indexOfFirst { it.id == updated.id }
        if (idx >= 0) listas[idx] = updated
    }

    fun removeList(listId: Long) {
        listas.removeAll { it.id == listId }
    }

    fun findListById(id: Long): ShoppingList? = listas.find { it.id == id }

    // itens
    fun addItemToList(listId: Long, item: ShoppingItem) {
        findListById(listId)?.itens?.add(item)
    }
    fun updateItemInList(listId: Long, item: ShoppingItem) {
        val list = findListById(listId) ?: return
        val idx = list.itens.indexOfFirst { it.id == item.id }
        if (idx >= 0) list.itens[idx] = item
    }
    fun removeItemFromList(listId: Long, itemId: Long) {
        val list = findListById(listId) ?: return
        list.itens.removeAll { it.id == itemId }
    }
}