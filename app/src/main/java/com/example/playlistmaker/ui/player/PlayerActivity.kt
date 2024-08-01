package com.example.playlistmaker.ui.player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Song
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private companion object{
        const val SONG = "Song"
        const val IMAGE_FORMAT = "512x512bb.jpg"
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val DELAY = 500L
    }

    private var playerState = STATE_DEFAULT

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

    private val mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING){
                timeView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                handler.postDelayed(this, DELAY)
            }

        }
    }
    private var previewUrl = ""

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
        playButton = findViewById(R.id.playButton)
        timeView = findViewById(R.id.timeView)

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
        previewUrl = song.previewUrl

        preparePlayer()

        playButton.setOnClickListener {
            playbackControl()
        }

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(timerRunnable)
        mediaPlayer.release()
    }

    private fun preparePlayer(){
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener{
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.play_button)
            handler.removeCallbacks(timerRunnable)
            playerState = STATE_PREPARED
            timeView.text = resources.getString(R.string.songTimePlaceholder)
        }
    }

    private fun startPlayer(){
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.pause_button)
        playerState = STATE_PLAYING
        handler.postDelayed(timerRunnable, DELAY)
    }

    private fun pausePlayer(){
        mediaPlayer.pause()
        handler.removeCallbacks(timerRunnable)
        playButton.setImageResource(R.drawable.play_button)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when(playerState){
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

}