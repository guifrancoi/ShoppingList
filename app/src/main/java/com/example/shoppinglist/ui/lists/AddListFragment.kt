package com.example.shoppinglist.ui.lists

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import com.example.shoppinglist.R
import com.example.shoppinglist.databinding.FragmentAddListBinding
import com.example.shoppinglist.models.ShoppingList
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File

class AddListFragment : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_LIST = "arg_list"
        private const val ARG_IS_EDIT_MODE = "arg_is_edit_mode"

        fun newInstance(list: ShoppingList? = null): AddListFragment {
            return AddListFragment().apply {
                arguments = bundleOf(
                    ARG_LIST to list,
                    ARG_IS_EDIT_MODE to (list != null)
                )
            }
        }
    }

    private var _binding: FragmentAddListBinding? = null
    private val binding get() = _binding!!
    
    private var isEditMode: Boolean = false
    private var listToEdit: ShoppingList? = null
    private var selectedImageUri: Uri? = null
    private var photoUri: Uri? = null
    private var onListSavedListener: ((String, String?, ShoppingList?) -> Unit)? = null

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("AddListFragment", "Permissão de câmera concedida")
            takePicture()
        } else {
            Log.d("AddListFragment", "Permissão de câmera negada")
            Toast.makeText(requireContext(), "Permissão de câmera necessária", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("AddListFragment", "Permissão concedida")
            openImagePicker()
        } else {
            Log.d("AddListFragment", "Permissão negada")
            Toast.makeText(requireContext(), "Permissão necessária para selecionar imagens", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                requireContext().contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                selectedImageUri = it
                updateImageView(it)
                Log.d("AddListFragment", "Imagem selecionada: $it")
            } catch (e: Exception) {
                Log.e("AddListFragment", "Erro ao obter permissão persistente: ${e.message}")
                selectedImageUri = it
                updateImageView(it)
            }
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && photoUri != null) {
            selectedImageUri = photoUri
            updateImageView(photoUri!!)
            Log.d("AddListFragment", "Foto tirada: $photoUri")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isEditMode = arguments?.getBoolean(ARG_IS_EDIT_MODE) ?: false
        listToEdit = arguments?.getParcelable(ARG_LIST, ShoppingList::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddListBinding.inflate(inflater, container, false)
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
        binding.tvTitle.text = if (isEditMode) "Editar lista" else "Adicionar lista"
        binding.btnAdd.text = if (isEditMode) "Salvar" else "Adicionar"
        
        binding.cardImagePlaceholder.visibility = View.VISIBLE
        binding.fabAddImage.visibility = View.VISIBLE
    }

    private fun populateFields() {
        listToEdit?.let { list ->
            binding.editTextName.setText(list.titulo)
            
            list.imageUri?.let { uriString ->
                selectedImageUri = Uri.parse(uriString)
                updateImageView(selectedImageUri!!)
            }
        }
    }

    private fun setupListeners() {
        binding.btnAdd.setOnClickListener {
            saveList()
        }
        
        binding.fabAddImage.setOnClickListener {
            showImageSourceDialog()
        }
    }

    private fun showImageSourceDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Selecionar imagem")
            .setItems(arrayOf("Tirar foto", "Escolher da galeria")) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndTakePicture()
                    1 -> checkPermissionAndPickImage()
                }
            }
            .show()
    }

    private fun checkCameraPermissionAndTakePicture() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                takePicture()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(
                    requireContext(),
                    "Precisamos de acesso à câmera para tirar fotos",
                    Toast.LENGTH_LONG
                ).show()
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun takePicture() {
        try {
            val photoFile = File(requireContext().cacheDir, "photo_${System.currentTimeMillis()}.jpg")
            photoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                photoFile
            )
            takePictureLauncher.launch(photoUri)
        } catch (e: Exception) {
            Log.e("AddListFragment", "Erro ao criar arquivo de foto: ${e.message}", e)
            Toast.makeText(requireContext(), "Erro ao abrir câmera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissionAndPickImage() {
        val permission = Manifest.permission.READ_MEDIA_IMAGES

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("AddListFragment", "Permissão já concedida")
                openImagePicker()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                Toast.makeText(
                    requireContext(),
                    "Precisamos de acesso às suas imagens para adicionar uma foto à lista",
                    Toast.LENGTH_LONG
                ).show()
                requestPermissionLauncher.launch(permission)
            }
            else -> {
                Log.d("AddListFragment", "Solicitando permissão")
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun openImagePicker() {
        Log.d("AddListFragment", "Abrindo picker de imagens")
        pickImageLauncher.launch("image/*")
    }

    private fun updateImageView(uri: Uri) {
        try {
            val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
            val bitmap = ImageDecoder.decodeBitmap(source)
            
            binding.ivListImage.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            binding.ivListImage.imageTintList = null
            
            binding.ivListImage.setImageBitmap(bitmap)
            binding.ivListImage.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            binding.ivListImage.setPadding(0, 0, 0, 0)
            Log.d("AddListFragment", "Imagem carregada no ImageView: $uri")
        } catch (e: Exception) {
            Log.e("AddListFragment", "Erro ao carregar imagem: ${e.message}", e)
            Toast.makeText(requireContext(), "Erro ao carregar imagem", Toast.LENGTH_SHORT).show()
            resetImageViewToPlaceholder()
        }
    }

    private fun resetImageViewToPlaceholder() {
        binding.ivListImage.setImageResource(R.drawable.ic_image_placeholder)
        binding.ivListImage.setBackgroundColor(0xFFE1E1E1.toInt())
        binding.ivListImage.imageTintList = android.content.res.ColorStateList.valueOf(0xFFb3b3b3.toInt())
        binding.ivListImage.scaleType = android.widget.ImageView.ScaleType.CENTER
        val padding = (32 * resources.displayMetrics.density).toInt()
        binding.ivListImage.setPadding(padding, padding, padding, padding)
    }

    private fun saveList() {
        val name = binding.editTextName.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Nome é obrigatório", Toast.LENGTH_SHORT).show()
            binding.inputLayoutName.error = "Campo obrigatório"
            return
        } else {
            binding.inputLayoutName.error = null
        }

        val imageUriString = selectedImageUri?.toString()
        Log.d("AddListFragment", "Salvando lista com URI: $imageUriString")

        if (isEditMode && listToEdit != null) {
            onListSavedListener?.invoke(name, imageUriString, listToEdit)
        } else {
            onListSavedListener?.invoke(name, imageUriString, null)
        }
        
        dismiss()
    }

    fun setOnListSavedListener(listener: (String, String?, ShoppingList?) -> Unit) {
        onListSavedListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
