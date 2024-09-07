package com.example.playlistmaker.settings.ui.view_model

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.sharing.domain.impl.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    fun darkTheme() : Boolean {
        return settingsInteractor.getThemeSettings().darkTheme
    }

    fun themeSwitch(isEnabled: Boolean){
        settingsInteractor.updateThemeSettings(ThemeSettings(isEnabled))
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun termsApp() {
        sharingInteractor.openTerms()
    }

    fun supportApp(){
        sharingInteractor.openSupport()
    }
}