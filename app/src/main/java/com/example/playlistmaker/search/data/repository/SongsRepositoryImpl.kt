package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.db.AppDatabase
import com.example.playlistmaker.search.data.db.converter.SongDbConvertor
import com.example.playlistmaker.search.data.dto.SongDto
import com.example.playlistmaker.search.data.dto.SongsSearchResponse
import com.example.playlistmaker.search.data.dto.SongsSearchRequest
import com.example.playlistmaker.search.domain.api.SongsRepository
import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SongsRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase,
    private val songDbConvertor: SongDbConvertor,
) : SongsRepository {
    override fun searchSongs(expression: String): Flow<Resource<List<Song>>> = flow{
        val response = networkClient.doRequest(SongsSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(response.resultCode))
            }
            200 -> {
                with(response as SongsSearchResponse) {
                    val data = results.map {
                        Song(
                            trackId = it.trackId,
                            trackName = it.trackName,
                            artistName = it.artistName,
                            trackTimeMillis = it.trackTimeMillis,
                            artworkUrl100 = it.artworkUrl100,
                            collectionName = it.collectionName,
                            releaseDate = it.releaseDate,
                            primaryGenreName = it.primaryGenreName,
                            country = it.country,
                            previewUrl = it.previewUrl
                        )
                    }
                    saveSong(results)
                    emit(Resource.Success(data))
                }
            }
            else -> {
                emit(Resource.Error(response.resultCode))
            }
        }
    }

    private suspend fun saveSong(songs: List<SongDto>){
        val songsEntitys = songs.map { song -> songDbConvertor.map(song) }
        appDatabase.songDao().insertSongIntoFavorites(songsEntitys)
    }

}