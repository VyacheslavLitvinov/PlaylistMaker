package com.example.playlistmaker.media.data.db.converter

import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.entity.Playlist

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
}