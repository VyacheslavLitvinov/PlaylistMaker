package com.example.playlistmaker.domain.api

interface DarkThemeRepository {

    fun getDarkTheme() : Boolean

    fun saveDarkTheme(darkThemeEnabled: Boolean)

}