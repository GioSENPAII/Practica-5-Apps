package com.example.practicacrud.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Int,
    val username: String,
    val role: String,
    val profilePicture: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)