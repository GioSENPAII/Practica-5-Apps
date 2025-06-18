package com.example.practicacrud.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practicacrud.R

class SearchResultsAdapter(
    private val onItemClick: (SearchItem) -> Unit,
    private val onFavoriteClick: (SearchItem) -> Unit
) : RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {

    private val items = mutableListOf<SearchItem>()

    data class SearchItem(
        val id: String,
        val title: String,
        val subtitle: String,
        val imageUrl: String?,
        val type: String, // "book" o "show"
        var isFavorite: Boolean = false
    )

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.itemImage)
        val titleText: TextView = view.findViewById(R.id.itemTitle)
        val subtitleText: TextView = view.findViewById(R.id.itemSubtitle)
        val favoriteButton: ImageButton = view.findViewById(R.id.favoriteButton)

        fun bind(item: SearchItem) {
            titleText.text = item.title
            subtitleText.text = item.subtitle

            // Cargar imagen
            Glide.with(itemView.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(imageView)

            // Configurar botón de favoritos
            updateFavoriteButton(item.isFavorite)

            favoriteButton.setOnClickListener {
                onFavoriteClick(item)
            }

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }

        private fun updateFavoriteButton(isFavorite: Boolean) {
            favoriteButton.setImageResource(
                if (isFavorite) android.R.drawable.btn_star_big_on
                else android.R.drawable.btn_star_big_off
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun setResults(newItems: List<SearchItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun clearResults() {
        items.clear()
        notifyDataSetChanged()
    }

    fun updateItem(item: SearchItem) {
        val index = items.indexOfFirst { it.id == item.id && it.type == item.type }
        if (index != -1) {
            items[index] = item
            notifyItemChanged(index)
        }
    }
}