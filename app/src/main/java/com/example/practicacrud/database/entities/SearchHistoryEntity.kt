package com.example.practicacrud.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val query: String,
    val searchType: String, // "book" o "show"
    val timestamp: Long = System.currentTimeMillis()
)