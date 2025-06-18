package com.example.practicacrud.database.dao

import androidx.room.*
import com.example.practicacrud.database.entities.SearchHistoryEntity

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history WHERE userId = :userId ORDER BY timestamp DESC LIMIT 50")
    suspend fun getUserSearchHistory(userId: Int): List<SearchHistoryEntity>

    @Insert
    suspend fun insertSearch(search: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE userId = :userId")
    suspend fun clearUserHistory(userId: Int)

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC")
    suspend fun getAllSearchHistory(): List<SearchHistoryEntity>
}