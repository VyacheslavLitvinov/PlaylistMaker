package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.data.db.interactor.FavoritesInteractor
import com.example.playlistmaker.search.domain.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val favoritesRepository: FavoritesRepository
): FavoritesInteractor {
    override fun addFavoritesSongs(song: Song) {
        return favoritesRepository.addFavoritesSongs(song)
    }

    override fun deleteFavoritesSong(songId: Long) {
        return favoritesRepository.deleteFavoritesSong(songId)
    }

    override fun getFavoritesSongs(): Flow<List<Song>> {
        return favoritesRepository.getFavoritesSongs()
    }

    override fun getSongsId(): Flow<List<Song>> {
        return favoritesRepository.getSongsId()
    }

    override suspend fun isFavorite(trackId: Long): Boolean {
        return favoritesRepository.isFavorite(trackId)
    }
}