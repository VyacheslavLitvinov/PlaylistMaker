package com.example.playlistmaker.media.data.db.entity

import androidx.room.Entity

@Entity(tableName = "playlist_songs", primaryKeys = ["playlistId", "trackId"])
data class PlaylistSongEntity(
    val playlistId: Long,
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    val timeAdd: Long = System.currentTimeMillis()
)