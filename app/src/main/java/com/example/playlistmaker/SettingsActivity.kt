package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<FrameLayout>(R.id.back_button_settings)
        backButton.setOnClickListener{
            finish()
        }

        val shareButton = findViewById<FrameLayout>(R.id.shareButton)
        shareButton.setOnClickListener{
            val shareButtonIntent = Intent(Intent.ACTION_SEND)
            shareButtonIntent.type = TYPE_SHARE_BUTTON
            shareButtonIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
            startActivity(Intent.createChooser(shareButtonIntent, SHARE_TITTLE))
        }

        val helpButton = findViewById<FrameLayout>(R.id.helpButton)
        helpButton.setOnClickListener{
            val helpButtonIntent = Intent(Intent.ACTION_SENDTO)
            helpButtonIntent.data = Uri.parse(DATA_HELP_BUTTON)
            helpButtonIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail_recipient)))
            helpButtonIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.theme_messege))
            helpButtonIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.messege))
            startActivity(helpButtonIntent)
        }

        val termsButton = findViewById<FrameLayout>(R.id.termsButton)
        termsButton.setOnClickListener{
            val url = Uri.parse(getString(R.string.url_terms))
            val termsButtonIntent = Intent(Intent.ACTION_VIEW, url)
            startActivity(termsButtonIntent)
        }
    }
    companion object{
        const val TYPE_SHARE_BUTTON = "text/plain"
        const val DATA_HELP_BUTTON = "mailto:"
        const val SHARE_TITTLE = "Share"
    }
}