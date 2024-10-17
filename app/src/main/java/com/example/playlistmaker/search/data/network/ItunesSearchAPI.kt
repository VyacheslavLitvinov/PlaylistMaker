package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.SongsSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesSearchAPI {

    @GET("/search?entity=song")
    suspend fun search(@Query("term") text: String): SongsSearchResponse

}