package com.example.shoppinglist

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppinglist.adapters.CategoriesAdapter
import com.example.shoppinglist.databinding.ActivityCategoriesBinding
import com.example.shoppinglist.databinding.DialogCategoryBinding
import com.example.shoppinglist.models.Category
import com.example.shoppinglist.repository.CategoryRepository

class CategoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoriesBinding
    private lateinit var adapter: CategoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    private fun loadCategories() {
        adapter.submitList(CategoryRepository.getAllCategories())
    }

    private fun showCategoryDialog(category: Category? = null) {
        val dialogBinding = DialogCategoryBinding.inflate(layoutInflater)
        val isEditing = category != null

        dialogBinding.textDialogTitle.text = if (isEditing) "Editar Categoria" else "Nova Categoria"

        if (isEditing) {
            dialogBinding.editTextCategoryName.setText(category!!.nome)
        }

        var selectedColor = category?.cor ?: "#D32F2F"

        val colorViews = listOf(
            dialogBinding.color1 to "#D32F2F",
            dialogBinding.color2 to "#388E3C",
            dialogBinding.color3 to "#F57C00",
            dialogBinding.color4 to "#1976D2",
            dialogBinding.color5 to "#512DA8",
            dialogBinding.color6 to "#00796B",
            dialogBinding.color7 to "#7B1FA2",
            dialogBinding.color8 to "#616161"
        )

        colorViews.forEach { (view, color) ->
            view.setOnClickListener {
                selectedColor = color
                colorViews.forEach { (v, _) ->
                    v.alpha = if (v == view) 1.0f else 0.5f
                }
            }
            if (isEditing && color == category!!.cor) {
                view.alpha = 1.0f
            } else {
                view.alpha = 0.5f
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.buttonSave.setOnClickListener {
            val name = dialogBinding.editTextCategoryName.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Nome da categoria é obrigatório", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val existingCategory = CategoryRepository.findCategoryByName(name)
            if (existingCategory != null && existingCategory.id != category?.id) {
                Toast.makeText(this, "Já existe uma categoria com esse nome", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isEditing) {
                val updatedCategory = category!!.copy(nome = name, cor = selectedColor)
                CategoryRepository.updateCategory(updatedCategory)
                Toast.makeText(this, "Categoria atualizada com sucesso", Toast.LENGTH_SHORT).show()
            } else {
                val newCategory = Category(nome = name, cor = selectedColor)
                CategoryRepository.addCategory(newCategory)
                Toast.makeText(this, "Categoria criada com sucesso", Toast.LENGTH_SHORT).show()
            }

            loadCategories()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteConfirmation(category: Category) {
        AlertDialog.Builder(this)
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
