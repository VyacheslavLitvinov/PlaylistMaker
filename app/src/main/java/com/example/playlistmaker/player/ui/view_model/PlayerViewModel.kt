package com.example.playlistmaker.player.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.models.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val mediaPlayerInteractor: MediaPlayerInteractor) : ViewModel() {

    companion object {
        const val DEFAULT_TIMER = 0
        const val DELAY = 300L
        const val FORMAT_TIME = "mm:ss"
        const val IMAGE_FORMAT = "512x512bb.jpg"
    }

    private var timerJob: Job? = null
    private val dateFormat = SimpleDateFormat(FORMAT_TIME, Locale.getDefault())
    private val playerState = MutableLiveData(PlayerState.STATE_DEFAULT)
    val state: LiveData<PlayerState> get() = playerState

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
            _currentTime.postValue(formatTime(startPosition))
            if (shouldPlay) {
                startPlayer()
            } else {
                playerState.value = PlayerState.STATE_PAUSED
            }
        }, {
            isPlayerPrepared = false
            playerState.value = PlayerState.STATE_COMPLETE
            timerJob?.cancel()
            _currentTime.postValue(formatTime(DEFAULT_TIMER))
        })
    }

    private fun startPlayer() {
        if (isPrepared) {
            mediaPlayerInteractor.startPlayer()
            playerState.value = PlayerState.STATE_PLAYING
            startTimer()
        } else {
            preparePlayer(songUrl ?: "", startPosition = getCurrentPosition(), shouldPlay = true)
        }
    }

    fun pausePlayer() {
        mediaPlayerInteractor.pausePlayer()
        timerJob?.cancel()
        playerState.value = PlayerState.STATE_PAUSED
    }

    fun resetPlayer() {
        mediaPlayerInteractor.resetPlayer()
        playerState.value = PlayerState.STATE_DEFAULT
        timerJob?.cancel()
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
            PlayerState.STATE_PLAYING -> pausePlayer()
            PlayerState.STATE_PAUSED, PlayerState.STATE_COMPLETE -> startPlayer()
            PlayerState.STATE_DEFAULT -> Unit
            null -> Unit
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

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayerInteractor.isPlaying()){
                delay(DELAY)
                val currentPosition = mediaPlayerInteractor.getCurrentPosition()
                _currentTime.postValue(formatTime(currentPosition))
                playerState.postValue(PlayerState.STATE_PLAYING)
            }
        }
    }

}