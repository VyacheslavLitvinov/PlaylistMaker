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
}