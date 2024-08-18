package com.example.playlistmaker.settings.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.model.ThemeSettings

class SettingsViewModel : ViewModel() {


    private val sharingInteractor = Creator.provideSharingInteractor()
    private val settingsInteractor = Creator.provideSettingsInteractor()


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

    companion object {
        fun getSettingsViewModelFactory() : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel()
            }
        }
    }

}