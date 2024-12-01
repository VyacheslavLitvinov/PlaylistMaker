package com.example.playlistmaker.media.ui.playlists.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.entity.Playlist
import com.example.playlistmaker.media.domain.interactor.PlaylistsInteractor
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val interactor: PlaylistsInteractor,
    private val playlistId: Long
) : ViewModel() {

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> get() = _playlist

    init {
        loadPlaylist()
    }

    private fun loadPlaylist() {
        viewModelScope.launch {
            val loadedPlaylist = interactor.getPlaylistById(playlistId)
            _playlist.value = loadedPlaylist
        }
    }

    fun updatePlaylist(name: String, description: String, coverImagePath: String?) {
        viewModelScope.launch {
            val updatedPlaylist = Playlist(
                id = playlistId,
                name = name,
                description = description,
                coverImagePath = coverImagePath,
                songCount = _playlist.value?.songCount ?: 0
            )
            interactor.updatePlaylist(updatedPlaylist)
        }
    }
}