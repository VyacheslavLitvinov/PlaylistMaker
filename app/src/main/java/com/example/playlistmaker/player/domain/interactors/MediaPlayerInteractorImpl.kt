package com.example.playlistmaker.player.domain.interactors

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor

class MediaPlayerInteractorImpl(private val mediaPlayer: MediaPlayer) : MediaPlayerInteractor {

    private var isPrepared = false
    override fun prepare(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        try {
            if (isPrepared) {
                mediaPlayer.reset()
            }
            mediaPlayer.setDataSource(trackUrl)
            mediaPlayer.setOnPreparedListener {
                isPrepared = true
                onPrepared()
            }
            mediaPlayer.setOnCompletionListener {
                onCompletion()
                mediaPlayer.seekTo(0)
            }
            mediaPlayer.prepareAsync()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun resetPlayer() {
        try {
            if (isPrepared) {
                mediaPlayer.reset()
                isPrepared = false
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun releasePlayer() {
        mediaPlayer.release()
        isPrepared = false
    }

    override fun isPlaying(): Boolean {
        return isPrepared && mediaPlayer.isPlaying
    }

    override fun getCurrentPosition(): Int {
        return if (isPrepared) mediaPlayer.currentPosition else 0
    }

    override fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
    }
}