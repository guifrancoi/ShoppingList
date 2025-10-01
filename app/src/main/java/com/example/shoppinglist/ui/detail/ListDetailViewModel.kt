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

    fun loadList(listId: Long) {
        _listaSelecionada.value = ShoppingRepository.findListById(listId)
    }

    fun addItem(item: ShoppingItem) {
        val list = _listaSelecionada.value ?: return
        ShoppingRepository.addItemToList(list.id, item)
        loadList(list.id) // recarrega
    }

    fun updateItem(item: ShoppingItem) {
        val list = _listaSelecionada.value ?: return
        ShoppingRepository.updateItemInList(list.id, item)
        loadList(list.id)
    }

    fun removeItem(itemId: Long) {
        val list = _listaSelecionada.value ?: return
        ShoppingRepository.removeItemFromList(list.id, itemId)
        loadList(list.id)
    }
}