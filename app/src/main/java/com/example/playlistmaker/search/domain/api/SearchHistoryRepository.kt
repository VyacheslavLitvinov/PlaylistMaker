package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Song

interface SearchHistoryRepository {
    fun addSongToSearchHistory(song: Song): ArrayList<Song>
    fun getSearchHistory(): ArrayList<Song>
    fun clearSearchHistory()
}