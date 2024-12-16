package com.example.playlistmaker.media.data.interactor

import com.example.playlistmaker.media.domain.entity.Playlist
import com.example.playlistmaker.media.domain.interactor.PlaylistsInteractor
import com.example.playlistmaker.media.domain.repository.PlaylistRepository
import com.example.playlistmaker.search.domain.models.Song

class PlaylistsInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistsInteractor {

    override suspend fun getAllPlaylists(): List<Playlist> {
        return playlistRepository.getAllPlaylists()
    }

    override suspend fun createPlaylist(
        name: String,
        description: String,
        coverImagePath: String?
    ): Long {
        val newPlaylist = Playlist(
            id = 0,
            name = name,
            description = description,
            coverImagePath = coverImagePath,
            songCount = 0
        )

        return playlistRepository.createPlaylist(newPlaylist)
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist {
        return playlistRepository.getPlaylistById(playlistId)
    }

    override suspend fun getTracksByPlaylistId(playlistId: Long): List<Song> {
        return playlistRepository.getTracksByPlaylistId(playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long) {
        playlistRepository.deleteTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun addTrackToPlaylist(
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
    ) {
        playlistRepository.addTrackToPlaylist(
            playlistId,
            trackId,
            trackName,
            artistName,
            trackTimeMillis,
            artworkUrl100,
            collectionName,
            releaseDate,
            primaryGenreName,
            country,
            previewUrl
        )
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistRepository.deletePlaylist(playlistId)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }
}