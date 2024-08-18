package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Song

interface SongsRepository {
    fun searchSongs(expression: String): Resource<ArrayList<Song>>
}