package com.example.shoppinglist.ui.lists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinglist.models.ShoppingList
import com.example.shoppinglist.repository.ShoppingRepository

class ListsViewModel : ViewModel() {
    private val _listas = MutableLiveData<List<ShoppingList>>(ShoppingRepository.getAllLists())
    val listas: LiveData<List<ShoppingList>> = _listas

    fun refresh() {
        _listas.value = ShoppingRepository.getAllLists()
    }

    fun addList(titulo: String) {
        ShoppingRepository.addList(ShoppingList(titulo = titulo))
        refresh()
    }

    fun updateList(list: ShoppingList) {
        ShoppingRepository.updateList(list)
        refresh()
    }

    fun removeList(listId: Long) {
        ShoppingRepository.removeList(listId)
        refresh()
    }
}