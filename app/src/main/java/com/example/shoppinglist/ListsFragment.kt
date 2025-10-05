package com.example.shoppinglist

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.viewModels
import androidx.core.widget.addTextChangedListener
import com.example.shoppinglist.adapters.ShoppingListsAdapter
import com.example.shoppinglist.databinding.FragmentListsBinding
import com.example.shoppinglist.ui.lists.ListsViewModel
import com.example.shoppinglist.ui.lists.AddListFragment
import com.example.shoppinglist.session.UserSession
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ListsFragment : Fragment() {

    private var _binding: FragmentListsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListsViewModel by viewModels()

    private lateinit var adapter: ShoppingListsAdapter
    
    private var allLists = listOf<com.example.shoppinglist.models.ShoppingList>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ShoppingListsAdapter(
            onClick = { list ->
                (activity as? MainActivity)?.openListDetail(list.id)
            },
            onEdit = { list ->
                showEditListDialog(list)
            },
            onDelete = { list ->
                showDeleteListDialog(list)
            }
        )
        binding.recyclerViewLists.adapter = adapter
        binding.recyclerViewLists.layoutManager = LinearLayoutManager(requireContext())

        binding.fabAddList.setOnClickListener {
            showCreateListDialog()
        }

        binding.ivActionIcon.setOnClickListener {
            showLogoutDialog()
        }

        binding.searchEditText.addTextChangedListener { text ->
            filterLists(text.toString())
        }

        viewModel.listas.observe(viewLifecycleOwner) { listas ->
            allLists = listas.sortedBy { it.titulo.lowercase() }
            val currentSearch = binding.searchEditText.text.toString()
            filterLists(currentSearch)
        }

        viewModel.refresh()
    }

    private fun showCreateListDialog() {
        val addListFragment = AddListFragment.newInstance()
        addListFragment.setOnListSavedListener { titulo, imageUri, _ ->
            viewModel.addList(titulo, imageUri)
        }
        addListFragment.show(parentFragmentManager, "AddListFragment")
    }

    private fun showEditListDialog(list: com.example.shoppinglist.models.ShoppingList) {
        val editListFragment = AddListFragment.newInstance(list)
        editListFragment.setOnListSavedListener { titulo, imageUri, updatedList ->
            updatedList?.let {
                val updatedListCopy = com.example.shoppinglist.models.ShoppingList(
                    id = it.id,
                    titulo = titulo,
                    imageUri = imageUri,
                    itens = it.itens.toMutableList(),
                    userId = it.userId
                )
                viewModel.updateList(updatedListCopy)
            }
        }
        editListFragment.show(parentFragmentManager, "EditListFragment")
    }

    private fun showDeleteListDialog(list: com.example.shoppinglist.models.ShoppingList) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Excluir lista")
            .setMessage("Deseja realmente excluir a lista '${list.titulo}'?\n\nTodos os ${list.itens.size} itens desta lista serão removidos permanentemente.")
            .setPositiveButton("Excluir") { _, _ ->
                viewModel.removeList(list.id)
                android.widget.Toast.makeText(
                    requireContext(),
                    "Lista '${list.titulo}' excluída com sucesso",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Sair")
            .setMessage("Deseja realmente sair da sua conta?")
            .setPositiveButton("Sim") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun logout() {
        UserSession.logout(requireContext())
        
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun filterLists(query: String) {
        val filteredLists = if (query.isBlank()) {
            allLists
        } else {
            allLists.filter { list ->
                list.titulo.contains(query, ignoreCase = true)
            }
        }
        if (filteredLists.isEmpty()) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.recyclerViewLists.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.recyclerViewLists.visibility = View.VISIBLE
        }
        adapter.submitList(filteredLists.toList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
