package com.example.shoppinglist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.databinding.ItemShoppingListBinding
import com.example.shoppinglist.models.ShoppingList


class ShoppingListsAdapter(
    private val onClick: (ShoppingList) -> Unit
) : ListAdapter<ShoppingList, ShoppingListsAdapter.ListViewHolder>(DiffCallback) {

    inner class ListViewHolder(private val binding: ItemShoppingListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(list: ShoppingList) {
            binding.tvTitle.text = list.titulo
            binding.tvCount.text = "${list.itens.size} itens"

            // Clique no card abre detalhe
            binding.root.setOnClickListener {
                onClick(list)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemShoppingListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val list = getItem(position)
        holder.bind(list)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ShoppingList>() {
            override fun areItemsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean =
                oldItem == newItem
        }
    }
}
