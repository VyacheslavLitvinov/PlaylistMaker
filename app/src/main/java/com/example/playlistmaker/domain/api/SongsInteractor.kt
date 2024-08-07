package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.ConsumerData
import com.example.playlistmaker.domain.models.Song

interface SongsInteractor {
    fun searchSongs(expression: String, consumer: SongsConsumer)
    interface SongsConsumer {
        fun consume(data: ConsumerData<ArrayList<Song>>)
    }
}