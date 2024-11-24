package com.example.playlistmaker.media.domain.repository

import com.example.playlistmaker.media.domain.entity.Playlist

interface PlaylistRepository {
    suspend fun getAllPlaylists(): List<Playlist>
    suspend fun createPlaylist(playlist: Playlist): Long
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long)
    suspend fun getTrackCountInPlaylist(playlistId: Long): Int
}