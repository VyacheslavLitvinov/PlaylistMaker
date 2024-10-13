package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.Flow

interface SongsInteractor {
    fun searchSongs(expression: String): Flow<Pair<List<Song>?, Int?>>
}