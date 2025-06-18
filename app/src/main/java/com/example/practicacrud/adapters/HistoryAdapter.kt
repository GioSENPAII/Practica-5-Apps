package com.example.practicacrud.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practicacrud.R
import com.example.practicacrud.database.entities.SearchHistoryEntity
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
    private val onItemClick: (SearchHistoryEntity) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private val historyItems = mutableListOf<SearchHistoryEntity>()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val queryText: TextView = view.findViewById(R.id.queryText)
        val typeText: TextView = view.findViewById(R.id.typeText)
        val dateText: TextView = view.findViewById(R.id.dateText)

        fun bind(item: SearchHistoryEntity) {
            queryText.text = item.query
            typeText.text = if (item.searchType == "book") "Libro" else "Serie/Película"
            dateText.text = dateFormat.format(Date(item.timestamp))

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(historyItems[position])
    }

    override fun getItemCount() = historyItems.size

    fun setHistory(items: List<SearchHistoryEntity>) {
        historyItems.clear()
        historyItems.addAll(items)
        notifyDataSetChanged()
    }
}