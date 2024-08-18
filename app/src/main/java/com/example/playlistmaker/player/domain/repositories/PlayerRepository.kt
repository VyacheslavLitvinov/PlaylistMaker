package com.example.playlistmaker.player.domain.repositories

interface PlayerRepository {
    fun preparePlayer(track: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun resetPlayer()
    fun releasePlayer()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
}