package com.example.playlistmaker.settings.domain.repository

import com.example.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsRepository {

    fun getThemeSettings() : ThemeSettings

    fun updateThemeSettings(settings: ThemeSettings)

}