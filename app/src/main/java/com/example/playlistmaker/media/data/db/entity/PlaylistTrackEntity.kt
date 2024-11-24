package com.example.playlistmaker.media.data.db.entity

import androidx.room.Entity

@Entity(tableName = "playlist_songs", primaryKeys = ["playlistId", "trackId"])
data class PlaylistTrackEntity(
    val playlistId: Long,
    val trackId: Long
)