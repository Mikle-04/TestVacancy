package com.example.hhmenu.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.JobItem
import com.example.domain.model.ResultWrapper
import com.example.domain.usecase.GetFavoriteJobsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavouriteViewModel(
    private val getFavoriteJobsUseCase: GetFavoriteJobsUseCase
) : ViewModel() {

    // Состояние списка избранных вакансий
    private val _uiState = MutableStateFlow<ResultWrapper<List<JobItem>>>(ResultWrapper.Loading)
    val uiState: StateFlow<ResultWrapper<List<JobItem>>> = _uiState

    // Состояние ошибки
    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    init {
        fetchFavoriteJobs()
    }

    // Метод для загрузки избранных вакансий
    private fun fetchFavoriteJobs() {
        viewModelScope.launch {
            getFavoriteJobsUseCase().collect { result ->
                _uiState.value = result
            }
        }
    }

    // Метод для обновления статуса "Избранное"
    fun updateFavorite(jobId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            val result = getFavoriteJobsUseCase.updateFavoriteStatus(jobId, !isFavorite)
            when (result) {
                is ResultWrapper.Success -> {
                    // При успешном обновлении повторно загружаем данные
                    fetchFavoriteJobs()
                }
                is ResultWrapper.Error -> {
                    _errorState.value = result.exception.message ?: "Неизвестная ошибка"
                }
                else -> {}
            }
        }
    }

    // Метод для сброса состояния ошибки
    fun resetErrorState() {
        _errorState.value = null
    }
}