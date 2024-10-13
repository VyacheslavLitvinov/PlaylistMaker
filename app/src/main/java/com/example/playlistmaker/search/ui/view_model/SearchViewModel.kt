package com.example.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SongsInteractor
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
            searchRequest(changedText)
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
        searchRequest(lastSearchText)
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

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotBlank()) {

            _screenStateLiveData.value = SearchState.Loading

            viewModelScope.launch {
                songsInteractor
                    .searchSongs(newSearchText)
                    .collect { pair ->
                    processResult(pair.first, pair.second)
                }
            }
        }
    }

    private fun processResult(data: List<Song>?, error: Int?) {
        val songList = mutableListOf<Song>()
        if (data != null) {
            songList.addAll(data)
        }
        when {
            error != null -> {
                renderState(SearchState.Error)
            }
            songList.isEmpty() -> {
                renderState(SearchState.NotFound)
            }
            else -> renderState(SearchState.Content(songList))
        }
    }

    private fun renderState(state: SearchState) {
        _screenStateLiveData.postValue(state)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
    }
}