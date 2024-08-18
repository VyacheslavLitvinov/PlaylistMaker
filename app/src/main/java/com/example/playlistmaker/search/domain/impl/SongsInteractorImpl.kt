package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.models.ConsumerData
import com.example.playlistmaker.search.domain.api.SongsInteractor
import com.example.playlistmaker.search.domain.api.SongsRepository
import com.example.playlistmaker.search.domain.models.Resource

class SongsInteractorImpl(private val repository: SongsRepository) : SongsInteractor {

    override fun searchSongs(expression: String, consumer: SongsInteractor.SongsConsumer) {
        Thread {
            val response = repository.searchSongs(expression)
            when (response) {
                is Resource.Success -> {
                    consumer.consume(ConsumerData.Data(response.data))
                }
                is Resource.Error -> {
                    consumer.consume(ConsumerData.Error(response.message.toString()))
                }
            }
        }.start()
    }
}