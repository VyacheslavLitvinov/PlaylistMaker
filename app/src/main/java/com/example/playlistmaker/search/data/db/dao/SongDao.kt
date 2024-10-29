package com.example.playlistmaker.search.data.db.dao

import android.text.method.MovementMethod
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.search.data.db.entity.SongEntity

@Dao
interface SongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSongIntoFavorites(songs: List<SongEntity>)

    @Delete(entity = SongEntity::class)
    fun deleteSongFromFavorites(song: SongEntity)

    @Query("SELECT * FROM song_table")
    fun getFavoritesSongs(): List<SongEntity>

    @Query("SELECT * FROM song_table WHERE trackId LIKE :id")
    fun favoritesSongsById(id: Int): SongEntity

}