package com.example.domain.repository


import com.example.domain.model.JobItem
import com.example.domain.model.Offer
import com.example.domain.model.ResultWrapper
import kotlinx.coroutines.flow.Flow

interface JobRepository {
    fun getJobs(): Flow<ResultWrapper<List<JobItem>>>
    fun getOffers():Flow<ResultWrapper<List<Offer>>>
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean) : ResultWrapper<Unit>
    fun getFavoriteJobs(): Flow<ResultWrapper<List<JobItem>>>
    suspend fun getFavoritesFromDb(): List<JobItem>

}