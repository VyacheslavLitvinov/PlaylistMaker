package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.repository.SongsRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.DarkThemeRepositoryImpl
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.api.DarkThemeRepository
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.SongsInteractor
import com.example.playlistmaker.domain.api.SongsRepository
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.SongsInteractorImpl
import com.example.playlistmaker.domain.usecase.GetDarkThemeUseCase
import com.example.playlistmaker.domain.usecase.SaveDarkThemeUseCase

object Creator {

    private lateinit var application: Application
    private const val APP_PREFERENCES = "playlist_maker_preferences"
    private fun getSongsRepository(): SongsRepository {
        return SongsRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideSongsInteractor(): SongsInteractor {
        return SongsInteractorImpl(getSongsRepository())
    }

    fun initApplication(application: Application) {
        this.application = application
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(provideSearchHistoryRepository())
    }

    fun provideSaveDarkThemeUseCase(): SaveDarkThemeUseCase {
        return SaveDarkThemeUseCase(provideDarkThemeRepository())
    }

    fun provideGetDarkThemeUseCase(): GetDarkThemeUseCase {
        return GetDarkThemeUseCase(provideDarkThemeRepository())
    }

    private fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    private fun provideSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(provideSharedPreferences())
    }

    private fun provideDarkThemeRepository(): DarkThemeRepository {
        return DarkThemeRepositoryImpl(provideSharedPreferences())
    }
}