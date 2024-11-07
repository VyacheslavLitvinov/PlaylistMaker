package com.example.playlistmaker.search.data.db.interactor

import com.example.playlistmaker.search.domain.models.Song

import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {

    fun addFavoritesSongs(song: Song)
    fun deleteFavoritesSong(songId: Long)
    fun getFavoritesSongs(): Flow<List<Song>>
    fun getSongsId(): Flow<List<Song>>
    suspend fun isFavorite(trackId: Long): Boolean

}