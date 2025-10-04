package com.example.shoppinglist.repository

import com.example.shoppinglist.R
import com.example.shoppinglist.models.Category

object CategoryRepository {
    private val categories = mutableListOf<Category>()

    private val defaultCategoryIds = setOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L)

    init {
        categories.add(Category(id = 1L, nome = "Carnes", isPadrao = true))
        categories.add(Category(id = 2L, nome = "Legumes", isPadrao = true))
        categories.add(Category(id = 3L, nome = "Frutas", isPadrao = true))
        categories.add(Category(id = 4L, nome = "Laticínios", isPadrao = true))
        categories.add(Category(id = 5L, nome = "Bebidas", isPadrao = true))
        categories.add(Category(id = 6L, nome = "Limpeza", isPadrao = true))
        categories.add(Category(id = 7L, nome = "Higiene", isPadrao = true))
        categories.add(Category(id = 8L, nome = "Verduras", isPadrao = true))
        categories.add(Category(id = 9L, nome = "Massas", isPadrao = true))
        categories.add(Category(id = 10L, nome = "Outros", isPadrao = true))
    }

    fun getCategoryIcon(category: Category): Int {
        return when {
            category.nome.equals("Frutas", ignoreCase = true) -> R.drawable.apple
            category.nome.equals("Legumes", ignoreCase = true) -> R.drawable.carrot
            category.nome.equals("Bebidas", ignoreCase = true) -> R.drawable.cocktail
            category.nome.equals("Limpeza", ignoreCase = true) -> R.drawable.cleaning
            category.nome.equals("Verduras", ignoreCase = true) -> R.drawable.lettuce
            category.nome.equals("Laticínios", ignoreCase = true) -> R.drawable.cheese
            category.nome.equals("Higiene", ignoreCase = true) -> R.drawable.shot_glass
            category.nome.equals("Carnes", ignoreCase = true) -> R.drawable.chicken_leg
            category.nome.equals("Massas", ignoreCase = true) -> R.drawable.bread
            else -> R.drawable.ic_category
        }
    }

    fun getAllCategories(userId: String? = null): List<Category> {
        return if (userId != null) {
            categories.filter { it.isPadrao || it.userId == userId }.toList()
        } else {
            categories.filter { it.isPadrao }.toList()
        }
    }

    fun addCategory(category: Category): Category {
        val newCategory = if (category.id == 0L || categories.any { it.id == category.id }) {
            category.copy(id = System.currentTimeMillis())
        } else {
            category
        }
        categories.add(newCategory)
        return newCategory
    }

    fun updateCategory(updated: Category) {
        if (updated.isPadrao) return
        
        val idx = categories.indexOfFirst { it.id == updated.id }
        if (idx >= 0) categories[idx] = updated
    }

    fun removeCategory(categoryId: Long) {
        if (categoryId in defaultCategoryIds) return
        
        categories.removeAll { it.id == categoryId }
    }

    fun findCategoryById(id: Long): Category? = categories.find { it.id == id }

    fun findCategoryByName(name: String, userId: String? = null): Category? {
        return if (userId != null) {
            categories.find { 
                it.nome.equals(name, ignoreCase = true) && (it.isPadrao || it.userId == userId)
            }
        } else {
            categories.find { it.nome.equals(name, ignoreCase = true) && it.isPadrao }
        }
    }

    fun categoryExists(name: String, userId: String? = null): Boolean {
        return if (userId != null) {
            categories.any { 
                it.nome.equals(name, ignoreCase = true) && (it.isPadrao || it.userId == userId)
            }
        } else {
            categories.any { it.nome.equals(name, ignoreCase = true) && it.isPadrao }
        }
    }
}
