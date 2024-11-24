package com.example.playlistmaker.search.domain.models

data class Song (
    val trackId: Long,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Int,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?,
    var isFavorite: Boolean = false,
)