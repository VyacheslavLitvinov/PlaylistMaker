package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.ui.App
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : ComponentActivity() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        viewModel = ViewModelProvider(this, SettingsViewModel.getSettingsViewModelFactory())[SettingsViewModel::class.java]

        val backButton = findViewById<FrameLayout>(R.id.back_button_settings)
        backButton.setOnClickListener {
            finish()
        }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val stateSwitch = viewModel.darkTheme()
        themeSwitcher.isChecked = stateSwitch

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
            viewModel.themeSwitch(checked)
            recreate()
        }

        val shareButton = findViewById<FrameLayout>(R.id.shareButton)
        shareButton.setOnClickListener {
            viewModel.shareApp()
        }

        val helpButton = findViewById<FrameLayout>(R.id.helpButton)
        helpButton.setOnClickListener {
            viewModel.supportApp()
        }

        val termsButton = findViewById<FrameLayout>(R.id.termsButton)
        termsButton.setOnClickListener {
            viewModel.termsApp()
        }
    }
}