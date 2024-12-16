package com.example.playlistmaker.media.ui.playlists.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.interactor.PlaylistsInteractor
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val interactor: PlaylistsInteractor,
    private val playlistId: Long? = null
) : ViewModel() {

    private val _playlistCreationStatus = MutableLiveData<Boolean>()
    val playlistCreationStatus: LiveData<Boolean> get() = _playlistCreationStatus

    fun createPlaylist(name: String, description: String, coverImagePath: String?) {
        viewModelScope.launch {
            try {
                val playlistId = interactor.createPlaylist(name, description, coverImagePath)
                if (playlistId > 0) {
                    _playlistCreationStatus.value = true
                } else {
                    _playlistCreationStatus.value = false
                }
            } catch (e: Exception) {
                _playlistCreationStatus.value = false
            }
        }
    }
}