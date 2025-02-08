package com.example.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_jobs")
data class FavoriteJob(
    @PrimaryKey val id: String,
    val isFavorite: Boolean
)