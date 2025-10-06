package com.example.shoppinglist.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinglist.models.ShoppingItem
import com.example.shoppinglist.models.ShoppingList
import com.example.shoppinglist.repository.ShoppingRepository

class ListDetailViewModel : ViewModel() {
    private val _listaSelecionada = MutableLiveData<ShoppingList?>()
    val listaSelecionada: LiveData<ShoppingList?> = _listaSelecionada
    
    private var currentListId: Long = 0L

    fun loadList(listId: Long) {
        currentListId = listId
        refresh()
    }
    
    fun refresh() {
        _listaSelecionada.value = ShoppingRepository.findListById(currentListId)
    }

    fun addItem(item: ShoppingItem) {
        ShoppingRepository.addItemToList(currentListId, item)
        refresh()
    }

    fun updateItem(item: ShoppingItem) {
        ShoppingRepository.updateItemInList(currentListId, item)
        refresh()
    }

    fun removeItem(itemId: Long) {
        ShoppingRepository.removeItemFromList(currentListId, itemId)
        refresh()
    }
}