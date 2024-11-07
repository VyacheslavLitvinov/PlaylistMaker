package com.example.playlistmaker.media.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.FavouriteSongState
import com.example.playlistmaker.search.data.db.interactor.FavoritesInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavouriteSongsViewModel(
    private val favoritesInteractor: FavoritesInteractor,
) : ViewModel() {

    private val _favoriteState = MutableLiveData<FavouriteSongState>()
    val favoriteState: LiveData<FavouriteSongState>
        get() = _favoriteState

    fun getFavoritesSongs(){
        viewModelScope.launch(Dispatchers.IO) {
            favoritesInteractor.getFavoritesSongs()
                .collect {favoritesSongs ->
                    if (favoritesSongs.isEmpty()) {
                        _favoriteState.postValue(FavouriteSongState.Empty)
                    } else {
                        _favoriteState.postValue(FavouriteSongState.Content(favoritesSongs))
                    }
                }
        }
    }

}