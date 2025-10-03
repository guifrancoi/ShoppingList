package com.example.shoppinglist.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppinglist.CategoriesActivity
import com.example.shoppinglist.adapters.CategoryManagementAdapter
import com.example.shoppinglist.adapters.CategorySelectionAdapter
import com.example.shoppinglist.databinding.DialogCategoryBinding
import com.example.shoppinglist.databinding.DialogCategorySelectionBinding
import com.example.shoppinglist.databinding.DialogManageCategoriesBinding
import com.example.shoppinglist.models.Category
import com.example.shoppinglist.repository.CategoryRepository

class CategoryDialogHelper {

    companion object {
        fun showCategorySelectionDialog(
            context: Context,
            selectedCategoryId: Long? = null,
            onCategorySelected: (Category) -> Unit
        ) {
            val dialogBinding = DialogCategorySelectionBinding.inflate(
                (context as android.app.Activity).layoutInflater
            )

            val dialog = AlertDialog.Builder(context)
                .setView(dialogBinding.root)
                .create()

            val adapter = CategorySelectionAdapter { category ->
                dialog.dismiss()
                onCategorySelected(category)
                Toast.makeText(context, "Categoria '${category.nome}' selecionada", Toast.LENGTH_SHORT).show()
            }

            dialogBinding.recyclerViewCategorySelection.adapter = adapter
            dialogBinding.recyclerViewCategorySelection.layoutManager = LinearLayoutManager(context)

            val categories = CategoryRepository.getAllCategories()
            adapter.submitList(categories)

            dialogBinding.buttonCreateNewCategory.setOnClickListener {
                dialog.dismiss()
                showCreateCategoryDialog(context) { newCategory ->
                    onCategorySelected(newCategory)
                    Toast.makeText(context, "Categoria '${newCategory.nome}' criada e selecionada", Toast.LENGTH_SHORT).show()
                }
            }

            dialogBinding.buttonManageCategories.setOnClickListener {
                dialog.dismiss()
                showManageCategoriesDialog(context) {
                    showCategorySelectionDialog(context, selectedCategoryId, onCategorySelected)
                }
            }

            dialogBinding.buttonCancelSelection.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        private fun showManageCategoriesDialog(
            context: Context,
            onClose: () -> Unit
        ) {
            val dialogBinding = DialogManageCategoriesBinding.inflate(
                (context as android.app.Activity).layoutInflater
            )

            val dialog = AlertDialog.Builder(context)
                .setView(dialogBinding.root)
                .create()

            fun refreshCategoriesList() {
                val currentAdapter = dialogBinding.recyclerViewManageCategories.adapter as? CategoryManagementAdapter
                currentAdapter?.submitList(CategoryRepository.getAllCategories())
            }

            val adapter = CategoryManagementAdapter(
                onEditClick = { category ->
                    showEditCategoryDialog(context, category) {
                        refreshCategoriesList()
                    }
                },
                onDeleteClick = { category ->
                    showDeleteConfirmationDialog(context, category) {
                        refreshCategoriesList()
                    }
                }
            )

            dialogBinding.recyclerViewManageCategories.adapter = adapter
            dialogBinding.recyclerViewManageCategories.layoutManager = LinearLayoutManager(context)

            adapter.submitList(CategoryRepository.getAllCategories())

            dialogBinding.buttonCloseManagement.setOnClickListener {
                dialog.dismiss()
                onClose()
            }

            dialog.show()
        }

        private fun showCreateCategoryDialog(
            context: Context,
            onCategoryCreated: (Category) -> Unit
        ) {
            val dialogBinding = DialogCategoryBinding.inflate(
                (context as android.app.Activity).layoutInflater
            )

            dialogBinding.textDialogTitle.text = "Adicionar"

            var selectedColor = "#D32F2F"

            val colorViews = listOf(
                dialogBinding.color1 to "#D32F2F",   // Vermelho
                dialogBinding.color2 to "#E91E63",   // Rosa
                dialogBinding.color3 to "#9C27B0",   // Roxo
                dialogBinding.color4 to "#673AB7",   // Roxo Escuro
                dialogBinding.color5 to "#3F51B5",   // Índigo
                dialogBinding.color6 to "#2196F3",   // Azul
                dialogBinding.color7 to "#03A9F4",   // Azul Claro
                dialogBinding.color8 to "#00BCD4",   // Ciano
                dialogBinding.color9 to "#009688",   // Verde-Água
                dialogBinding.color10 to "#4CAF50",  // Verde
                dialogBinding.color11 to "#8BC34A",  // Verde Claro
                dialogBinding.color12 to "#CDDC39",  // Lima
                dialogBinding.color13 to "#FFEB3B",  // Amarelo
                dialogBinding.color14 to "#FFC107",  // Âmbar
                dialogBinding.color15 to "#FF9800",  // Laranja
                dialogBinding.color16 to "#FF5722",  // Laranja Escuro
                dialogBinding.color17 to "#795548",  // Marrom
                dialogBinding.color18 to "#607D8B"   // Azul Acinzentado
            )

            colorViews.forEach { (view, color) ->
                view.setOnClickListener {
                    selectedColor = color
                    colorViews.forEach { (v, _) ->
                        v.alpha = if (v == view) 1.0f else 0.5f
                    }
                }
                view.alpha = if (color == selectedColor) 1.0f else 0.5f
            }

            val dialog = AlertDialog.Builder(context)
                .setView(dialogBinding.root)
                .create()

            dialogBinding.buttonCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialogBinding.buttonSave.setOnClickListener {
                val name = dialogBinding.editTextCategoryName.text.toString().trim()

                if (name.isEmpty()) {
                    Toast.makeText(context, "Nome da categoria é obrigatório", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (CategoryRepository.categoryExists(name)) {
                    Toast.makeText(context, "Já existe uma categoria com esse nome", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val newCategory = Category(nome = name, cor = selectedColor)
                val savedCategory = CategoryRepository.addCategory(newCategory)
                Toast.makeText(context, "Categoria criada com sucesso", Toast.LENGTH_SHORT).show()

                onCategoryCreated(savedCategory)
                dialog.dismiss()
            }

            dialog.show()
        }

        private fun showEditCategoryDialog(
            context: Context,
            category: Category,
            onCategoryUpdated: () -> Unit
        ) {
            val dialogBinding = DialogCategoryBinding.inflate(
                (context as android.app.Activity).layoutInflater
            )

            dialogBinding.textDialogTitle.text = "Editar Categoria"
            dialogBinding.editTextCategoryName.setText(category.nome)

            var selectedColor = category.cor

            val colorViews = listOf(
                dialogBinding.color1 to "#D32F2F",   // Vermelho
                dialogBinding.color2 to "#E91E63",   // Rosa
                dialogBinding.color3 to "#9C27B0",   // Roxo
                dialogBinding.color4 to "#673AB7",   // Roxo Escuro
                dialogBinding.color5 to "#3F51B5",   // Índigo
                dialogBinding.color6 to "#2196F3",   // Azul
                dialogBinding.color7 to "#03A9F4",   // Azul Claro
                dialogBinding.color8 to "#00BCD4",   // Ciano
                dialogBinding.color9 to "#009688",   // Verde-Água
                dialogBinding.color10 to "#4CAF50",  // Verde
                dialogBinding.color11 to "#8BC34A",  // Verde Claro
                dialogBinding.color12 to "#CDDC39",  // Lima
                dialogBinding.color13 to "#FFEB3B",  // Amarelo
                dialogBinding.color14 to "#FFC107",  // Âmbar
                dialogBinding.color15 to "#FF9800",  // Laranja
                dialogBinding.color16 to "#FF5722",  // Laranja Escuro
                dialogBinding.color17 to "#795548",  // Marrom
                dialogBinding.color18 to "#607D8B"   // Azul Acinzentado
            )

            colorViews.forEach { (view, color) ->
                view.setOnClickListener {
                    selectedColor = color
                    colorViews.forEach { (v, _) ->
                        v.alpha = if (v == view) 1.0f else 0.5f
                    }
                }
                view.alpha = if (color == selectedColor) 1.0f else 0.5f
            }

            val dialog = AlertDialog.Builder(context)
                .setView(dialogBinding.root)
                .create()

            dialogBinding.buttonCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialogBinding.buttonSave.setOnClickListener {
                val name = dialogBinding.editTextCategoryName.text.toString().trim()

                if (name.isEmpty()) {
                    Toast.makeText(context, "Nome da categoria é obrigatório", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val existingCategory = CategoryRepository.findCategoryByName(name)
                if (existingCategory != null && existingCategory.id != category.id) {
                    Toast.makeText(context, "Já existe uma categoria com esse nome", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val updatedCategory = category.copy(nome = name, cor = selectedColor)
                CategoryRepository.updateCategory(updatedCategory)
                Toast.makeText(context, "Categoria atualizada com sucesso", Toast.LENGTH_SHORT).show()

                onCategoryUpdated()
                dialog.dismiss()
            }

            dialog.show()
        }

        private fun showDeleteConfirmationDialog(
            context: Context,
            category: Category,
            onCategoryDeleted: () -> Unit
        ) {
            AlertDialog.Builder(context)
                .setTitle("Confirmar exclusão")
                .setMessage("Deseja realmente excluir a categoria '${category.nome}'?\n\nEsta ação não pode ser desfeita.")
                .setPositiveButton("Excluir") { _, _ ->
                    CategoryRepository.removeCategory(category.id)
                    Toast.makeText(context, "Categoria '${category.nome}' excluída com sucesso", Toast.LENGTH_SHORT).show()
                    onCategoryDeleted()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
}
