package com.example.playlistmaker.data.dto

data class SongsSearchResponse (
    val resultCount: Int,
    val results: ArrayList<SongDto>
) : Response()