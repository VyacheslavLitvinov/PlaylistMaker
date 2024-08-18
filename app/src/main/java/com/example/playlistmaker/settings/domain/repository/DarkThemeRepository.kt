package com.example.playlistmaker.settings.domain.repository

interface DarkThemeRepository {

    fun getDarkTheme() : Boolean

    fun saveDarkTheme(darkThemeEnabled: Boolean)

}