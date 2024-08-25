package com.example.playlistmaker.search.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Song
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(
    private val preferences: SharedPreferences,
) : SearchHistoryRepository {
    override fun addSongToSearchHistory(song: Song) : ArrayList<Song> {
        val historyItems = getSearchHistory()
        historyItems.removeIf{it.trackId == song.trackId}
        historyItems.add(0, song)
        if (historyItems.size > MAX_ITEM) {
            historyItems.removeAt(historyItems.size - 1)
        }
        return saveSongHistory(historyItems)
    }

    override fun getSearchHistory(): ArrayList<Song> {
        val json = preferences.getString(HISTORY_SONGS_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<ArrayList<Song>>() {}.type
            Gson().fromJson(json, type)
        } else {
            ArrayList()
        }
    }

    private fun saveSongHistory(song: ArrayList<Song>): ArrayList<Song> {
        val json = Gson().toJson(song)
        preferences.edit()
            .putString(HISTORY_SONGS_KEY, json)
            .apply()
        return song
    }

    override fun clearSearchHistory() {
        preferences.edit {
            remove(HISTORY_SONGS_KEY)
        }
    }

    private companion object {
        const val HISTORY_SONGS_KEY = "history_songs_key"
        const val MAX_ITEM = 10
    }
}