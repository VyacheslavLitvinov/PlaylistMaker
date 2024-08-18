package com.example.playlistmaker.settings.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)
        val themeSettings = Creator.provideSettingsInteractor().getThemeSettings()
        val isDarkThemeEnabled = themeSettings.darkTheme
        switchTheme(isDarkThemeEnabled)
    }

    fun switchTheme(isDarkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}