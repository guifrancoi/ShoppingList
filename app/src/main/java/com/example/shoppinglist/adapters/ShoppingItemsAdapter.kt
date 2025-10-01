package com.example.shoppinglist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.databinding.ItemShoppingItemBinding
import com.example.shoppinglist.models.ShoppingItem

class ShoppingItemsAdapter(
    private val onEdit: (ShoppingItem) -> Unit,
    private val onDelete: (ShoppingItem) -> Unit
) : ListAdapter<ShoppingItem, ShoppingItemsAdapter.ItemViewHolder>(DiffCallback) {

    inner class ItemViewHolder(private val binding: ItemShoppingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ShoppingItem) {
            binding.tvItemName.text = item.nome
            binding.tvItemQuantity.text = "${item.quantidade} ${item.unidade}"
            binding.tvItemCategory.text = item.categoria

            // Clique para editar
            binding.root.setOnClickListener {
                onEdit(item)
            }

            // Clique longo para excluir
            binding.root.setOnLongClickListener {
                onDelete(item)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemShoppingItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ShoppingItem>() {
            override fun areItemsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean =
                oldItem == newItem
        }
    }
}
