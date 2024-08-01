package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.api.DarkThemeRepository

class SaveDarkThemeUseCase(private val darkThemeRepository: DarkThemeRepository) {

    fun execute(darkThemeEnabled: Boolean) {
        return darkThemeRepository.saveDarkTheme(darkThemeEnabled)
    }

}