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
    suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long)
    suspend fun addTrackToPlaylist(
        playlistId: Long,
        trackId: Long,
        trackName: String,
        artistName: String,
        trackTimeMillis: Int,
        artworkUrl100: String,
        collectionName: String,
        releaseDate: String,
        primaryGenreName: String,
        country: String,
        previewUrl: String
    )

    suspend fun deletePlaylist(playlistId: Long)

}