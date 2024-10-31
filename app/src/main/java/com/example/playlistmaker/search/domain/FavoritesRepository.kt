package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun addFavoritesSongs(song: Song)
    fun deleteFavoritesSong(songId: Long)
    fun getFavoritesSongs(): Flow<List<Song>>
    fun getSongsId(): Flow<List<Song>>
    suspend fun isFavorite(trackId: Long): Boolean
}