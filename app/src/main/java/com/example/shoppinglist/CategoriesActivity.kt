package com.example.shoppinglist

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppinglist.adapters.CategoriesAdapter
import com.example.shoppinglist.databinding.ActivityCategoriesBinding
import com.example.shoppinglist.models.Category
import com.example.shoppinglist.repository.CategoryRepository
import com.example.shoppinglist.session.UserSession
import com.example.shoppinglist.ui.categories.AddCategoryFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CategoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoriesBinding
    private lateinit var adapter: CategoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupRecyclerView()
        setupListeners()
        loadCategories()
    }

    private fun setupRecyclerView() {
        adapter = CategoriesAdapter(
            onEditClick = { category -> showCategoryDialog(category) },
            onDeleteClick = { category -> showDeleteConfirmation(category) }
        )
        binding.recyclerViewCategories.adapter = adapter
        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        binding.buttonAddCategory.setOnClickListener {
            showCategoryDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        loadCategories()
    }

    private fun loadCategories() {
        val userId = UserSession.getCurrentUserId()
        adapter.submitList(CategoryRepository.getAllCategories(userId))
    }

    private fun showCategoryDialog(category: Category? = null) {
        val addCategoryFragment = AddCategoryFragment.newInstance(category)
        addCategoryFragment.setOnCategorySavedListener { updatedCategory ->
            val userId = UserSession.getCurrentUserId()
            if (category != null) {
                val existingCategory = CategoryRepository.findCategoryByName(updatedCategory.nome, userId)
                if (existingCategory != null && existingCategory.id != category.id) {
                    Toast.makeText(this, "Já existe uma categoria com esse nome", Toast.LENGTH_SHORT).show()
                    return@setOnCategorySavedListener
                }
                CategoryRepository.updateCategory(updatedCategory)
                Toast.makeText(this, "Categoria atualizada com sucesso", Toast.LENGTH_SHORT).show()
            } else {
                val existingCategory = CategoryRepository.findCategoryByName(updatedCategory.nome, userId)
                if (existingCategory != null) {
                    Toast.makeText(this, "Já existe uma categoria com esse nome", Toast.LENGTH_SHORT).show()
                    return@setOnCategorySavedListener
                }
                CategoryRepository.addCategory(updatedCategory.copy(userId = userId))
            }
            loadCategories()
        }
        addCategoryFragment.show(supportFragmentManager, "AddCategoryFragment")
    }

    private fun showDeleteConfirmation(category: Category) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirmar exclusão")
            .setMessage("Deseja realmente excluir a categoria '${category.nome}'?")
            .setPositiveButton("Excluir") { _, _ ->
                CategoryRepository.removeCategory(category.id)
                Toast.makeText(this, "Categoria excluída com sucesso", Toast.LENGTH_SHORT).show()
                loadCategories()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
