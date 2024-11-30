package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.interactor.PlaylistsInteractor
import com.example.playlistmaker.media.ui.playlists.PlaylistInfo
import com.example.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistInfoViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _playlistInfo = MutableLiveData<PlaylistInfo>()
    val playlistInfo: LiveData<PlaylistInfo> get() = _playlistInfo

    private val _tracks = MutableLiveData<List<Song>>()
    val tracks: LiveData<List<Song>> get() = _tracks

    fun loadPlaylistInfo(playlistId: Long) {
        viewModelScope.launch {
            val playlist = withContext(Dispatchers.IO) {
                playlistsInteractor.getPlaylistById(playlistId)
            }
            val tracks = withContext(Dispatchers.IO) {
                playlistsInteractor.getTracksByPlaylistId(playlistId)
            }
            val totalDuration = tracks.sumOf { it.trackTimeMillis }
            val formattedDuration = SimpleDateFormat("mm", Locale.getDefault()).format(totalDuration)

            _playlistInfo.postValue(
                PlaylistInfo(
                    name = playlist.name,
                    description = playlist.description,
                    coverImagePath = playlist.coverImagePath,
                    songCount = playlist.songCount,
                    totalDuration = formattedDuration
                )
            )
            _tracks.postValue(tracks)
        }
    }

    fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                playlistsInteractor.deleteTrackFromPlaylist(playlistId, trackId)
                loadPlaylistInfo(playlistId)
            }
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistsInteractor.deletePlaylist(playlistId)
        }
    }
}