package com.example.domain.model

data class JobItem(
    val id: String,
    val lookingNumber: Int?, // Количество просмотров
    val title: String, // Название вакансии
    val address: Address, // Адрес
    val company: String, // Название компании
    val experience: Experience, // Опыт работы
    val publishedDate: String, // Дата публикации
    val isFavorite: Boolean // Флаг избранного
)

data class Address(
    val town: String // Город
)

data class Experience(
    val previewText: String // Текстовое представление опыта
)