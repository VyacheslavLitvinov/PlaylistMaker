package com.example.playlistmaker.player.ui.activity

import android.media.MediaPlayer
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
        const val CURRENT_POSITION = "CurrentPosition"
        const val IS_PLAYING = "IsPlaying"
        const val SONG_URL = "SongUrl"
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
    private var mediaPlayer: MediaPlayer? = null
    private var songUrl: String? = null

    private var currentPosition = 0
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)

        val mediaPlayerInteractor = Creator.provideMediaPlayerInteractor()
        viewModel = ViewModelProvider(this, PlayerViewModel.Factory(mediaPlayerInteractor))[PlayerViewModel::class.java]

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
            viewModel.resetPlayer()
        }

        val songFromJson = intent.getStringExtra(SONG)
        val song = Gson().fromJson(songFromJson, Song::class.java)

        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(CURRENT_POSITION, 0)
            isPlaying = savedInstanceState.getBoolean(IS_PLAYING, false)
            songUrl = savedInstanceState.getString(SONG_URL) ?: song.previewUrl
            viewModel.preparePlayer(songUrl ?: "", startPosition = currentPosition, shouldPlay = isPlaying)
        } else {
            songUrl = song.previewUrl
            viewModel.preparePlayer(songUrl ?: "", startPosition = currentPosition, shouldPlay = isPlaying)
        }

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SONG_URL, songUrl)
        outState.putInt(CURRENT_POSITION, viewModel.getCurrentPosition())
        outState.putBoolean(IS_PLAYING, viewModel.isPlaying())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val songUrl = savedInstanceState.getString("songUrl")
        val currentPosition = savedInstanceState.getInt("currentPosition", 0)
        val isPlaying = savedInstanceState.getBoolean("isPlaying", false)
        if (songUrl != null) {
            prepareMediaPlayer(songUrl, currentPosition, isPlaying)
        }
    }

    override fun onPause() {
        super.onPause()
        releaseMediaPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
            mediaPlayer = null
        }
    }

    private fun prepareMediaPlayer(songUrl: String, startPosition: Int, shouldPlay: Boolean) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(songUrl)
            setOnPreparedListener {
                seekTo(startPosition)
                if (shouldPlay) start()
            }
            setOnErrorListener { _, _, _ -> true }
            prepareAsync()
        }
    }
}
