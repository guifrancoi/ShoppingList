package com.example.shoppinglist.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppinglist.adapters.CategoriesAdapter
import com.example.shoppinglist.databinding.FragmentCategoriesBinding
import com.example.shoppinglist.models.Category
import com.example.shoppinglist.repository.CategoryRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupListeners() {
        binding.fabAddCategory.setOnClickListener {
            showCategoryDialog()
        }
        
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        loadCategories()
    }

    private fun loadCategories() {
        val userId = com.example.shoppinglist.session.UserSession.getCurrentUserId()
        val categories = CategoryRepository.getAllCategories(userId)
        adapter.submitList(categories)
        if (categories.isEmpty()) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.recyclerViewCategories.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.recyclerViewCategories.visibility = View.VISIBLE
        }
    }

    private fun showCategoryDialog(category: Category? = null) {
        if (category != null && category.isPadrao) {
            Toast.makeText(requireContext(), "Categorias padrão não podem ser editadas", Toast.LENGTH_SHORT).show()
            return
        }
        
        val addCategoryFragment = AddCategoryFragment.newInstance(category)
        addCategoryFragment.setOnCategorySavedListener { updatedCategory ->
            val userId = com.example.shoppinglist.session.UserSession.getCurrentUserId()
            if (category != null) {
                val existingCategory = CategoryRepository.findCategoryByName(updatedCategory.nome, userId)
                if (existingCategory != null && existingCategory.id != category.id) {
                    Toast.makeText(requireContext(), "Já existe uma categoria com esse nome", Toast.LENGTH_SHORT).show()
                    return@setOnCategorySavedListener
                }
                
                CategoryRepository.updateCategory(updatedCategory)
                Toast.makeText(requireContext(), "Categoria atualizada com sucesso", Toast.LENGTH_SHORT).show()
            } else {
                val existingCategory = CategoryRepository.findCategoryByName(updatedCategory.nome, userId)
                if (existingCategory != null) {
                    Toast.makeText(requireContext(), "Já existe uma categoria com esse nome", Toast.LENGTH_SHORT).show()
                    return@setOnCategorySavedListener
                }
                
                CategoryRepository.addCategory(updatedCategory.copy(userId = userId))
            }
            loadCategories()
        }
        addCategoryFragment.show(parentFragmentManager, "AddCategoryFragment")
    }

    private fun showDeleteConfirmation(category: Category) {
        if (category.isPadrao) {
            Toast.makeText(requireContext(), "Categorias padrão não podem ser excluídas", Toast.LENGTH_SHORT).show()
            return
        }
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirmar exclusão")
            .setMessage("Deseja realmente excluir a categoria '${category.nome}'?")
            .setPositiveButton("Excluir") { _, _ ->
                CategoryRepository.removeCategory(category.id)
                Toast.makeText(requireContext(), "Categoria excluída com sucesso", Toast.LENGTH_SHORT).show()
                loadCategories()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
