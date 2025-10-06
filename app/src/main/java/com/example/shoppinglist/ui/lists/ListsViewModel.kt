package com.example.shoppinglist.ui.lists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinglist.models.ShoppingList
import com.example.shoppinglist.repository.ShoppingRepository
import com.example.shoppinglist.session.UserSession

class ListsViewModel : ViewModel() {
    private val _listas = MutableLiveData<List<ShoppingList>>()
    val listas: LiveData<List<ShoppingList>> = _listas

    init {
        refresh()
    }

    fun refresh() {
        val userId = UserSession.getCurrentUserId()
        if (userId != null) {
            _listas.value = ShoppingRepository.getAllLists(userId).toList()
        } else {
            _listas.value = emptyList()
        }
    }

    fun addList(titulo: String, imageUri: String? = null) {
        val userId = UserSession.getCurrentUserId() ?: return
        ShoppingRepository.addList(ShoppingList(titulo = titulo, imageUri = imageUri, userId = userId))
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