package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Song

interface SearchHistoryInteractor {
    fun addSongToSearchHistory(song: Song)
    fun getSearchHistory(): ArrayList<Song>
    fun clearSearchHistory()
}