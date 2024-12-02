package com.example.playlistmaker.media.ui.playlists

data class PlaylistInfo(
    val name: String,
    val description: String?,
    val coverImagePath: String?,
    val songCount: Int,
    val totalDuration: String
)