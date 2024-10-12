package com.example.playlistmaker.search.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SongsInteractor
import com.example.playlistmaker.search.domain.models.ConsumerData
import com.example.playlistmaker.search.domain.models.Song
import com.example.playlistmaker.search.ui.state.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val songsInteractor: SongsInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private var searchJob: Job? = null
    private var lastSearchText = ""
    private val songList = mutableListOf<Song>()
    private val _screenStateLiveData = MutableLiveData<SearchState>()

    val screenStateLiveData: LiveData<SearchState>
        get() = _screenStateLiveData

    private val _isClearInputButtonVisibleLiveData = MutableLiveData(false)
    val isClearInputButtonVisibleLiveData: LiveData<Boolean>
        get() = _isClearInputButtonVisibleLiveData

    fun searchDebounce(changedText: String) {
        if (lastSearchText == changedText){
            return
        }
        lastSearchText = changedText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY_MILLIS)
            sendQuery(changedText)
        }
    }

    fun onInputStateChanged(hasFocus: Boolean, searchInput: CharSequence?) {
        val searchHistory = searchHistoryInteractor.getSearchHistory()

        _isClearInputButtonVisibleLiveData.value = searchInput.toString().isNotEmpty()

        if (hasFocus && searchInput.toString().isEmpty() && searchHistory.isNotEmpty()) {
            _screenStateLiveData.value = SearchState.History(searchHistory)
        } else if (!hasFocus || searchInput.toString().isNotEmpty()) {
            searchDebounce(searchInput.toString())
        }
    }

    fun changeScreenState(state: SearchState) {
        _screenStateLiveData.value = state
    }

    fun cleanSearchHistory() {
        searchHistoryInteractor.clearSearchHistory()
        _screenStateLiveData.value = SearchState.Content(ArrayList())
    }

    fun repeatLastRequest() {
        _screenStateLiveData.value = SearchState.Loading
        sendQuery(lastSearchText)
    }

    fun addSongToSearchHistory(song: Song) {
        searchHistoryInteractor.addSongToSearchHistory(song)
        if (_screenStateLiveData.value is SearchState.History) {
            _screenStateLiveData.value =
                SearchState.History(getSearchHistory())
        }
    }

    fun getSearchHistory(): List<Song> {
        return searchHistoryInteractor.getSearchHistory()
    }

    private fun sendQuery(newSearchText: String) {
        if (newSearchText.isNotBlank()) {
            _screenStateLiveData.value = SearchState.Loading

            songsInteractor.searchSongs(newSearchText) { data ->
                    if (_screenStateLiveData.value != SearchState.Loading) {
                        return@searchSongs
                    }
                    when (data) {
                        is ConsumerData.Data -> {
                            if (!data.data.isNullOrEmpty()) {
                                songList.clear()
                                songList.addAll(data.data)
                                _screenStateLiveData.postValue(SearchState.Content(ArrayList(songList)))
                            } else {
                                _screenStateLiveData.postValue(SearchState.NotFound)
                            }
                        }
                        is ConsumerData.Error -> {
                            _screenStateLiveData.postValue(SearchState.Error)
                        }
                    }
                }
            }
        }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
    }
}