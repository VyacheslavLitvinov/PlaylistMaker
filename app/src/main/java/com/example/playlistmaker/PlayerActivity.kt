package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var songImage: ImageView
    private lateinit var songName: TextView
    private lateinit var songArtist: TextView
    private lateinit var durationInfoValue: TextView
    private lateinit var albumInfoValue: TextView
    private lateinit var yearInfoValue: TextView
    private lateinit var genreInfoValue: TextView
    private lateinit var countryInfoValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)

        songImage = findViewById(R.id.songImage)
        songName = findViewById(R.id.songName)
        songArtist = findViewById(R.id.songArtist)
        durationInfoValue = findViewById(R.id.durationInfoValue)
        albumInfoValue = findViewById(R.id.albumInfoValue)
        yearInfoValue = findViewById(R.id.yearInfoValue)
        genreInfoValue = findViewById(R.id.genreInfoValue)
        countryInfoValue = findViewById(R.id.countryInfoValue)

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        val songFromJson = intent.getStringExtra(SONG)
        val song = Gson().fromJson(songFromJson, Song::class.java)
        val songUrl = song.artworkUrl100.replaceAfterLast('/', IMAGE_FORMAT)

        Glide.with(this)
            .load(songUrl)
            .fitCenter()
            .placeholder(R.drawable.album_placeholder_player)
            .transform(RoundedCorners(10))
            .into(songImage)
        songName.text = song.trackName
        songArtist.text = song.artistName
        durationInfoValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(293000L)
        albumInfoValue.text = song.collectionName
        yearInfoValue.text = song.releaseDate.substring(0, 4)
        genreInfoValue.text = song.primaryGenreName
        countryInfoValue.text = song.country
    }

    private companion object{
        const val SONG = "Song"
        const val IMAGE_FORMAT = "512x512bb.jpg"
    }
}