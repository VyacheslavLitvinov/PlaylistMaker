package com.example.playlistmaker.media.data.repository

import com.example.playlistmaker.media.data.db.converter.PlaylistDbConvertor
import com.example.playlistmaker.media.data.db.entity.PlaylistSongEntity
import com.example.playlistmaker.media.domain.entity.Playlist
import com.example.playlistmaker.media.domain.repository.PlaylistRepository
import com.example.playlistmaker.search.data.db.AppDatabase
import com.example.playlistmaker.search.domain.models.Song

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val convertor: PlaylistDbConvertor
) : PlaylistRepository {

    override suspend fun getAllPlaylists(): List<Playlist> {
        return appDatabase.playlistDao().getAllPlaylistsWithTrackCounts().map { convertor.map(it) }
    }

    override suspend fun createPlaylist(playlist: Playlist): Long {
        val playlistEntity = convertor.map(playlist)
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
        return playlistEntity.id
    }

    override suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean {
        return appDatabase.playlistSongDao().isTrackInPlaylist(playlistId, trackId)
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
        val playlistTrack = PlaylistSongEntity(
            playlistId = playlistId,
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTimeMillis = trackTimeMillis,
            artworkUrl100 = artworkUrl100,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            country = country,
            previewUrl = previewUrl
        )
        appDatabase.playlistSongDao().insertPlaylistTrack(playlistTrack)
        appDatabase.playlistDao().updateSongCount(playlistId)
    }

    override suspend fun getTrackCountInPlaylist(playlistId: Long): Int {
        return appDatabase.playlistSongDao().getTrackCountInPlaylist(playlistId)
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist {
        return convertor.map(appDatabase.playlistDao().getPlaylistById(playlistId))
    }

    override suspend fun getTracksByPlaylistId(playlistId: Long): List<Song> {
        val playlistTracks = appDatabase.playlistSongDao().getPlaylistTracks(playlistId)
        return playlistTracks.map { convertor.map(it) }
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long) {
        appDatabase.playlistSongDao().deletePlaylistTrack(playlistId, trackId)
        appDatabase.playlistDao().updateSongCount(playlistId)

        val playlists = appDatabase.playlistDao().getAllPlaylistsWithTrackCounts()
        val trackInAnyPlaylist = playlists.any { playlist ->
            appDatabase.playlistSongDao().isTrackInPlaylist(playlist.id, trackId)
        }
        if (!trackInAnyPlaylist) {
            appDatabase.songDao().deleteSong(trackId)
        }
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        appDatabase.playlistDao().deletePlaylist(playlistId)
        appDatabase.playlistDao().deletePlaylistTracks(playlistId)
        val allTracks = appDatabase.songDao().getAllSongs()
        val usedTracks = appDatabase.playlistSongDao().getAllPlaylistTracks().map { it.trackId }
        val unusedTracks = allTracks.filter { it.trackId !in usedTracks }
        unusedTracks.forEach {
            appDatabase.songDao().deleteSong(it.trackId)
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = convertor.map(playlist)
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }
}