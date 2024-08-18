package com.example.playlistmaker.settings.domain.interactor

import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.settings.domain.repository.DarkThemeRepository

class SettingsInteractorImpl(private val darkThemeRepository: DarkThemeRepository) : SettingsInteractor{
    override fun getThemeSettings(): ThemeSettings {
        val darkTheme = darkThemeRepository.getDarkTheme()
        return ThemeSettings(darkTheme)
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        darkThemeRepository.saveDarkTheme(settings.darkTheme)
    }
}