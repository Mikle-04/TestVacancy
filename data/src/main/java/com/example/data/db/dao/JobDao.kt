package com.example.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.db.entity.FavoriteJob
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateFavoriteStatus(favoriteJob: FavoriteJob)

    @Query("SELECT * FROM favorite_jobs WHERE isFavorite = 1")
    fun getAllFavorites(): Flow<List<FavoriteJob>>
}