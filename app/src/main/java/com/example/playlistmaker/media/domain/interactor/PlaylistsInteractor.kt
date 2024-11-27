package com.example.playlistmaker.media.domain.interactor

import com.example.playlistmaker.media.domain.entity.Playlist
import com.example.playlistmaker.search.domain.models.Song

interface PlaylistsInteractor {
    suspend fun getAllPlaylists(): List<Playlist>
    suspend fun createPlaylist(
        name: String,
        description: String,
        coverImagePath: String?
    ): Long
    suspend fun getPlaylistById(playlistId: Long): Playlist
    suspend fun getTracksByPlaylistId(playlistId: Long): List<Song>
}