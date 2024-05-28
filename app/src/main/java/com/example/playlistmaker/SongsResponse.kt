package com.example.playlistmaker

data class SongsResponse (
    val resultCount: Int,
    val results: List<Song>
)