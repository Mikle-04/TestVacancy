package com.example.data.api

import com.example.domain.model.JobItem
import com.example.domain.model.Offer
import retrofit2.http.GET

interface JobApiService {
    @GET("u/0/uc?id=1z4TbeDkbfXkvgpoJprXbN85uCcD7f00r&export=download")
    suspend fun getJobs(): JobResponse
}

data class JobResponse(
    val offers: List<Offer>,
    val vacancies: List<JobItem>
){
    val totalVacancies: Int = vacancies.size
}