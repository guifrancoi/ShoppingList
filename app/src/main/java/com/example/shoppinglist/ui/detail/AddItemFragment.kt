package com.example.shoppinglist.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import com.example.shoppinglist.databinding.FragmentAddItemBinding
import com.example.shoppinglist.models.Category
import com.example.shoppinglist.models.ShoppingItem
import com.example.shoppinglist.repository.CategoryRepository
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddItemFragment : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_ITEM = "arg_item"
        private const val ARG_IS_EDIT_MODE = "arg_is_edit_mode"

        fun newInstance(item: ShoppingItem? = null): AddItemFragment {
            return AddItemFragment().apply {
                arguments = bundleOf(
                    ARG_ITEM to item,
                    ARG_IS_EDIT_MODE to (item != null)
                )
            }
        }
    }

    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!
    
    private var selectedCategory: Category? = null
    private var isEditMode: Boolean = false
    private var itemToEdit: ShoppingItem? = null
    private var onItemSavedListener: ((ShoppingItem) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isEditMode = arguments?.getBoolean(ARG_IS_EDIT_MODE) ?: false
        itemToEdit = arguments?.getParcelable(ARG_ITEM, ShoppingItem::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUnitDropdown()
        setupCategoryDropdown()
        setupViews()
        setupListeners()
        
        if (isEditMode) {
            populateFields()
        }
    }

    private fun setupUnitDropdown() {
        val units = arrayOf("un", "kg", "g", "l", "ml")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, units)
        binding.editTextUnit.setAdapter(adapter)
    }

    private fun setupCategoryDropdown() {
        val userId = com.example.shoppinglist.session.UserSession.getCurrentUserId()
        val categories = CategoryRepository.getAllCategories(userId)
        val categoryNames = categories.map { it.nome }.toTypedArray()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categoryNames)
        binding.editTextCategory.setAdapter(adapter)
        
        if (categories.isNotEmpty() && !isEditMode) {
            binding.editTextCategory.setText(categories[0].nome, false)
            selectedCategory = categories[0]
        }
        
        binding.editTextCategory.setOnItemClickListener { _, _, position, _ ->
            selectedCategory = categories[position]
        }
    }

    private fun setupViews() {
        binding.tvTitle.text = if (isEditMode) "Editar item" else "Adicionar item"
        binding.btnSave.text = if (isEditMode) "Salvar" else "Adicionar"
    }

    private fun populateFields() {
        itemToEdit?.let { item ->
            binding.editTextName.setText(item.nome)
            binding.editTextQuantity.setText(item.quantidade.toString())
            binding.editTextUnit.setText(item.unidade, false)
            
            selectedCategory = CategoryRepository.findCategoryById(item.categoryId)
            binding.editTextCategory.setText(selectedCategory?.nome ?: "", false)
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            saveItem()
        }
    }

    private fun saveItem() {
        val nome = binding.editTextName.text.toString().trim()
        val quantidadeStr = binding.editTextQuantity.text.toString().trim()
        val unidade = binding.editTextUnit.text.toString().trim()

        if (nome.isEmpty()) {
            Toast.makeText(requireContext(), "Nome é obrigatório", Toast.LENGTH_SHORT).show()
            binding.inputLayoutName.error = "Campo obrigatório"
            return
        } else {
            binding.inputLayoutName.error = null
        }

        val quantidade = quantidadeStr.toDoubleOrNull()
        if (quantidade == null || quantidade <= 0) {
            Toast.makeText(requireContext(), "Quantidade inválida", Toast.LENGTH_SHORT).show()
            binding.inputLayoutQuantity.error = "Digite um valor válido"
            return
        } else {
            binding.inputLayoutQuantity.error = null
        }

        if (unidade.isEmpty()) {
            Toast.makeText(requireContext(), "Unidade é obrigatória", Toast.LENGTH_SHORT).show()
            binding.inputLayoutUnit.error = "Campo obrigatório"
            return
        } else {
            binding.inputLayoutUnit.error = null
        }

        if (selectedCategory == null) {
            Toast.makeText(requireContext(), "Selecione uma categoria", Toast.LENGTH_SHORT).show()
            binding.inputLayoutCategory.error = "Campo obrigatório"
            return
        } else {
            binding.inputLayoutCategory.error = null
        }

        val item = if (isEditMode && itemToEdit != null) {
            ShoppingItem(
                id = itemToEdit!!.id,
                nome = nome,
                quantidade = quantidade,
                unidade = unidade,
                categoryId = selectedCategory!!.id,
                isChecked = itemToEdit!!.isChecked
            )
        } else {
            ShoppingItem(
                nome = nome,
                quantidade = quantidade,
                unidade = unidade,
                categoryId = selectedCategory!!.id
            )
        }

        onItemSavedListener?.invoke(item)
        dismiss()
    }

    fun setOnItemSavedListener(listener: (ShoppingItem) -> Unit) {
        onItemSavedListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
