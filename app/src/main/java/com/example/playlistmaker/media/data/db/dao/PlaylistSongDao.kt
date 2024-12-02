package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.entity.PlaylistSongEntity

@Dao
interface PlaylistSongDao {
    @Query("SELECT * FROM playlist_songs WHERE playlistId = :playlistId ORDER BY timeAdd ASC")
    suspend fun getPlaylistTracks(playlistId: Long): List<PlaylistSongEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistTrack(playlistTrack: PlaylistSongEntity)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deletePlaylistTrack(playlistId: Long, trackId: Long)

    @Query("SELECT COUNT(*) FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun getTrackCountInPlaylist(playlistId: Long): Int

    @Query("SELECT EXISTS(SELECT 1 FROM playlist_songs WHERE playlistId = :playlistId AND trackId = :trackId)")
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean
    @Query("SELECT * FROM playlist_songs")
    suspend fun getAllPlaylistTracks(): List<PlaylistSongEntity>
}