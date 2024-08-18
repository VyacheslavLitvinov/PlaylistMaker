package com.example.playlistmaker.search.data.dto

data class SongsSearchResponse (
    val results: ArrayList<SongDto>
) : Response()