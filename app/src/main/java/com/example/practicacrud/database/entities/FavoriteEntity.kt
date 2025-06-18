package com.example.practicacrud.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val itemId: String,
    val itemType: String, // "book" o "show"
    val title: String,
    val subtitle: String?,
    val imageUrl: String?,
    val addedAt: Long = System.currentTimeMillis()
)