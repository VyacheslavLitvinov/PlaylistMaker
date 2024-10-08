package com.example.playlistmaker.di

import com.example.playlistmaker.media.ui.FavouriteSongsViewModel
import com.example.playlistmaker.media.ui.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {

    viewModel {
        FavouriteSongsViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }

}