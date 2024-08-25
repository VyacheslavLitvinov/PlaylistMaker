package com.example.playlistmaker.settings.data.impl

import com.example.playlistmaker.settings.data.shared.SharedPreferenceSource
import com.example.playlistmaker.settings.domain.repository.DarkThemeRepository

class DarkThemeRepositoryImpl(private val sharedPreferencesSource: SharedPreferenceSource) :
    DarkThemeRepository {

    override fun getDarkTheme(): Boolean {
        return sharedPreferencesSource.getDarkTheme()
    }

    override fun saveDarkTheme(darkThemeEnabled: Boolean) {
        sharedPreferencesSource.saveDarkTheme(darkThemeEnabled)
    }
}