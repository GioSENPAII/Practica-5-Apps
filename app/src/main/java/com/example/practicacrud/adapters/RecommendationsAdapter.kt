package com.example.practicacrud.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practicacrud.R
import com.example.practicacrud.services.RecommendationService

class RecommendationsAdapter(
    private val onItemClick: (RecommendationService.Recommendation) -> Unit,
    private val onAddToFavorites: (RecommendationService.Recommendation) -> Unit
) : RecyclerView.Adapter<RecommendationsAdapter.ViewHolder>() {

    private val recommendations = mutableListOf<RecommendationService.Recommendation>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.itemImage)
        val titleText: TextView = view.findViewById(R.id.itemTitle)
        val subtitleText: TextView = view.findViewById(R.id.itemSubtitle)
        val reasonText: TextView = view.findViewById(R.id.reasonText)
        val addButton: Button = view.findViewById(R.id.addToFavoritesButton)

        fun bind(item: RecommendationService.Recommendation) {
            titleText.text = item.title
            subtitleText.text = item.subtitle
            reasonText.text = item.reason

            // Cargar imagen
            Glide.with(itemView.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(imageView)

            addButton.setOnClickListener {
                onAddToFavorites(item)
            }

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recommendation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recommendations[position])
    }

    override fun getItemCount() = recommendations.size

    fun setRecommendations(items: List<RecommendationService.Recommendation>) {
        recommendations.clear()
        recommendations.addAll(items)
        notifyDataSetChanged()
    }
}