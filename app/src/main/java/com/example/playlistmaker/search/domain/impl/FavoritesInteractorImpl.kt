package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.data.db.entity.SongEntity
import com.example.playlistmaker.search.data.db.interactor.FavoritesInteractor
import com.example.playlistmaker.search.domain.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val favoritesRepository: FavoritesRepository
): FavoritesInteractor {
    override fun addFavoritesSongs(songs: List<SongEntity>) {
        return favoritesRepository.addFavoritesSongs(songs)
    }

    override fun deleteFavoritesSong(song: SongEntity) {
        return favoritesRepository.deleteFavoritesSong(song)
    }

    override fun favoritesSongs(): Flow<List<Song>> {
        return favoritesRepository.getFavoritesSongs()
    }
}