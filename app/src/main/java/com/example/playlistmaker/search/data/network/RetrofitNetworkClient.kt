package com.example.playlistmaker.search.data.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.SongsSearchRequest
import java.io.IOException

class RetrofitNetworkClient(
    private val connectivityManager: ConnectivityManager,
    private val iTunesService : ItunesSearchAPI,
) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        return try {
            if (!isConnected()){
                return Response(resultCode = 500)
            }
            if (dto is SongsSearchRequest) {
                val resp = iTunesService.search(dto.expression).execute()
                val body = resp.body() ?: Response()
                body.apply { resultCode = resp.code() }
            } else {
                Response(resultCode = 400)
            }
        } catch (e: IOException) {
            Response(resultCode = 500)
        }
    }

    private fun isConnected(): Boolean {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}