package com.example.playlistmaker.settings.data.impl

import com.example.playlistmaker.settings.data.shared.SharedPreferenceSource
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.settings.domain.repository.SettingsRepository

class SettingsRepositoryImpl (private val sharedPreferenceSource: SharedPreferenceSource) : SettingsRepository{
    override fun getThemeSettings(): ThemeSettings {
        val darkTheme = sharedPreferenceSource.getDarkTheme()
        return ThemeSettings(darkTheme)
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        sharedPreferenceSource.saveDarkTheme(settings.darkTheme)
    }
}