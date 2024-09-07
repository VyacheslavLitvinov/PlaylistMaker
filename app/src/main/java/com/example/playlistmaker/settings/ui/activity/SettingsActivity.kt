package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.ui.App
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

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