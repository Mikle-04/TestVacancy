package com.example.data
import com.example.data.db.entity.FavoriteJob
import com.example.data.api.JobApiService
import com.example.data.db.AppDatabase
import com.example.domain.model.JobItem
import com.example.domain.model.Address
import com.example.domain.model.Experience
import com.example.domain.model.Offer
import com.example.domain.model.ResultWrapper
import com.example.domain.repository.JobRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow



class JobRepositoryImpl(
    private val apiService: JobApiService,
    private val database: AppDatabase
) : JobRepository {

    private val jobDao = database.jobDao()

    override fun getJobs(): Flow<ResultWrapper<List<JobItem>>> = flow {
        emit(ResultWrapper.Loading)

        try {
            // Получаем данные через API
            val response = apiService.getJobs()

            // Получаем ID избранных вакансий из базы данных
            val favoriteIds = jobDao.getAllFavorites().first().map { it.id }

            // Извлекаем список вакансий из response.vacancies
            val jobsWithFavorites = response.vacancies.map { job ->
                job.copy(isFavorite = job.id in favoriteIds)
            }

            emit(ResultWrapper.Success(jobsWithFavorites))
        } catch (e: Exception) {
            emit(ResultWrapper.Error(e))
        }
    }

    override fun getOffers(): Flow<ResultWrapper<List<Offer>>> = flow {
        emit(ResultWrapper.Loading)

        try {
            // Получаем данные через API
            val response = apiService.getJobs()
            emit(ResultWrapper.Success(response.offers))
        } catch (e: Exception) {
            emit(ResultWrapper.Error(e))
        }
    }

    override suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean): ResultWrapper<Unit> {
        return try {
            jobDao.updateFavoriteStatus(FavoriteJob(id, isFavorite))
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error(e)
        }
    }
    override fun getFavoriteJobs(): Flow<ResultWrapper<List<JobItem>>> = flow {
        emit(ResultWrapper.Loading)

        try {
            // Получаем ID избранных вакансий из базы данных
            val favoriteIds = jobDao.getAllFavorites().first().map { it.id }

            if (favoriteIds.isEmpty()) {
                emit(ResultWrapper.Success(emptyList()))
                return@flow
            }

            // Получаем данные через API
            val response = apiService.getJobs()

            // Фильтруем только избранные вакансии
            val favoriteJobs = response.vacancies.filter { it.id in favoriteIds }.map { it.copy(isFavorite = true) }

            emit(ResultWrapper.Success(favoriteJobs))
        } catch (e: Exception) {
            emit(ResultWrapper.Error(e))
        }
    }

    override suspend fun getFavoritesFromDb(): List<JobItem> {
        return jobDao.getAllFavorites().first().map { it.toJobItem() }
    }
}

// Расширение для преобразования FavoriteJob в JobItem
private fun FavoriteJob.toJobItem(): JobItem {
    return JobItem(
        id = this.id,
        lookingNumber = null, // Дополнительные данные могут быть загружены отдельно
        title = "Заглушка", // Замените на реальные данные
        address = Address(town = "Заглушка"),
        company = "Заглушка",
        experience = Experience(previewText = "Заглушка"),
        publishedDate = "2024-01-01",
        isFavorite = this.isFavorite
    )
}