package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.SongsInteractor
import com.example.playlistmaker.search.domain.api.SongsRepository
import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SongsInteractorImpl(private val repository: SongsRepository) : SongsInteractor {

    override fun searchSongs(expression: String): Flow<Pair<List<Song>?, Int?>> {
        return repository.searchSongs(expression).map { result ->
            when(result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }
                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}