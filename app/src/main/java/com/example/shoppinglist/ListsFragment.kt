package com.example.shoppinglist

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.viewModels
import com.example.shoppinglist.adapters.ShoppingListsAdapter
import com.example.shoppinglist.databinding.FragmentListsBinding
import com.example.shoppinglist.ui.lists.ListsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ListsFragment : Fragment() {

    private var _binding: FragmentListsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListsViewModel by viewModels()

    private lateinit var adapter: ShoppingListsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ShoppingListsAdapter { list ->
            (activity as? MainActivity)?.openListDetail(list.id)
        }
        binding.recyclerViewLists.adapter = adapter
        binding.recyclerViewLists.layoutManager = LinearLayoutManager(requireContext())

        binding.fabAddList.setOnClickListener {
            showCreateListDialog()
        }

        viewModel.listas.observe(viewLifecycleOwner) { listas ->
            adapter.submitList(listas)
        }

        // refresh inicial
        viewModel.refresh()
    }

    private fun showCreateListDialog() {
        val editText = EditText(requireContext()).apply {
            hint = "Nome da lista"
            inputType = InputType.TYPE_CLASS_TEXT
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Nova lista")
            .setView(editText)
            .setPositiveButton("Criar") { _, _ ->
                val titulo = editText.text.toString().trim()
                if (titulo.isNotEmpty()) {
                    viewModel.addList(titulo)
                } else {
                    Toast.makeText(requireContext(), "Digite um nome", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
