package com.example.playlistmaker.search.data.db.interactor

import com.example.playlistmaker.search.data.db.entity.SongEntity
import com.example.playlistmaker.search.domain.models.Song

import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {

    fun addFavoritesSongs(songs: List<SongEntity>)
    fun deleteFavoritesSong(song: SongEntity)
    fun favoritesSongs(): Flow<List<Song>>

}