package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Song

class SearchHistoryInteractorImpl(private val searchHistoryRepository: SearchHistoryRepository) : SearchHistoryInteractor {
    override fun addSongToSearchHistory(song: Song) {
        searchHistoryRepository.addSongToSearchHistory(song)
    }

    override fun getSearchHistory(): ArrayList<Song> {
        return searchHistoryRepository.getSearchHistory()
    }

    override fun clearSearchHistory() {
        searchHistoryRepository.clearSearchHistory()
    }
}