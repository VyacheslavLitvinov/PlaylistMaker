package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.db.AppDatabase
import com.example.playlistmaker.search.data.db.converter.SongDbConvertor
import com.example.playlistmaker.search.data.db.entity.SongEntity
import com.example.playlistmaker.search.domain.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val songDbConvertor: SongDbConvertor,
): FavoritesRepository {
    override fun addFavoritesSongs(song: Song) {
        appDatabase.songDao().insertSongIntoFavorites(songDbConvertor.map(song))
    }

    override fun deleteFavoritesSong(songId: Long) {
        appDatabase.songDao().deleteSongFromFavorites(songId)
    }

    override fun getFavoritesSongs(): Flow<List<Song>> = flow {
        val songs = appDatabase.songDao().getFavoritesSongs().reversed()
        emit(convertFromSongEntity(songs))
    }

    override fun getSongsId(): Flow<List<Song>> = flow {
        appDatabase.songDao().getFavoritesSongs()
    }

    override suspend fun isFavorite(trackId: Long): Boolean {
        return appDatabase.songDao().isFavorite(trackId)
    }

    private fun convertFromSongEntity(songs: List<SongEntity>): List<Song>{
        return songs.map(songDbConvertor::map)
    }

}