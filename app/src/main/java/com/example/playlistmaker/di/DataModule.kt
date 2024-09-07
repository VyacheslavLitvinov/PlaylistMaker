package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.example.playlistmaker.Constants
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.network.ItunesSearchAPI
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.settings.data.shared.ThemeRepository
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    val baseUrl = "https://itunes.apple.com"
    val appPreferences = "playlist_maker_preferences"

    single<ItunesSearchAPI> {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesSearchAPI::class.java)
    }

    single<SharedPreferences> {
        androidContext()
            .getSharedPreferences(appPreferences, Context.MODE_PRIVATE)
    }

    single<ThemeRepository> {
        ThemeRepository(get())
    }

    factory { Gson() }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), get())
    }

    single<ConnectivityManager> {
        androidContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

}