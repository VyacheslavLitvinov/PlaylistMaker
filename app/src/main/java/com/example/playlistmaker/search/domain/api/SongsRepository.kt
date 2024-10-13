package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.Flow

interface SongsRepository {
    fun searchSongs(expression: String): Flow<Resource<List<Song>>>
}