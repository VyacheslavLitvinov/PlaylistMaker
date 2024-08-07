package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.SongsSearchResponse
import com.example.playlistmaker.data.dto.SongsSearchRequest
import com.example.playlistmaker.domain.api.SongsRepository
import com.example.playlistmaker.domain.models.Resource
import com.example.playlistmaker.domain.models.Song

class SongsRepositoryImpl(private val networkClient: NetworkClient) : SongsRepository {
    override fun searchSongs(expression: String): Resource<ArrayList<Song>> {
        val response = networkClient.doRequest(SongsSearchRequest(expression))
        return if (response is SongsSearchResponse) {
            val songList = response.results.map { songDto ->
                Song(
                    trackId = songDto.trackId,
                    trackName = songDto.trackName,
                    artistName = songDto.artistName,
                    trackTimeMillis = songDto.trackTimeMillis,
                    artworkUrl100 = songDto.artworkUrl100,
                    collectionName = songDto.collectionName,
                    releaseDate = songDto.releaseDate,
                    primaryGenreName = songDto.primaryGenreName,
                    country = songDto.country,
                    previewUrl = songDto.previewUrl
                )
            }
            Resource.Success(ArrayList(songList))
        } else {
            Resource.Error(response.resultCode)
        }
    }
}