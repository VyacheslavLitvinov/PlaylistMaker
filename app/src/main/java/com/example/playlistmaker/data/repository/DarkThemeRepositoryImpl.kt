package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.example.playlistmaker.domain.api.DarkThemeRepository

class DarkThemeRepositoryImpl(private val sharedPreferences: SharedPreferences) : DarkThemeRepository {

    override fun getDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(THEME_KEY, false)
    }

    override fun saveDarkTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPreferences.edit {
            putBoolean(THEME_KEY, darkThemeEnabled)
        }
    }

    private companion object {
        private const val THEME_KEY = "theme_key"
    }
}