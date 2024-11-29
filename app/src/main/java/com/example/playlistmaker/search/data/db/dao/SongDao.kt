package com.example.playlistmaker.search.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.search.data.db.entity.SongEntity

@Dao
interface SongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSongIntoFavorites(song: SongEntity)

    @Query("DELETE FROM song_table WHERE trackId = :songId")
    fun deleteSongFromFavorites(songId: Long)

    @Query("SELECT * FROM song_table")
    fun getFavoritesSongs(): List<SongEntity>

    @Query("SELECT * FROM song_table WHERE trackId = :id")
    suspend fun favoritesSongsById(id: Long): SongEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM song_table WHERE trackId = :trackId LIMIT 1)")
    fun isFavorite(trackId: Long): Boolean

    @Query("SELECT * FROM song_table WHERE trackId IN (:trackIds)")
    fun getAllSongs(trackIds: List<Long>): List<SongEntity>

    @Query("SELECT * FROM song_table WHERE trackId = :id")
    suspend fun getSongById(id: Long): SongEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongEntity)
}