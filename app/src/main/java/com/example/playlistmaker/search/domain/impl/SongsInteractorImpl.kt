package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.models.ConsumerData
import com.example.playlistmaker.search.domain.api.SongsInteractor
import com.example.playlistmaker.search.domain.api.SongsRepository
import com.example.playlistmaker.search.domain.models.Resource
import java.util.concurrent.Executors

class SongsInteractorImpl(private val repository: SongsRepository) : SongsInteractor {

    private val executor = Executors.newCachedThreadPool()
    override fun searchSongs(expression: String, consumer: SongsInteractor.SongsConsumer) {
        executor.execute {
            when (val response = repository.searchSongs(expression)) {
                is Resource.Success -> {
                    consumer.consume(ConsumerData.Data(response.data))
                }
                is Resource.Error -> {
                    consumer.consume(ConsumerData.Error(response.message.toString()))
                }
            }
        }
    }
}