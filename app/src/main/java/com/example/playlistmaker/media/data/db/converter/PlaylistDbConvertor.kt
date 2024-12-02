package com.example.playlistmaker.media.data.db.converter

import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.data.db.entity.PlaylistSongEntity
import com.example.playlistmaker.media.domain.entity.Playlist
import com.example.playlistmaker.search.data.db.entity.SongEntity
import com.example.playlistmaker.search.domain.models.Song

class PlaylistDbConvertor {
    fun map(entity: PlaylistEntity): Playlist {
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            coverImagePath = entity.coverImagePath,
            songCount = entity.songCount
        )
    }

    fun map(domain: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = domain.id,
            name = domain.name,
            description = domain.description,
            coverImagePath = domain.coverImagePath,
            songCount = domain.songCount
        )
    }

    fun map(entity: SongEntity): Song {
        return Song(
            trackId = entity.trackId,
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTimeMillis = entity.trackTimeMillis,
            artworkUrl100 = entity.artworkUrl100,
            collectionName = entity.collectionName,
            releaseDate = entity.releaseDate,
            primaryGenreName = entity.primaryGenreName,
            country = entity.country,
            previewUrl = entity.previewUrl
        )
    }

    fun map(entity: PlaylistSongEntity): Song {
        return Song(
            trackId = entity.trackId,
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTimeMillis = entity.trackTimeMillis,
            artworkUrl100 = entity.artworkUrl100,
            collectionName = entity.collectionName,
            releaseDate = entity.releaseDate,
            primaryGenreName = entity.primaryGenreName,
            country = entity.country,
            previewUrl = entity.previewUrl
        )
    }
}