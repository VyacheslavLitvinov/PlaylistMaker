package com.example.playlistmaker.search.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SongsInteractor
import com.example.playlistmaker.search.domain.models.ConsumerData
import com.example.playlistmaker.search.domain.models.Song
import com.example.playlistmaker.search.ui.state.SearchState

class SearchViewModel(
    private val songsInteractor: SongsInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable by lazy {
        Runnable {
            val newSearchText = lastSearchText
            sendQuery(newSearchText)
        }
    }

    private var lastSearchText = ""

    private val songList = mutableListOf<Song>()

    private val _screenStateLiveData = MutableLiveData<SearchState>()

    val screenStateLiveData: LiveData<SearchState>
        get() = _screenStateLiveData

    private val _isClearInputButtonVisibleLiveData = MutableLiveData<Boolean>(false)
    val isClearInputButtonVisibleLiveData: LiveData<Boolean>
        get() = _isClearInputButtonVisibleLiveData

    fun searchDebounce(changedText: String) {
        lastSearchText = changedText
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_MILLIS)
        //_screenStateLiveData.value = SearchState.Content(ArrayList())
    }

    fun onInputStateChanged(hasFocus: Boolean, searchInput: CharSequence?) {
        val searchHistory = searchHistoryInteractor.getSearchHistory()

        _isClearInputButtonVisibleLiveData.value = searchInput.toString().isNotEmpty()

        if (hasFocus && searchInput.toString().isEmpty() && searchHistory.isNotEmpty()) {
            handler.removeCallbacks(searchRunnable)
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
                SearchState.History(searchHistoryInteractor.getSearchHistory())
        }
    }

    fun getSearchHistory(): ArrayList<Song> {
        return searchHistoryInteractor.getSearchHistory()
    }

    private fun sendQuery(newSearchText: String) {
        if (newSearchText.isNotBlank()) {
            _screenStateLiveData.value = SearchState.Loading

            songsInteractor.searchSongs(newSearchText, object : SongsInteractor.SongsConsumer {
                override fun consume(data: ConsumerData<ArrayList<Song>>) {
                    if (_screenStateLiveData.value != SearchState.Loading) {
                        return
                    }
                    when (data) {
                        is ConsumerData.Data -> {
                            if (!data.data.isNullOrEmpty()) {
                                songList.clear()
                                songList.addAll(data.data)
                                _screenStateLiveData.postValue(SearchState.Content(ArrayList(songList)))
                            } else {
                                _screenStateLiveData.postValue(SearchState.Empty)
                            }
                        }
                        is ConsumerData.Error -> {
                            _screenStateLiveData.postValue(SearchState.Error)
                        }
                    }
                }
            })
        }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(searchRunnable)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L

        fun getViewModelFactory(): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    SearchViewModel(
                        Creator.provideSongsInteractor(),
                        Creator.provideSearchHistoryInteractor()
                    )
                }
            }
        }
    }
}