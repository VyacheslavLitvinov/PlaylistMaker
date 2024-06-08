package com.example.playlistmaker

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences(HISTORY_SONGS, Context.MODE_PRIVATE)
    fun getHistorySongs(): ArrayList<Song> {
        val json = sharedPreferences.getString(HISTORY_SONGS_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<ArrayList<Song>>() {}.type
            Gson().fromJson(json, type)
        } else {
            ArrayList()
        }
    }

    fun saveSongHistory (song: ArrayList<Song>): ArrayList<Song> {
        val json = Gson().toJson(song)
        sharedPreferences.edit()
            .putString(HISTORY_SONGS_KEY,json)
            .apply()
        return song
    }
    fun checkHistory (song: Song): ArrayList<Song> {
        val historyItems = getHistorySongs()
        historyItems.removeIf{it.trackId == song.trackId}
        historyItems.add(0,song)
        if (historyItems.size > MAX_ITEM) {
            historyItems.removeAt(historyItems.size-1)
        }
        return saveSongHistory(historyItems)
    }
    fun clearHistory(){
        sharedPreferences.edit()
            .remove(HISTORY_SONGS_KEY)
            .apply()
    }
    companion object {
        const val HISTORY_SONGS_KEY = "history_songs_key"
        const val HISTORY_SONGS = "history_songs"
        const val MAX_ITEM = 10
    }
}