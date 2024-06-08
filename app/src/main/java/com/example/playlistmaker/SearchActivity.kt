package com.example.playlistmaker

import android.app.Activity
import android.os.Bundle
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
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

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
    private lateinit var placeholderImageNetwork: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var editTextSearch: EditText
    private lateinit var songList: RecyclerView
    private lateinit var updateButton: Button
    private lateinit var clearHistoryButton: Button
    private lateinit var adapter: SearchAdapter
    private lateinit var historyAdapter: SearchAdapter
    private lateinit var historyView: LinearLayout
    private lateinit var historyTracks: RecyclerView
    private lateinit var historyManager: HistoryManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        adapter = SearchAdapter(clickListener)
        historyAdapter = SearchAdapter(clickListener)
        historyManager = HistoryManager(this)

        val backButton = findViewById<FrameLayout>(R.id.back_button_search)
        val clearButton = findViewById<ImageView>(R.id.clearIconSearch)
        editTextSearch = findViewById(R.id.inputEditTextSearch)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        songList = findViewById(R.id.tracks)
        placeholderImageNotResult = findViewById(R.id.placeholderImageNotResult)
        placeholderImageNetwork = findViewById(R.id.placeholderImageNetwork)
        updateButton = findViewById(R.id.updateButton)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        historyView = findViewById(R.id.historyView)
        historyTracks = findViewById(R.id.historyTracks)

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
            placeholderMessage.visibility = View.GONE
            placeholderImageNotResult.visibility = View.GONE
            placeholderImageNetwork.visibility = View.GONE
            updateButton.visibility = View.GONE
        }

        clearHistoryButton.setOnClickListener {
            historyManager.clearHistory()
            historySongs.clear()
            historyAdapter.updateHistorySongs(historySongs)
            historyView.visibility = View.GONE
        }

        updateButton.setOnClickListener {
            search()
        }

        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                historyView.visibility = View.GONE
                search()
            }
            false
        }

        editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && editTextSearch.text.isEmpty() && historySongs.isNotEmpty()) {
                historyView.visibility = View.VISIBLE
            } else {
                historyView.visibility = View.GONE
            }
        }

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (editTextSearch.hasFocus() && s.isNullOrEmpty() && historySongs.isNotEmpty()) {
                    historyView.visibility = View.VISIBLE
                } else {
                    historyView.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        val textWatcher = object : TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                resultSearch = s.toString()
            }
        }

        editTextSearch.addTextChangedListener(textWatcher)
        editTextSearch.setText(resultSearch)


        backButton.setOnClickListener{
            finish()
        }

    }

    val clickListener = SearchAdapter.SongClickListener { song, _ ->
        val checkHistory = historyManager.checkHistory(song)
        historyManager.saveSongHistory(checkHistory)
        historyAdapter.updateHistorySongs(checkHistory)
    }

    private fun search() {
        iTunesService.search(editTextSearch.text.toString()).enqueue(object : Callback<SongsResponse> {
            override fun onResponse(call: Call<SongsResponse>, response: Response<SongsResponse>) {
                if (response.code() == 200) {
                    songs.clear()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        songs.addAll(response.body()?.results!!)
                        adapter.notifyDataSetChanged()
                    }
                    if (songs.isEmpty()) {
                        showMessage(getString(R.string.nothing_found))
                    } else {
                        showMessage("")
                    }
                } else {
                    showMessage(getString(R.string.something_went_wrong))
                }
            }

            override fun onFailure(call: Call<SongsResponse>, t: Throwable) {
                showMessage(getString(R.string.network_problems))
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
            placeholderMessage.visibility = View.VISIBLE
            songs.clear()
            adapter.notifyDataSetChanged()
            placeholderMessage.text = text
            if (placeholderMessage.text == getString(R.string.network_problems)) {
                placeholderImageNetwork.visibility = View.VISIBLE
                placeholderImageNotResult.visibility = View.GONE
                updateButton.visibility = View.VISIBLE
            } else if (placeholderMessage.text == getString(R.string.nothing_found)) {
                placeholderImageNotResult.visibility = View.VISIBLE
                placeholderImageNetwork.visibility = View.GONE
            }
        } else {
            placeholderMessage.visibility = View.GONE
            placeholderImageNotResult.visibility = View.GONE
            placeholderImageNetwork.visibility = View.GONE
            updateButton.visibility = View.GONE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_VALUE, resultSearch)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        resultSearch = savedInstanceState.getString(TEXT_VALUE, RESULT_DEF)
    }
    companion object {
        const val TEXT_VALUE = "TEXT_VALUE"
        const val RESULT_DEF = ""
    }

}