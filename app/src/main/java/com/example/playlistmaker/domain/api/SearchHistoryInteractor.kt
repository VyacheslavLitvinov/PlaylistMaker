package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Song

interface SearchHistoryInteractor {
    fun addSongToSearchHistory(song: Song)
    fun getSearchHistory(): ArrayList<Song>
    fun clearSearchHistory()
}