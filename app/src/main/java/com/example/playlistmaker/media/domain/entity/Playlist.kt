package com.example.playlistmaker.media.domain.entity

data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    val coverImagePath: String?,
    val songCount: Int
)