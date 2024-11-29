package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.data.db.entity.PlaylistSongEntity
import com.example.playlistmaker.search.data.db.entity.SongEntity

@Dao
interface PlaylistDao {
    @Query("""
    SELECT playlists.*,
           (SELECT COUNT(*) FROM playlist_songs WHERE playlistId = playlists.id) AS songCount
    FROM playlists
    """)
    suspend fun getAllPlaylistsWithTrackCounts(): List<PlaylistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("SELECT EXISTS(SELECT 1 FROM playlist_songs WHERE playlistId = :playlistId AND trackId = :trackId)")
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToPlaylist(playlistTrack: PlaylistSongEntity)

    @Query("SELECT COUNT(*) FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun getTrackCountInPlaylist(playlistId: Long): Int

    @Query("UPDATE playlists SET songCount = (SELECT COUNT(*) FROM playlist_songs WHERE playlistId = :playlistId) WHERE id = :playlistId")
    suspend fun updateSongCount(playlistId: Long)

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity

    @Query("SELECT * FROM song_table WHERE trackId IN (SELECT trackId FROM playlist_songs WHERE playlistId = :playlistId)")
    suspend fun getTracksByPlaylistId(playlistId: Long): List<SongEntity>

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long)

    @Query("DELETE FROM song_table WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Long)

}