package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(THEME_STATE, Context.MODE_PRIVATE)
        val darkThemeEnabled = sharedPreferences.getBoolean(THEME_KEY, false)
        switchTheme(darkThemeEnabled)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        sharedPreferences.edit().putBoolean(THEME_KEY, darkThemeEnabled).apply()
    }

    companion object {
        private const val THEME_STATE = "theme_state"
        private const val THEME_KEY = "theme_key"
    }

}