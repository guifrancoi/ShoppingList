package com.example.shoppinglist.adapters

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.databinding.ItemShoppingListBinding
import com.example.shoppinglist.models.ShoppingList

class ShoppingListsAdapter(
    private val onClick: (ShoppingList) -> Unit,
    private val onEdit: (ShoppingList) -> Unit,
    private val onDelete: (ShoppingList) -> Unit
) : ListAdapter<ShoppingList, ShoppingListsAdapter.ListViewHolder>(DiffCallback) {

    inner class ListViewHolder(private val binding: ItemShoppingListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.P)
        fun bind(list: ShoppingList) {
            binding.tvTitle.text = list.titulo
            binding.tvCount.text = "${list.itens.size} itens"

            if (list.imageUri != null) {
                Log.d("ShoppingListsAdapter", "Carregando imagem para '${list.titulo}': ${list.imageUri}")
                try {
                    val uri = Uri.parse(list.imageUri)
                    val source = ImageDecoder.createSource(binding.root.context.contentResolver, uri)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    binding.ivListImage.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    binding.ivListImage.imageTintList = null
                    binding.ivListImage.setImageBitmap(bitmap)
                    binding.ivListImage.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                    binding.ivListImage.setPadding(0, 0, 0, 0)
                    Log.d("ShoppingListsAdapter", "Imagem carregada com sucesso")
                } catch (e: Exception) {
                    Log.e("ShoppingListsAdapter", "Erro ao carregar imagem: ${e.message}", e)
                    setPlaceholderImage(binding)
                }
            } else {
                Log.d("ShoppingListsAdapter", "Sem imagem para '${list.titulo}', usando placeholder")
                setPlaceholderImage(binding)
            }

            binding.root.setOnClickListener {
                onClick(list)
            }

            binding.btnEdit.setOnClickListener {
                onEdit(list)
            }

            binding.btnDelete.setOnClickListener {
                onDelete(list)
            }
        }

        private fun setPlaceholderImage(binding: ItemShoppingListBinding) {
            binding.ivListImage.setImageResource(R.drawable.ic_image_placeholder)
            binding.ivListImage.setBackgroundColor(0xFFE1E1E1.toInt())
            binding.ivListImage.imageTintList = android.content.res.ColorStateList.valueOf(0xFFb3b3b3.toInt())
            binding.ivListImage.scaleType = android.widget.ImageView.ScaleType.CENTER
            val padding = (32 * binding.root.context.resources.displayMetrics.density).toInt()
            binding.ivListImage.setPadding(padding, padding, padding, padding)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemShoppingListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ListViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val list = getItem(position)
        holder.bind(list)
    }
    
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: ListViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val list = getItem(position)
            holder.bind(list)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ShoppingList>() {
            override fun areItemsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean {
                return oldItem.titulo == newItem.titulo && 
                       oldItem.imageUri == newItem.imageUri &&
                       oldItem.itens.size == newItem.itens.size
            }
            
            override fun getChangePayload(oldItem: ShoppingList, newItem: ShoppingList): Any? {
                return if (oldItem.id == newItem.id) {
                    bundleOf(
                        "titulo" to newItem.titulo,
                        "imageUri" to newItem.imageUri,
                        "itemsSize" to newItem.itens.size
                    )
                } else {
                    null
                }
            }
        }
        
        private fun bundleOf(vararg pairs: Pair<String, Any?>): Any {
            return mapOf(*pairs)
        }
    }
}
