package com.example.playlistmaker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private companion object {
        const val TEXT_VALUE = "TEXT_VALUE"
        const val RESULT_DEF = ""
        const val SONG = "Song"
        private const val SEARCH_DELAY = 2000L
        private const val CLICK_DELAY = 1000L
    }

    var resultSearch : String = RESULT_DEF

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ItunesSearchAPI::class.java)

    private val songs = ArrayList<Song>()
    private var historySongs = ArrayList<Song>()

    private lateinit var placeholderImageNotResult: ImageView
    private lateinit var backButton: FrameLayout
    private lateinit var placeholderImageNetwork: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var editTextSearch: EditText
    private lateinit var songList: RecyclerView
    private lateinit var updateButton: Button
    private lateinit var clearButton: ImageView
    private lateinit var clearHistoryButton: Button
    private lateinit var adapter: SearchAdapter
    private lateinit var historyAdapter: SearchAdapter
    private lateinit var historyView: LinearLayout
    private lateinit var historyTracks: RecyclerView
    private lateinit var historyManager: HistoryManager
    private lateinit var progressBar: ProgressBar

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        adapter = SearchAdapter(clickListener)
        historyAdapter = SearchAdapter(clickListener)
        historyManager = HistoryManager(this)

        backButton = findViewById(R.id.back_button_search)
        clearButton = findViewById(R.id.clearIconSearch)
        editTextSearch = findViewById(R.id.inputEditTextSearch)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        songList = findViewById(R.id.tracks)
        placeholderImageNotResult = findViewById(R.id.placeholderImageNotResult)
        placeholderImageNetwork = findViewById(R.id.placeholderImageNetwork)
        updateButton = findViewById(R.id.updateButton)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        historyView = findViewById(R.id.historyView)
        historyTracks = findViewById(R.id.historyTracks)
        progressBar = findViewById(R.id.progressBar)
        progressBar.isVisible = false

        adapter.songs = songs
        songList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        historyTracks.layoutManager = LinearLayoutManager(this)
        songList.adapter = adapter
        historyTracks.adapter = historyAdapter

        historySongs = historyManager.getHistorySongs()
        historyAdapter.updateHistorySongs(historySongs)

        clearButton.setOnClickListener {
            editTextSearch.setText("")
            hideKeyboard(editTextSearch)
            editTextSearch.clearFocus()
            songs.clear()
            adapter.notifyDataSetChanged()
            historySongs = historyManager.getHistorySongs()
            historyAdapter.updateHistorySongs(historySongs)
            adapter.notifyDataSetChanged()
            hidingPlaceholderView()
        }

        clearHistoryButton.setOnClickListener {
            historyManager.clearHistory()
            historySongs.clear()
            historyAdapter.updateHistorySongs(historySongs)
            historyView.isVisible = false
        }

        updateButton.setOnClickListener {
            search()
        }

        //выполнение поиска при нажатии на кнопку Done клавиатуры
        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                historyView.isVisible = false
                search()
            }
            false
        }

        editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && editTextSearch.text.isEmpty() && historySongs.isNotEmpty()) {
                historyView.isVisible = true
            } else {
                historyView.isVisible = false
            }
        }

        editTextSearch.addTextChangedListener(
            beforeTextChanged = { _, _, _, _ ->  },
            onTextChanged = {text: CharSequence?, _, _, _ ->
                historyView.isVisible = editTextSearch.hasFocus() && text.isNullOrEmpty() && historySongs.isNotEmpty()
            },
            afterTextChanged = {_ ->}
        )

        val textWatcher = object : TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    songs.clear()
                    adapter.notifyDataSetChanged()
                    historySongs = historyManager.getHistorySongs()
                    historyAdapter.updateHistorySongs(historySongs)
                    hidingPlaceholderView()
                } else {
                    searchDebounce()
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                resultSearch = s.toString()
                hidingPlaceholderView()
            }
        }

        editTextSearch.addTextChangedListener(textWatcher)
        editTextSearch.setText(resultSearch)


        backButton.setOnClickListener{
            finish()
        }

    }
//
    private val clickListener = SearchAdapter.SongClickListener { song, _ ->
        val checkHistory = historyManager.checkHistory(song)
        historyManager.saveSongHistory(checkHistory)
        historyAdapter.updateHistorySongs(checkHistory)
        val json = Gson().toJson(song)
        if(clickDebounce()){
            val playerIntent = Intent(this, PlayerActivity::class.java)
            playerIntent.putExtra(SONG, json)
            startActivity(playerIntent)
        }
    }

    private fun search() {

        val searchText = editTextSearch.text.toString()
        if (searchText.isEmpty()){
            progressBar.isVisible = false
            songs.clear()
            adapter.notifyDataSetChanged()
            return
        }

        progressBar.isVisible = true
        iTunesService.search(editTextSearch.text.toString()).enqueue(object : Callback<SongsResponse> {
            override fun onResponse(call: Call<SongsResponse>, response: Response<SongsResponse>) {
                if (response.code() == 200) {
                    songs.clear()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        songs.addAll(response.body()?.results!!)
                        adapter.notifyDataSetChanged()
                        progressBar.isVisible = false
                    }
                    if (songs.isEmpty()) {
                        showMessage(getString(R.string.nothing_found))
                        progressBar.isVisible = false
                    } else {
                        showMessage("")
                        progressBar.isVisible = false
                    }
                } else {
                    showMessage(getString(R.string.something_went_wrong))
                    progressBar.isVisible = false
                }
            }

            override fun onFailure(call: Call<SongsResponse>, t: Throwable) {
                showMessage(getString(R.string.network_problems))
                progressBar.isVisible = false
            }
        })
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()){
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showMessage(text: String) {
        if (text.isNotEmpty()) {
            placeholderMessage.isVisible = true
            songs.clear()
            adapter.notifyDataSetChanged()
            placeholderMessage.text = text
            if (placeholderMessage.text == getString(R.string.network_problems)) {
                placeholderImageNetwork.isVisible = true
                placeholderImageNotResult.isVisible = false
                updateButton.isVisible = true
            } else if (placeholderMessage.text == getString(R.string.nothing_found)) {
                placeholderImageNotResult.isVisible = true
                placeholderImageNetwork.isVisible = false
                updateButton.isVisible = false
            }
        } else {
            placeholderMessage.isVisible = false
            placeholderImageNotResult.isVisible = false
            placeholderImageNetwork.isVisible = false
            updateButton.isVisible = false
        }
    }

    private fun searchDebounce(){
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DELAY)
    }

    private fun clickDebounce(): Boolean{
        val current = isClickAllowed
        if (isClickAllowed){
            isClickAllowed = false
            handler.postDelayed({isClickAllowed = true}, CLICK_DELAY)
        }
        return current
    }

    private fun hidingPlaceholderView(){
        placeholderMessage.isVisible = false
        placeholderImageNotResult.isVisible = false
        placeholderImageNetwork.isVisible = false
        updateButton.isVisible = false
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_VALUE, resultSearch)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        resultSearch = savedInstanceState.getString(TEXT_VALUE, RESULT_DEF)
    }

}