package com.example.playlistmaker.player.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val mediaPlayerInteractor: MediaPlayerInteractor) : ViewModel() {

    companion object {
        const val DEFAULT_TIMER = 0
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

    private var isPlayerPrepared = false
    private val isPrepared: Boolean
        get() = isPlayerPrepared

    private var songUrl: String? = null

    fun preparePlayer(url: String, startPosition: Int = 0, shouldPlay: Boolean = false) {
        songUrl = url
        mediaPlayerInteractor.prepare(url, {
            isPlayerPrepared = true
            seekTo(startPosition)
            if (shouldPlay) {
                startPlayer()
            } else {
                playerState.value = STATE_PAUSED
            }
        }, {
            isPlayerPrepared = false
            playerState.value = STATE_COMPLETE
            mainThreadHandler.removeCallbacks(updateTimeRunnable)
            _currentTime.postValue(formatTime(DEFAULT_TIMER))
        })
    }

    private fun startPlayer() {
        if (isPrepared) {
            mediaPlayerInteractor.startPlayer()
            playerState.value = STATE_PLAYING
            mainThreadHandler.post(updateTimeRunnable)
        } else {
            preparePlayer(songUrl ?: "", startPosition = getCurrentPosition(), shouldPlay = true)
        }
    }

    private fun pausePlayer() {
        mediaPlayerInteractor.pausePlayer()
        playerState.value = STATE_PAUSED
        mainThreadHandler.removeCallbacks(updateTimeRunnable)
    }

    fun resetPlayer() {
        mediaPlayerInteractor.resetPlayer()
        playerState.value = STATE_DEFAULT
        mainThreadHandler.removeCallbacks(updateTimeRunnable)
        _currentTime.postValue(formatTime(DEFAULT_TIMER))
    }

    fun isPlaying(): Boolean {
        return mediaPlayerInteractor.isPlaying()
    }

    fun getCurrentPosition(): Int {
        return mediaPlayerInteractor.getCurrentPosition()
    }

    private fun seekTo(position: Int) {
        mediaPlayerInteractor.seekTo(position)
    }

    fun togglePlayback() {
        when (playerState.value) {
            STATE_PLAYING -> pausePlayer()
            STATE_PAUSED, STATE_COMPLETE -> startPlayer()
        }
    }

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            val currentPosition = mediaPlayerInteractor.getCurrentPosition()
            _currentTime.postValue(formatTime(currentPosition))
            mainThreadHandler.postDelayed(this, DELAY)
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