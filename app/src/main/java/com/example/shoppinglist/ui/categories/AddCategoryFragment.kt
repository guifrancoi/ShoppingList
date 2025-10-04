package com.example.shoppinglist.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import com.example.shoppinglist.databinding.FragmentAddCategoryBinding
import com.example.shoppinglist.models.Category
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddCategoryFragment : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_CATEGORY = "arg_category"
        private const val ARG_IS_EDIT_MODE = "arg_is_edit_mode"

        fun newInstance(category: Category? = null): AddCategoryFragment {
            return AddCategoryFragment().apply {
                arguments = bundleOf(
                    ARG_CATEGORY to category,
                    ARG_IS_EDIT_MODE to (category != null)
                )
            }
        }
    }

    private var _binding: FragmentAddCategoryBinding? = null
    private val binding get() = _binding!!
    
    private var isEditMode: Boolean = false
    private var categoryToEdit: Category? = null
    private var onCategorySavedListener: ((Category) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isEditMode = arguments?.getBoolean(ARG_IS_EDIT_MODE) ?: false
        categoryToEdit = arguments?.getParcelable(ARG_CATEGORY, Category::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupListeners()
        
        if (isEditMode) {
            populateFields()
        }
    }

    private fun setupViews() {
        binding.tvTitle.text = if (isEditMode) "Editar Categoria" else "Nova Categoria"
        binding.btnSave.text = if (isEditMode) "Salvar" else "Adicionar"
    }

    private fun populateFields() {
        categoryToEdit?.let { category ->
            binding.editTextCategoryName.setText(category.nome)
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            saveCategory()
        }
    }

    private fun saveCategory() {
        val nome = binding.editTextCategoryName.text.toString().trim()

        if (nome.isEmpty()) {
            Toast.makeText(requireContext(), "Nome é obrigatório", Toast.LENGTH_SHORT).show()
            binding.inputLayoutName.error = "Campo obrigatório"
            return
        } else {
            binding.inputLayoutName.error = null
        }

        val category = if (isEditMode && categoryToEdit != null) {
            Category(
                id = categoryToEdit!!.id,
                nome = nome,
                isPadrao = categoryToEdit!!.isPadrao,
                userId = categoryToEdit!!.userId
            )
        } else {
            Category(
                nome = nome
            )
        }

        onCategorySavedListener?.invoke(category)
        dismiss()
    }

    fun setOnCategorySavedListener(listener: (Category) -> Unit) {
        onCategorySavedListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
