package com.example.playlistmaker.di

import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.SongsRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.SongsRepository
import com.example.playlistmaker.settings.data.impl.DarkThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.repository.DarkThemeRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<SongsRepository> {
        SongsRepositoryImpl(get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get())
    }

    single<DarkThemeRepository> {
        DarkThemeRepositoryImpl(get())
    }

}