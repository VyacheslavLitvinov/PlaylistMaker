package com.example.playlistmaker.di

import com.example.playlistmaker.media.ui.PlaylistInfoViewModel
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
}