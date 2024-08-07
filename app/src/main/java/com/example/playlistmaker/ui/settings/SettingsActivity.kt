package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        val saveDarkThemeUseCase = Creator.provideSaveDarkThemeUseCase()

        val backButton = findViewById<FrameLayout>(R.id.back_button_settings)
        backButton.setOnClickListener {
            finish()
        }

        val sharedPrefs = getSharedPreferences(STATE_SWITCH, MODE_PRIVATE)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val stateSwitch = sharedPrefs.getBoolean(STATE_SWITCH, false)
        themeSwitcher.isChecked = stateSwitch

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
            saveDarkThemeUseCase.execute(checked)
            sharedPrefs.edit()
                .putBoolean(STATE_SWITCH, checked)
                .apply()
        }

        val shareButton = findViewById<FrameLayout>(R.id.shareButton)
        shareButton.setOnClickListener {
            val shareButtonIntent = Intent(Intent.ACTION_SEND)
            shareButtonIntent.type = TYPE_SHARE_BUTTON
            shareButtonIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
            startActivity(Intent.createChooser(shareButtonIntent, SHARE_TITTLE))
        }

        val helpButton = findViewById<FrameLayout>(R.id.helpButton)
        helpButton.setOnClickListener {
            val helpButtonIntent = Intent(Intent.ACTION_SENDTO)
            helpButtonIntent.data = Uri.parse(DATA_HELP_BUTTON)
            helpButtonIntent.putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf(getString(R.string.mail_recipient))
            )
            helpButtonIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.theme_messege))
            helpButtonIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.messege))
            if (helpButtonIntent.resolveActivity(packageManager) != null) {
                startActivity(helpButtonIntent)
            } else {
                Toast.makeText(this, "Нет доступных приложений для отправки сообщения", Toast.LENGTH_SHORT).show()
            }
        }

        val termsButton = findViewById<FrameLayout>(R.id.termsButton)
        termsButton.setOnClickListener {
            val url = Uri.parse(getString(R.string.url_terms))
            val termsButtonIntent = Intent(Intent.ACTION_VIEW, url)
            startActivity(termsButtonIntent)
        }
    }

    companion object {
        const val TYPE_SHARE_BUTTON = "text/plain"
        const val DATA_HELP_BUTTON = "mailto:"
        const val SHARE_TITTLE = "Share"
        const val STATE_SWITCH = "state_switch"
    }
}