package com.example.playlistmaker.di

import com.example.playlistmaker.media.data.db.converter.PlaylistDbConvertor
import com.example.playlistmaker.media.data.interactor.PlaylistsInteractorImpl
import com.example.playlistmaker.media.domain.interactor.PlaylistsInteractor
import com.example.playlistmaker.media.domain.repository.PlaylistRepository
import com.example.playlistmaker.media.data.repository.PlaylistRepositoryImpl
import com.example.playlistmaker.search.data.db.AppDatabase
import org.koin.dsl.module

val mediaModule = module {

    single {
        get<AppDatabase>().playlistDao()
    }

    single {
        PlaylistDbConvertor()
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get())
    }

    single<PlaylistsInteractor> {
        PlaylistsInteractorImpl(get())
    }
}