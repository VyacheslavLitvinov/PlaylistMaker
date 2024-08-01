package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Song
import com.example.playlistmaker.ui.search.SearchActivity

interface SongsInteractor {
    fun searchSongs(expression: String, consumer: SongsConsumer)
    interface SongsConsumer {
        fun consume(data: ConsumerData<ArrayList<Song>>)
    }
}