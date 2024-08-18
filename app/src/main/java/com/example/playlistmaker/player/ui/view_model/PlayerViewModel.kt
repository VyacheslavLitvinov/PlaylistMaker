package com.example.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.ui.state.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val mediaPlayerInteractor: MediaPlayerInteractor) : ViewModel() {

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PLAYING = 1
        const val STATE_PAUSED = 2
        const val STATE_COMPLETE = 3
        const val DELAY = 500L
        const val FORMAT_TIME = "mm:ss"
        const val IMAGE_FORMAT = "512x512bb.jpg"
    }

    class Factory(private val mediaPlayerInteractor: MediaPlayerInteractor) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PlayerViewModel(mediaPlayerInteractor) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private val dateFormat by lazy {
        SimpleDateFormat(FORMAT_TIME, Locale.getDefault())
    }

    private val playerState = MutableLiveData(STATE_DEFAULT)
    val state: LiveData<Int> get() = playerState

    private val _currentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> = _currentTime


    fun preparePlayer(url: String) {

        mediaPlayerInteractor.prepare(url, {
            playerState.value = STATE_PAUSED
        }, {
            playerState.value = STATE_COMPLETE
            mainThreadHandler.removeCallbacks(updateTimeRunnable)
            _currentTime.value = dateFormat.format(0)
        })
    }

    fun togglePlayback() {
        if (mediaPlayerInteractor.isPlaying()) {
            pausePlayer()
        } else {
            startPlayer()
        }
    }

    fun releasePlayer() {
        mediaPlayerInteractor.releasePlayer()
        playerState.value = STATE_DEFAULT
        _currentTime.value = dateFormat.format(0)
    }

    fun startPlayer() {
        mediaPlayerInteractor.startPlayer()
        playerState.value = STATE_PLAYING
        mainThreadHandler.post(updateTimeRunnable)
    }

    fun pausePlayer() {
        if (mediaPlayerInteractor.isPlaying()) {
            mediaPlayerInteractor.pausePlayer()
            playerState.value = STATE_PAUSED
            mainThreadHandler.removeCallbacks(updateTimeRunnable)
        }
    }

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (mediaPlayerInteractor.isPlaying()) {
                val currentPosition = mediaPlayerInteractor.getCurrentPosition()
                _currentTime.value = dateFormat.format(currentPosition)
                mainThreadHandler.postDelayed(this, DELAY)
            } else {
                playerState.value = STATE_COMPLETE
                _currentTime.value = dateFormat.format(0)
                mainThreadHandler.removeCallbacks(this)
            }
        }
    }

    fun formatArtworkUrl(artworkUrl: String?): String? {
        return artworkUrl?.replaceAfterLast("/", IMAGE_FORMAT)
    }

    fun formatTime(trackTimeMillis: Int): String {
        return dateFormat.format(trackTimeMillis)
    }

    fun formatReleaseDate(releaseDate: String?): String {
        return releaseDate?.substring(0, 4) ?: "Unknown"
    }

}