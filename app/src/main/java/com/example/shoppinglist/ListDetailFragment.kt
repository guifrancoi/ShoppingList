package com.example.shoppinglist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.viewModels
import com.example.shoppinglist.adapters.ShoppingItemsAdapter
import com.example.shoppinglist.databinding.DialogItemBinding
import com.example.shoppinglist.databinding.FragmentListDetailBinding
import com.example.shoppinglist.models.ShoppingItem
import com.example.shoppinglist.models.Category
import com.example.shoppinglist.repository.CategoryRepository
import com.example.shoppinglist.ui.detail.ListDetailViewModel
import com.example.shoppinglist.ui.CategoryDialogHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ListDetailFragment : Fragment() {
    companion object {
        private const val ARG_LIST_ID = "arg_list_id"
        fun newInstance(listId: Long) = ListDetailFragment().apply {
            arguments = bundleOf(ARG_LIST_ID to listId)
        }
    }

    private var _binding: FragmentListDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ListDetailViewModel by viewModels()
    private lateinit var adapter: ShoppingItemsAdapter
    private var listId: Long = 0L
    private var selectedCategory: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listId = requireArguments().getLong(ARG_LIST_ID)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        _binding = FragmentListDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ShoppingItemsAdapter(
            onEdit = { item -> showEditItemDialog(item) },
            onDelete = { item -> viewModel.removeItem(item.id) }
        )
        binding.recyclerViewItems.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewItems.adapter = adapter

        binding.fabAddItem.setOnClickListener { showAddItemDialog() }

        viewModel.listaSelecionada.observe(viewLifecycleOwner) { lista ->
            binding.toolbarDetail.title = lista?.titulo ?: "Lista"
            adapter.submitList(lista?.itens?.toList() ?: emptyList())
        }
        viewModel.loadList(listId)
    }

    private fun showAddItemDialog() {
        val dialogBinding = DialogItemBinding.inflate(layoutInflater)
        selectedCategory = null

        dialogBinding.buttonSelectCategory.setOnClickListener {
            CategoryDialogHelper.showCategorySelectionDialog(requireActivity()) { category ->
                selectedCategory = category
                dialogBinding.buttonSelectCategory.text = category.nome
            }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Novo item")
            .setView(dialogBinding.root)
            .setPositiveButton("Adicionar") { _, _ ->
                val nome = dialogBinding.editNome.text.toString().trim()
                val quantidade = dialogBinding.editQuantidade.text.toString().toDoubleOrNull() ?: 1.0
                val unidade = dialogBinding.editUnidade.text.toString().trim()

                if (nome.isEmpty()) {
                    Toast.makeText(requireContext(), "Nome é obrigatório", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (selectedCategory == null) {
                    Toast.makeText(requireContext(), "Selecione uma categoria", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val item = ShoppingItem(
                    nome = nome,
                    quantidade = quantidade,
                    unidade = unidade,
                    categoryId = selectedCategory!!.id
                )
                viewModel.addItem(item)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showEditItemDialog(item: ShoppingItem) {
        val dialogBinding = DialogItemBinding.inflate(layoutInflater)
        dialogBinding.editNome.setText(item.nome)
        dialogBinding.editQuantidade.setText(item.quantidade.toString())
        dialogBinding.editUnidade.setText(item.unidade)

        selectedCategory = CategoryRepository.findCategoryById(item.categoryId)
        dialogBinding.buttonSelectCategory.text = selectedCategory?.nome ?: "Selecionar Categoria"

        dialogBinding.buttonSelectCategory.setOnClickListener {
            CategoryDialogHelper.showCategorySelectionDialog(requireActivity()) { category ->
                selectedCategory = category
                dialogBinding.buttonSelectCategory.text = category.nome
            }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Editar item")
            .setView(dialogBinding.root)
            .setPositiveButton("Salvar") { _, _ ->
                if (selectedCategory == null) {
                    Toast.makeText(requireContext(), "Selecione uma categoria", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                item.nome = dialogBinding.editNome.text.toString().trim()
                item.quantidade = dialogBinding.editQuantidade.text.toString().toDoubleOrNull() ?: item.quantidade
                item.unidade = dialogBinding.editUnidade.text.toString().trim()
                item.categoryId = selectedCategory!!.id
                viewModel.updateItem(item)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
