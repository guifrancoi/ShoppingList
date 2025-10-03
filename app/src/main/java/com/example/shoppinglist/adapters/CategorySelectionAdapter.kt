package com.example.shoppinglist.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.databinding.ItemCategorySelectionBinding
import com.example.shoppinglist.models.Category

class CategorySelectionAdapter(
    private val onCategorySelected: (Category) -> Unit
) : ListAdapter<Category, CategorySelectionAdapter.CategorySelectionViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategorySelectionViewHolder {
        val binding = ItemCategorySelectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategorySelectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategorySelectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategorySelectionViewHolder(
        private val binding: ItemCategorySelectionBinding
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

                val clickListener = {
                    onCategorySelected(category)
                }

                root.setOnClickListener { clickListener() }
                textCategoryName.setOnClickListener { clickListener() }
                viewCategoryColor.setOnClickListener { clickListener() }

                root.isClickable = true
                root.isFocusable = true
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
