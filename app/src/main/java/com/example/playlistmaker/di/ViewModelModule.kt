package com.example.playlistmaker.di

import com.example.playlistmaker.media.ui.PlaylistInfoViewModel
import com.example.playlistmaker.media.ui.favorites.FavouriteSongsViewModel
import com.example.playlistmaker.media.ui.playlists.PlaylistsViewModel
import com.example.playlistmaker.media.ui.playlists.create.CreatePlaylistViewModel
import com.example.playlistmaker.media.ui.playlists.edit.EditPlaylistViewModel
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel{
        SearchViewModel(get(), get())
    }

    viewModel{
        PlayerViewModel(get(), get(), get())
    }

    viewModel{
        SettingsViewModel(get(), get())
    }

    viewModel{
        PlaylistInfoViewModel(get())
    }

    viewModel {
        FavouriteSongsViewModel(get())
    }

    viewModel {
        CreatePlaylistViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        EditPlaylistViewModel(get(), get())
    }

}