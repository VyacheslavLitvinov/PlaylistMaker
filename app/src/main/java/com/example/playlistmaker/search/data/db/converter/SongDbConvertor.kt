package com.example.playlistmaker.search.data.db.converter

import com.example.playlistmaker.search.data.db.entity.SongEntity
import com.example.playlistmaker.search.domain.models.Song

class SongDbConvertor {
    fun map(song: Song): SongEntity {
        return SongEntity(song.trackId, song.trackName, song.artistName, song.trackTimeMillis,
        song.artworkUrl100, song.collectionName, song.releaseDate, song.primaryGenreName,
        song.country, song.previewUrl
        )
    }

    fun map(song: SongEntity): Song {
        return Song(song.trackId, song.trackName, song.artistName, song.trackTimeMillis,
            song.artworkUrl100, song.collectionName, song.releaseDate, song.primaryGenreName,
            song.country, song.previewUrl
        )
    }

}