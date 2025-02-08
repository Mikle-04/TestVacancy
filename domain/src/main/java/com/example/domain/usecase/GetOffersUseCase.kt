package com.example.domain.usecase

import com.example.domain.model.Offer
import com.example.domain.model.ResultWrapper
import com.example.domain.repository.JobRepository
import kotlinx.coroutines.flow.Flow

class GetOffersUseCase(private val repository: JobRepository) {
    operator fun invoke(): Flow<ResultWrapper<List<Offer>>> = repository.getOffers()
}