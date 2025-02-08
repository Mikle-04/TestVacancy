package com.example.domain.usecase

import com.example.domain.model.JobItem
import com.example.domain.model.ResultWrapper
import com.example.domain.repository.JobRepository
import kotlinx.coroutines.flow.Flow

class GetJobsUseCase(private val repository: JobRepository) {
    operator fun invoke(): Flow<ResultWrapper<List<JobItem>>> = repository.getJobs()
}