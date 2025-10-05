package com.example.shoppinglist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.viewModels
import com.example.shoppinglist.adapters.ShoppingItemsAdapter
import com.example.shoppinglist.databinding.FragmentListDetailBinding
import com.example.shoppinglist.models.ShoppingItem
import com.example.shoppinglist.repository.CategoryRepository
import com.example.shoppinglist.ui.detail.ListDetailViewModel
import com.example.shoppinglist.ui.detail.AddItemFragment
import com.example.shoppinglist.ui.categories.CategoriesFragment

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
    private var allItems = listOf<ShoppingItem>()

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
            onDelete = { item -> viewModel.removeItem(item.id) },
            onCheckChanged = { item, isChecked ->
                val updatedItem = ShoppingItem(
                    id = item.id,
                    nome = item.nome,
                    quantidade = item.quantidade,
                    unidade = item.unidade,
                    categoryId = item.categoryId,
                    isChecked = isChecked
                )
                viewModel.updateItem(updatedItem)
            }
        )
        binding.recyclerViewItems.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewItems.adapter = adapter

        binding.fabAddItem.setOnClickListener { showAddItemDialog() }
        
        binding.btnCategories.setOnClickListener {
            navigateToCategoriesFragment()
        }

        binding.searchEditText.addTextChangedListener { text ->
            filterItems(text.toString())
        }

        viewModel.listaSelecionada.observe(viewLifecycleOwner) { lista ->
            binding.toolbarDetail.text = lista?.titulo ?: "Lista"
            allItems = lista?.itens?.toList() ?: emptyList()
            val currentSearch = binding.searchEditText.text.toString()
            filterItems(currentSearch)
        }
        
        viewModel.loadList(listId)
    }

    private fun filterItems(query: String) {
        val filteredItems = if (query.isBlank()) {
            allItems
        } else {
            allItems.filter { item ->
                item.nome.contains(query, ignoreCase = true)
            }
        }
        val sortedItems = filteredItems.sortedWith(
            compareBy<ShoppingItem> { it.isChecked }
                .thenBy { item -> 
                    CategoryRepository.findCategoryById(item.categoryId)?.nome ?: "Sem categoria"
                }
                .thenBy { it.nome.lowercase() }
        )
        if (sortedItems.isEmpty()) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.recyclerViewItems.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.recyclerViewItems.visibility = View.VISIBLE
        }
        adapter.submitList(sortedItems.toList())
    }

    private fun showAddItemDialog() {
        val addItemFragment = AddItemFragment.newInstance()
        addItemFragment.setOnItemSavedListener { item ->
            viewModel.addItem(item)
        }
        addItemFragment.show(parentFragmentManager, "AddItemFragment")
    }

    private fun showEditItemDialog(item: ShoppingItem) {
        val editItemFragment = AddItemFragment.newInstance(item)
        editItemFragment.setOnItemSavedListener { updatedItem ->
            viewModel.updateItem(updatedItem)
        }
        editItemFragment.show(parentFragmentManager, "EditItemFragment")
    }

    private fun navigateToCategoriesFragment() {
        val categoriesFragment = CategoriesFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, categoriesFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
