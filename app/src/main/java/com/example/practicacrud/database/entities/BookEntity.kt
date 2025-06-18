package com.example.practicacrud.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val author: String?,
    val coverUrl: String?,
    val publishYear: Int?,
    val description: String?,
    val lastUpdated: Long = System.currentTimeMillis()
)