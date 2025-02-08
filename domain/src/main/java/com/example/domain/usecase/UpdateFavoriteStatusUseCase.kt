package com.example.domain.usecase

import com.example.domain.model.ResultWrapper
import com.example.domain.repository.JobRepository

class UpdateFavoriteStatusUseCase(private val repository: JobRepository) {
    suspend operator fun invoke(id: String, isFavorite: Boolean): ResultWrapper<Unit> {
        return repository.updateFavoriteStatus(id, isFavorite)
    }
}