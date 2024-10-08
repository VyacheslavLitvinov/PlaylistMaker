package com.example.playlistmaker.settings.data.shared

import android.content.SharedPreferences
class ThemeRepository(private val sharedPreferences: SharedPreferences) {

    fun getDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(THEME_KEY, false)
    }

    fun saveDarkTheme(darkThemeEnabled: Boolean) {
        sharedPreferences.edit().putBoolean(THEME_KEY, darkThemeEnabled).apply()
    }

    private companion object {
        const val THEME_KEY = "theme_key"
    }

}