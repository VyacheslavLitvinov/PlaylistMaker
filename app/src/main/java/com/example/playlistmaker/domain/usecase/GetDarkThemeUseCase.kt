package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.api.DarkThemeRepository

class GetDarkThemeUseCase(private val darkThemeRepository: DarkThemeRepository) {

    fun execute(): Boolean {
        return darkThemeRepository.getDarkTheme()
    }

}