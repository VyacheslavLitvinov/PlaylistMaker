package com.example.playlistmaker.media.domain

import com.example.playlistmaker.search.domain.models.Song

sealed interface FavouriteSongState {
    data class Content(val songs: List<Song>) : FavouriteSongState

    data object Empty : FavouriteSongState
}