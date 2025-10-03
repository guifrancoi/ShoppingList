package com.example.shoppinglist.repository

import com.example.shoppinglist.models.Category

object CategoryRepository {
    private val categories = mutableListOf<Category>()

    init {
        categories.add(Category(id = 1L, nome = "Carnes", cor = "#D32F2F"))
        categories.add(Category(id = 2L, nome = "Verduras", cor = "#388E3C"))
        categories.add(Category(id = 3L, nome = "Frutas", cor = "#F57C00"))
        categories.add(Category(id = 4L, nome = "Laticínios", cor = "#1976D2"))
        categories.add(Category(id = 5L, nome = "Bebidas", cor = "#512DA8"))
        categories.add(Category(id = 6L, nome = "Limpeza", cor = "#00796B"))
        categories.add(Category(id = 7L, nome = "Higiene", cor = "#7B1FA2"))
        categories.add(Category(id = 8L, nome = "Outros", cor = "#616161"))
    }

    fun getAllCategories(): List<Category> = categories.toList()

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
        val idx = categories.indexOfFirst { it.id == updated.id }
        if (idx >= 0) categories[idx] = updated
    }

    fun removeCategory(categoryId: Long) {
        categories.removeAll { it.id == categoryId }
    }

    fun findCategoryById(id: Long): Category? = categories.find { it.id == id }

    fun findCategoryByName(name: String): Category? = categories.find { it.nome.equals(name, ignoreCase = true) }

    fun categoryExists(name: String): Boolean = categories.any { it.nome.equals(name, ignoreCase = true) }
}
