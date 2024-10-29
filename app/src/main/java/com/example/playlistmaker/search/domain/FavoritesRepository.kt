package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.data.db.entity.SongEntity
import com.example.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun addFavoritesSongs(songs: List<SongEntity>)
    fun deleteFavoritesSong(song: SongEntity)
    fun getFavoritesSongs(): Flow<List<Song>>
}