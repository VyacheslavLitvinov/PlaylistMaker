package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Song

interface SearchHistoryRepository {
    fun addSongToSearchHistory(song: Song): ArrayList<Song>
    fun getSearchHistory(): ArrayList<Song>
    fun clearSearchHistory()
}