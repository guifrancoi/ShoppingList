package com.example.shoppinglist.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.databinding.ItemCategoryBinding
import com.example.shoppinglist.models.Category

class CategoryManagementAdapter(
    private val onEditClick: (Category) -> Unit,
    private val onDeleteClick: (Category) -> Unit
) : ListAdapter<Category, CategoryManagementAdapter.CategoryManagementViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryManagementViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryManagementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryManagementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryManagementViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.apply {
                textCategoryName.text = category.nome

                try {
                    val color = Color.parseColor(category.cor)
                    viewCategoryColor.setBackgroundColor(color)
                } catch (e: IllegalArgumentException) {
                    viewCategoryColor.setBackgroundColor(Color.parseColor("#6200EE"))
                }

                buttonEditCategory.setOnClickListener {
                    onEditClick(category)
                }

                buttonDeleteCategory.setOnClickListener {
                    onDeleteClick(category)
                }

                root.setOnClickListener {
                    onEditClick(category)
                }
            }
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
}
