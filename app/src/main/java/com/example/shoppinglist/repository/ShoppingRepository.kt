package com.example.shoppinglist.repository

import com.example.shoppinglist.models.ShoppingItem
import com.example.shoppinglist.models.ShoppingList

object ShoppingRepository {
    private val listas = mutableListOf<ShoppingList>()

    fun getAllLists(userId: String): List<ShoppingList> = listas.filter { it.userId == userId }.toList()

    fun addList(list: ShoppingList) {
        listas.add(0, list)
    }

    fun updateList(updated: ShoppingList) {
        val idx = listas.indexOfFirst { it.id == updated.id }
        if (idx >= 0) {
            listas[idx] = ShoppingList(
                id = updated.id,
                titulo = updated.titulo,
                imageUri = updated.imageUri,
                itens = updated.itens.toMutableList(),
                userId = updated.userId
            )
        }
    }

    fun removeList(listId: Long) {
        listas.removeAll { it.id == listId }
    }

    fun findListById(id: Long): ShoppingList? {
        val list = listas.find { it.id == id } ?: return null
        return ShoppingList(
            id = list.id,
            titulo = list.titulo,
            imageUri = list.imageUri,
            itens = list.itens.toMutableList(),
            userId = list.userId
        )
    }
    
    private fun findListByIdInternal(id: Long): ShoppingList? = listas.find { it.id == id }

    fun addItemToList(listId: Long, item: ShoppingItem) {
        val list = findListByIdInternal(listId) ?: return
        val updatedItems = list.itens.toMutableList()
        updatedItems.add(item)
        updateList(ShoppingList(
            id = list.id,
            titulo = list.titulo,
            imageUri = list.imageUri,
            itens = updatedItems,
            userId = list.userId
        ))
    }
    
    fun updateItemInList(listId: Long, item: ShoppingItem) {
        val list = findListByIdInternal(listId) ?: return
        val updatedItems = list.itens.toMutableList()
        val idx = updatedItems.indexOfFirst { it.id == item.id }
        if (idx >= 0) {
            updatedItems[idx] = item
            updateList(ShoppingList(
                id = list.id,
                titulo = list.titulo,
                imageUri = list.imageUri,
                itens = updatedItems,
                userId = list.userId
            ))
        }
    }
    
    fun removeItemFromList(listId: Long, itemId: Long) {
        val list = findListByIdInternal(listId) ?: return
        val updatedItems = list.itens.toMutableList()
        updatedItems.removeAll { it.id == itemId }
        updateList(ShoppingList(
            id = list.id,
            titulo = list.titulo,
            imageUri = list.imageUri,
            itens = updatedItems,
            userId = list.userId
        ))
    }
}
