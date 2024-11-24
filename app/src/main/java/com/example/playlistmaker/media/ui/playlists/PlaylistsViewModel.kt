package com.example.playlistmaker.media.ui.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.entity.Playlist
import com.example.playlistmaker.media.domain.interactor.PlaylistsInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> get() = _playlists

    fun loadPlaylists() {
        viewModelScope.launch {
            val playlists = withContext(Dispatchers.IO) {
                playlistsInteractor.getAllPlaylists()
            }
            _playlists.postValue(playlists)
        }
    }
}