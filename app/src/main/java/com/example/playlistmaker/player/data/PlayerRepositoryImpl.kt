package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.repositories.PlayerRepository

class PlayerRepositoryImpl(private val mediaPlayerInteractor: MediaPlayerInteractor) : PlayerRepository {

    override fun preparePlayer(track: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayerInteractor.prepare(track, onPrepared, onCompletion)
    }

    override fun startPlayer() {
        mediaPlayerInteractor.startPlayer()
    }

    override fun pausePlayer() {
        mediaPlayerInteractor.pausePlayer()
    }

    override fun resetPlayer() {
        mediaPlayerInteractor.resetPlayer()
    }

    override fun releasePlayer() {
        mediaPlayerInteractor.releasePlayer()
    }

    override fun isPlaying(): Boolean {
        return mediaPlayerInteractor.isPlaying()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayerInteractor.getCurrentPosition()
    }
}