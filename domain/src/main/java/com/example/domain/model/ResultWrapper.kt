package com.example.domain.model

sealed class ResultWrapper<out T> {
    data class Success<out T>(val data: T) : ResultWrapper<T>()
    data class Error(val exception: Throwable) : ResultWrapper<Nothing>()
    object Loading : ResultWrapper<Nothing>()
}