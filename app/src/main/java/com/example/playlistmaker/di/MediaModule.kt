package com.example.playlistmaker.di

import com.example.playlistmaker.media.data.db.converter.PlaylistDbConvertor
import com.example.playlistmaker.media.data.interactor.PlaylistsInteractorImpl
import com.example.playlistmaker.media.domain.interactor.PlaylistsInteractor
import com.example.playlistmaker.media.domain.repository.PlaylistRepository
import com.example.playlistmaker.media.data.repository.PlaylistRepositoryImpl
import com.example.playlistmaker.media.ui.favorites.FavouriteSongsViewModel
import com.example.playlistmaker.media.ui.playlists.PlaylistsViewModel
import com.example.playlistmaker.media.ui.playlists.create.CreatePlaylistViewModel
import com.example.playlistmaker.search.data.db.AppDatabase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {

    viewModel {
        FavouriteSongsViewModel(get())
    }
    viewModel {
        CreatePlaylistViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

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