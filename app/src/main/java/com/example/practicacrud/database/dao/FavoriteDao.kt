package com.example.practicacrud.database.dao

import androidx.room.*
import com.example.practicacrud.database.entities.FavoriteEntity

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY addedAt DESC")
    suspend fun getUserFavorites(userId: Int): List<FavoriteEntity>

    @Query("SELECT * FROM favorites WHERE userId = :userId AND itemId = :itemId AND itemType = :itemType")
    suspend fun getFavorite(userId: Int, itemId: String, itemType: String): FavoriteEntity?

    @Insert
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND itemId = :itemId AND itemType = :itemType")
    suspend fun deleteFavoriteByIds(userId: Int, itemId: String, itemType: String)

    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    suspend fun getAllFavorites(): List<FavoriteEntity>
}