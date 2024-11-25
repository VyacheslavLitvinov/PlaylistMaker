package com.example.playlistmaker.media.domain.interactor

import com.example.playlistmaker.media.domain.entity.Playlist

interface PlaylistsInteractor {
    suspend fun getAllPlaylists(): List<Playlist>
    suspend fun createPlaylist(
        name: String,
        description: String,
        coverImagePath: String?
    ): Long
}