package com.example.playlistmaker.ui.settings

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.usecase.GetDarkThemeUseCase

class App : Application() {

    private lateinit var getDarkThemeUseCase: GetDarkThemeUseCase

    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)
        getDarkThemeUseCase = Creator.provideGetDarkThemeUseCase()
        val isDarkThemeEnabled = getDarkThemeUseCase.execute()
        switchTheme(isDarkThemeEnabled)
    }

    fun switchTheme(isDarkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}