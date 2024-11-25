package com.example.playlistmaker.media.data.repository

import com.example.playlistmaker.media.data.db.converter.PlaylistDbConvertor
import com.example.playlistmaker.media.data.db.dao.PlaylistDao
import com.example.playlistmaker.media.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.media.domain.entity.Playlist
import com.example.playlistmaker.media.domain.repository.PlaylistRepository

class PlaylistRepositoryImpl(
    private val dao: PlaylistDao,
    private val convertor: PlaylistDbConvertor
) : PlaylistRepository {

    override suspend fun getAllPlaylists(): List<Playlist> {
        return dao.getAllPlaylistsWithTrackCounts().map { convertor.map(it) }
    }

    override suspend fun createPlaylist(playlist: Playlist): Long {
        val playlistEntity = convertor.map(playlist)
        dao.insertPlaylist(playlistEntity)
        return playlistEntity.id
    }

    override suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean {
        return dao.isTrackInPlaylist(playlistId, trackId)
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        val playlistTrack = PlaylistTrackEntity(playlistId, trackId)
        dao.addTrackToPlaylist(playlistTrack)
        dao.updateSongCount(playlistId)
    }

    override suspend fun getTrackCountInPlaylist(playlistId: Long): Int {
        return dao.getTrackCountInPlaylist(playlistId)
    }

}