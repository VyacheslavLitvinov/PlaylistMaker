package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.ConsumerData
import com.example.playlistmaker.search.domain.models.Song

interface SongsInteractor {
    fun searchSongs(expression: String, consumer: SongsConsumer)
    interface SongsConsumer {
        fun consume(data: ConsumerData<ArrayList<Song>>)
    }
}