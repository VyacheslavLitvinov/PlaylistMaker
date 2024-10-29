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

    override fun addFavoritesSongs(songs: List<SongEntity>) {
        appDatabase.songDao().insertSongIntoFavorites(songs)
    }

    override fun deleteFavoritesSong(song: SongEntity) {
        appDatabase.songDao().deleteSongFromFavorites(song)
    }

    override fun getFavoritesSongs(): Flow<List<Song>> = flow {
        val songs = appDatabase.songDao().getFavoritesSongs()
        emit(convertFromSongEntity(songs))
    }

    private fun convertFromSongEntity(songs: List<SongEntity>): List<Song>{
        return songs.map { song -> songDbConvertor.map(song) }
    }

}