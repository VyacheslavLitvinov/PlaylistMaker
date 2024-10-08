package com.example.playlistmaker.search.ui.state

import com.example.playlistmaker.search.domain.models.Song

sealed interface SearchState {

    object Loading : SearchState

    data class Content(val songs: List<Song>) : SearchState

    data class History(val songs: List<Song>) : SearchState

    object Empty : SearchState

    object NotFound : SearchState

    object Error : SearchState
}