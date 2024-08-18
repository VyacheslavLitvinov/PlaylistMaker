package com.example.playlistmaker.search.ui.state

import com.example.playlistmaker.search.domain.models.Song

sealed interface SearchState {

    object Loading : SearchState

    data class Content(val songs: ArrayList<Song>) : SearchState

    data class History(val songs: ArrayList<Song>) : SearchState

    object Empty : SearchState

    object Error : SearchState
}