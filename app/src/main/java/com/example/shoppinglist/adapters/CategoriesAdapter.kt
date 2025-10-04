package com.example.shoppinglist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.databinding.ItemCategoryBinding
import com.example.shoppinglist.models.Category
import com.example.shoppinglist.repository.CategoryRepository

class CategoriesAdapter(
    private val onEditClick: (Category) -> Unit,
    private val onDeleteClick: (Category) -> Unit
) : ListAdapter<Category, CategoriesAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.apply {
                textCategoryName.text = category.nome

                ivCategoryIcon.setImageResource(CategoryRepository.getCategoryIcon(category))

                if (category.isPadrao) {
                    buttonEditCategory.visibility = View.GONE
                    buttonDeleteCategory.visibility = View.GONE
                } else {
                    buttonEditCategory.visibility = View.VISIBLE
                    buttonDeleteCategory.visibility = View.VISIBLE
                    
                    buttonEditCategory.setOnClickListener {
                        onEditClick(category)
                    }

                    buttonDeleteCategory.setOnClickListener {
                        onDeleteClick(category)
                    }
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
