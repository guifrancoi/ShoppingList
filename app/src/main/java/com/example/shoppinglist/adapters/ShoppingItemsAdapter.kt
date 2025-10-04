package com.example.shoppinglist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.databinding.ItemShoppingItemBinding
import com.example.shoppinglist.models.ShoppingItem
import com.example.shoppinglist.repository.CategoryRepository

class ShoppingItemsAdapter(
    private val onEdit: (ShoppingItem) -> Unit,
    private val onDelete: (ShoppingItem) -> Unit,
    private val onCheckChanged: (ShoppingItem, Boolean) -> Unit
) : ListAdapter<ShoppingItem, ShoppingItemsAdapter.ItemViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ShoppingItem>() {
            override fun areItemsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
                return oldItem == newItem
            }
            
            override fun getChangePayload(oldItem: ShoppingItem, newItem: ShoppingItem): Any? {
                return if (oldItem.copy(isChecked = newItem.isChecked) == newItem) {
                    "CHECK_CHANGED"
                } else {
                    null
                }
            }
        }
    }

    inner class ItemViewHolder(private val binding: ItemShoppingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ShoppingItem) {
            binding.tvItemName.text = item.nome
            binding.tvItemQuantity.text = item.quantidade.toString()
            binding.tvItemUnit.text = item.unidade
            
            val category = CategoryRepository.findCategoryById(item.categoryId)
            if (category != null) {
                val iconRes = CategoryRepository.getCategoryIcon(category)
                binding.ivItemIcon.setImageResource(iconRes)
            }
            
            binding.checkboxItem.setOnCheckedChangeListener(null)
            binding.checkboxItem.isChecked = item.isChecked
            
            updateItemVisualState(item.isChecked)

            binding.checkboxItem.setOnCheckedChangeListener { _, isChecked ->
                updateItemVisualState(isChecked)
                onCheckChanged(item, isChecked)
            }

            binding.root.setOnClickListener {
                onEdit(item)
            }

            binding.ivDeleteItem.setOnClickListener {
                onDelete(item)
            }
        }
        
        fun updateCheckState(isChecked: Boolean) {
            binding.checkboxItem.setOnCheckedChangeListener(null)
            binding.checkboxItem.isChecked = isChecked
            updateItemVisualState(isChecked)
            binding.checkboxItem.setOnCheckedChangeListener { _, checked ->
                updateItemVisualState(checked)
                onCheckChanged(getItem(bindingAdapterPosition), checked)
            }
        }
        
        private fun updateItemVisualState(isChecked: Boolean) {
            if (isChecked) {
                binding.tvItemName.paintFlags = binding.tvItemName.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                binding.root.setBackgroundColor(0xFFF5F5F5.toInt())
                binding.root.alpha = 0.7f
            } else {
                binding.tvItemName.paintFlags = binding.tvItemName.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.root.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                binding.root.alpha = 1.0f
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
        holder.bind(getItem(position))
    }
    
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            if (payloads[0] == "CHECK_CHANGED") {
                holder.updateCheckState(getItem(position).isChecked)
            }
        }
    }
}
