package com.example.hhmenu.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.JobItem
import com.example.domain.model.Offer
import com.example.domain.model.ResultWrapper
import com.example.domain.usecase.GetJobsUseCase
import com.example.domain.usecase.GetOffersUseCase
import com.example.domain.usecase.UpdateFavoriteStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val getJobsUseCase: GetJobsUseCase,
    private val getOffersUseCase: GetOffersUseCase,
    private val updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase
) : ViewModel() {

    private var countVacancy = 0

    // Состояние списка вакансий
    private val _uiState = MutableStateFlow<ResultWrapper<List<JobItem>>>(ResultWrapper.Loading)
    val uiState: StateFlow<ResultWrapper<List<JobItem>>> = _uiState

    // Флаг для отображения всех вакансий
    var showAllVacancies = false

    // Состояние предложений
    private val _offersState = MutableStateFlow<ResultWrapper<List<Offer>>>(ResultWrapper.Loading)
    val offersState: StateFlow<ResultWrapper<List<Offer>>> = _offersState

    // Состояние ошибки
    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    init {
        fetchJobs()
        fetchOffers()
    }

    fun showAllVacancies() {
        showAllVacancies = true
        fetchJobs()
    }

    // Метод для загрузки вакансий
    private fun fetchJobs() {
        viewModelScope.launch {
            getJobsUseCase().collect { result ->
                _uiState.value = when (result) {
                    is ResultWrapper.Loading -> ResultWrapper.Loading
                    is ResultWrapper.Success -> {
                        if (!showAllVacancies) {
                            countVacancy = result.data.size
                            ResultWrapper.Success(result.data.take(3)) // Показываем первые 3 вакансии
                        } else {
                            ResultWrapper.Success(result.data) // Показываем все вакансии
                        }
                    }
                    is ResultWrapper.Error -> ResultWrapper.Error(result.exception)
                }
            }
        }
    }

    fun getCountVacancy() : Int{
        return countVacancy
    }

    // Метод для загрузки предложений
    private fun fetchOffers() {
        viewModelScope.launch {
            getOffersUseCase().collect { result ->
                _offersState.value = result
            }
        }
    }

    // Метод для обновления статуса "Избранное"
    fun updateFavorite(jobId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            val result = updateFavoriteStatusUseCase(jobId, !isFavorite)

            when (result) {
                is ResultWrapper.Success -> {
                    // Обработка успешного результата
                    _uiState.value = ResultWrapper.Success(updateJobListWithNewFavoriteStatus(jobId, !isFavorite))
                }
                is ResultWrapper.Error -> {
                    // Обработка ошибки
                    _errorState.value = result.exception.message ?: "Неизвестная ошибка"
                }

                else ->{}
            }
        }
    }

    // Метод для обновления статуса "Избранное" в списке вакансий
    private fun updateJobListWithNewFavoriteStatus(jobId: String, newIsFavorite: Boolean): List<JobItem> {
        return (_uiState.value as? ResultWrapper.Success)?.data?.map { job ->
            if (job.id == jobId) {
                job.copy(isFavorite = newIsFavorite)
            } else {
                job
            }
        } ?: emptyList()
    }

    // Метод для показа всех вакансий
    fun showMoreVacancies(totalVacancies: Int) {
        showAllVacancies = true
        fetchJobs()
    }

    // Метод для форматирования текста кнопки "Еще (число) вакансий"
    fun formatShowMoreButton(totalVacancies: Int): String {
        return when {
            totalVacancies % 10 == 1 && totalVacancies % 100 != 11 -> "Еще $totalVacancies вакансия"
            totalVacancies % 10 in 2..4 && !(totalVacancies % 100 in 12..14) -> "Еще $totalVacancies вакансии"
            else -> "Еще $totalVacancies вакансий"
        }
    }

    // Метод для сброса состояния ошибки
    fun resetErrorState() {
        _errorState.value = null
    }
}