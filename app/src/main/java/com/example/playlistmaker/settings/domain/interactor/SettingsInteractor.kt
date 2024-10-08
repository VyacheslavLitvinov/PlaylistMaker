package com.example.playlistmaker.settings.domain.interactor

import com.example.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsInteractor {

    fun getThemeSettings() : ThemeSettings

    fun updateThemeSettings(settings: ThemeSettings)
}