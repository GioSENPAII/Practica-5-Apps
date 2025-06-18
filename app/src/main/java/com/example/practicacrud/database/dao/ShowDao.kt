package com.example.practicacrud.database.dao

import androidx.room.*
import com.example.practicacrud.database.entities.ShowEntity

@Dao
interface ShowDao {
    @Query("SELECT * FROM shows")
    suspend fun getAllShows(): List<ShowEntity>

    @Query("SELECT * FROM shows WHERE id = :showId")
    suspend fun getShow(showId: Int): ShowEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShows(shows: List<ShowEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShow(show: ShowEntity)

    @Query("DELETE FROM shows WHERE lastUpdated < :timestamp")
    suspend fun deleteOldShows(timestamp: Long)
}