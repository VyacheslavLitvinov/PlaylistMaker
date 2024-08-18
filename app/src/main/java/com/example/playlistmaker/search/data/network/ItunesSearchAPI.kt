package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.SongsSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesSearchAPI {

    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<SongsSearchResponse>

}