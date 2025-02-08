package com.example.domain.usecase


import com.example.domain.model.JobItem
import com.example.domain.model.ResultWrapper
import com.example.domain.repository.JobRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteJobsUseCase(private val repository: JobRepository) {
    operator fun invoke(): Flow<ResultWrapper<List<JobItem>>> = repository.getFavoriteJobs()

    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean): ResultWrapper<Unit> {
        return repository.updateFavoriteStatus(id, isFavorite)
    }

    suspend fun getFavoritesFromDb(): List<JobItem> {
        return repository.getFavoritesFromDb()
    }
}