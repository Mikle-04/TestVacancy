package com.example.hhmenu.di

import com.example.data.JobRepositoryImpl
import com.example.data.api.JobApiService
import com.example.data.db.AppDatabase
import com.example.data.db.dao.JobDao
import com.example.domain.repository.JobRepository
import com.example.domain.usecase.GetFavoriteJobsUseCase
import com.example.domain.usecase.GetJobsUseCase
import com.example.domain.usecase.GetOffersUseCase
import com.example.domain.usecase.UpdateFavoriteStatusUseCase
import com.example.hhmenu.presentation.FavouriteViewModel
import com.example.hhmenu.presentation.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    // Retrofit
    single {
        Retrofit.Builder()
            .baseUrl("https://drive.usercontent.google.com/") // Базовый URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // JobApiService для работы с API
    single { get<Retrofit>().create(JobApiService::class.java) }

    // AppDatabase для работы с Room
    single { AppDatabase.getDatabase(androidContext()) }

    // JobRepository для централизованного доступа к данным
    single<JobRepository> { JobRepositoryImpl(get(), get()) }

    // Use Cases для бизнес-логики
    factory { GetJobsUseCase(get()) } // Получение списка вакансий
    factory { UpdateFavoriteStatusUseCase(get()) } // Обновление статуса "Избранное"
    factory { GetFavoriteJobsUseCase(get()) }
    factory { GetOffersUseCase(get()) }

    // ViewModel для управления состоянием UI
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel {FavouriteViewModel(get())}
}