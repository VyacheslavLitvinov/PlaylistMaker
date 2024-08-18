package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Song
import com.google.gson.Gson

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val SONG = "Song"
    }

    private lateinit var songImage: ImageView
    private lateinit var songName: TextView
    private lateinit var songArtist: TextView
    private lateinit var durationInfoValue: TextView
    private lateinit var albumInfoValue: TextView
    private lateinit var yearInfoValue: TextView
    private lateinit var genreInfoValue: TextView
    private lateinit var countryInfoValue: TextView
    private lateinit var playButton: ImageView
    private lateinit var timeView: TextView
    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)

        val mediaPlayerInteractor = Creator.provideMediaPlayerInteractor()
        viewModel = ViewModelProvider(this, PlayerViewModel.Factory(mediaPlayerInteractor))[PlayerViewModel::class.java]

        viewModel = ViewModelProvider(this)[PlayerViewModel::class.java]

        songImage = findViewById(R.id.songImage)
        songName = findViewById(R.id.songName)
        songArtist = findViewById(R.id.songArtist)
        durationInfoValue = findViewById(R.id.durationInfoValue)
        albumInfoValue = findViewById(R.id.albumInfoValue)
        yearInfoValue = findViewById(R.id.yearInfoValue)
        genreInfoValue = findViewById(R.id.genreInfoValue)
        countryInfoValue = findViewById(R.id.countryInfoValue)
        playButton = findViewById(R.id.playButton)
        timeView = findViewById(R.id.timeView)

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        val songFromJson = intent.getStringExtra(SONG)
        val song = Gson().fromJson(songFromJson, Song::class.java)
        viewModel.preparePlayer(song.previewUrl)

        Glide.with(this)
            .load(viewModel.formatArtworkUrl(song.artworkUrl100))
            .fitCenter()
            .placeholder(R.drawable.album_placeholder_player)
            .transform(RoundedCorners(10))
            .into(songImage)

        songName.text = song.trackName
        songArtist.text = song.artistName
        durationInfoValue.text = viewModel.formatTime(song.trackTimeMillis)
        albumInfoValue.text = song.collectionName
        yearInfoValue.text = viewModel.formatReleaseDate(song.releaseDate)
        genreInfoValue.text = song.primaryGenreName
        countryInfoValue.text = song.country

        playButton.setOnClickListener {
            viewModel.togglePlayback()
        }

        viewModel.state.observe(this) { state ->
            when (state) {
                PlayerViewModel.STATE_PLAYING -> {
                    playButton.setImageResource(R.drawable.pause_button)
                }
                PlayerViewModel.STATE_PAUSED, PlayerViewModel.STATE_COMPLETE -> {
                    playButton.setImageResource(R.drawable.play_button)
                }
            }
        }

        viewModel.currentTime.observe(this) { time ->
            timeView.text = time
        }

    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }

}