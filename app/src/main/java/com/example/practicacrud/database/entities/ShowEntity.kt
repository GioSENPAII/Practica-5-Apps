package com.example.practicacrud.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shows")
data class ShowEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val type: String?,
    val language: String?,
    val genres: String?, // JSON string
    val status: String?,
    val runtime: Int?,
    val premiered: String?,
    val imageUrl: String?,
    val summary: String?,
    val lastUpdated: Long = System.currentTimeMillis()
)