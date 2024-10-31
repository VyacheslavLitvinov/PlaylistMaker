package com.example.playlistmaker.di

import com.example.playlistmaker.media.ui.favorites.FavouriteSongsViewModel
import com.example.playlistmaker.media.ui.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {

    viewModel {
        FavouriteSongsViewModel(get())
    }

    viewModel {
        PlaylistsViewModel()
    }

}