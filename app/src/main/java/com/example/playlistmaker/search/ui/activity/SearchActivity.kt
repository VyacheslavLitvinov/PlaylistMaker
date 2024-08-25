package com.example.playlistmaker.search.ui.activity

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
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.Constants
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.ui.SearchAdapter
import com.example.playlistmaker.search.ui.state.SearchState
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    private companion object {
        const val CLICK_DELAY = 1000L
    }

    private lateinit var viewModel: SearchViewModel

    private lateinit var adapter: SearchAdapter
    private lateinit var historyAdapter: SearchAdapter

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    private val clickListener = SearchAdapter.SongClickListener { song, _ ->
        if (clickDebounce()) {
            viewModel.addSongToSearchHistory(song)
            val playerIntent = Intent(this, PlayerActivity::class.java)
            playerIntent.putExtra(Constants.SONG, Gson().toJson(song))
            startActivity(playerIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, SearchViewModel.getViewModelFactory()).get(SearchViewModel::class.java)

        initViews()

        adapter = SearchAdapter(clickListener)
        historyAdapter = SearchAdapter(clickListener)

        binding.tracks.adapter = adapter
        binding.historyTracks.adapter = historyAdapter

        setupObservers()
        setupListeners()

        binding.backButtonSearch.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        with(binding) {
            tracks.layoutManager = LinearLayoutManager(this@SearchActivity)
            historyTracks.layoutManager = LinearLayoutManager(this@SearchActivity)
            progressBar.isVisible = false
        }
    }

    private fun setupObservers() {
        viewModel.screenStateLiveData.observe(this) { state ->
            when (state) {
                is SearchState.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.historyView.isVisible = false
                    binding.tracks.isVisible = false
                    binding.placeholderMessage.isVisible = false
                    binding.placeholderImageNetwork.isVisible = false
                    binding.placeholderImageNotResult.isVisible = false
                    binding.updateButton.isVisible = false
                }
                is SearchState.Content -> {
                    binding.progressBar.isVisible = false
                    binding.historyView.isVisible = false
                    binding.tracks.isVisible = true
                    binding.placeholderMessage.isVisible = false
                    binding.placeholderImageNetwork.isVisible = false
                    binding.placeholderImageNotResult.isVisible = false
                    binding.updateButton.isVisible = false
                    adapter.songs = state.songs
                    adapter.notifyDataSetChanged()
                }
                is SearchState.History -> {
                    binding.progressBar.isVisible = false
                    binding.historyView.isVisible = true
                    binding.tracks.isVisible = false
                    binding.placeholderMessage.isVisible = false
                    binding.placeholderImageNetwork.isVisible = false
                    binding.placeholderImageNotResult.isVisible = false
                    binding.updateButton.isVisible = false
                    historyAdapter.songs = state.songs
                    historyAdapter.notifyDataSetChanged()
                }
                is SearchState.Empty -> {
                    binding.progressBar.isVisible = false
                    binding.historyView.isVisible = false
                    binding.tracks.isVisible = false
                }
                is SearchState.Error -> {
                    binding.progressBar.isVisible = false
                    binding.historyView.isVisible = false
                    binding.tracks.isVisible = false
                    showMessage(getString(R.string.network_problems))
                }
                is SearchState.NotFound -> {
                    binding.progressBar.isVisible = false
                    binding.historyView.isVisible = false
                    binding.tracks.isVisible = false
                    showMessage(getString(R.string.nothing_found))
                }
            }
        }

        viewModel.isClearInputButtonVisibleLiveData.observe(this) {
            binding.clearIconSearch.isVisible = it
        }
    }

    private fun setupListeners() {
        binding.clearIconSearch.setOnClickListener {
            binding.inputEditTextSearch.setText("")
            hideKeyboard(binding.inputEditTextSearch)
            binding.inputEditTextSearch.clearFocus()
            viewModel.changeScreenState(SearchState.Empty)
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.cleanSearchHistory()
        }

        binding.updateButton.setOnClickListener {
            viewModel.repeatLastRequest()
        }

        binding.inputEditTextSearch.setOnFocusChangeListener { v, hasFocus ->
            viewModel.onInputStateChanged(hasFocus, (v as EditText).text)
        }

        binding.inputEditTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchDebounce(s.toString())
                viewModel.onInputStateChanged(binding.inputEditTextSearch.hasFocus(), s)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.inputEditTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(binding.inputEditTextSearch)
                viewModel.searchDebounce(binding.inputEditTextSearch.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun showMessage(text: String) {
        with(binding) {
            placeholderMessage.isVisible = text.isNotEmpty()
            placeholderMessage.text = text
            placeholderImageNetwork.isVisible = false
            placeholderImageNotResult.isVisible = false
            updateButton.isVisible = false

            when (text) {
                getString(R.string.network_problems) -> {
                    placeholderImageNetwork.isVisible = true
                    updateButton.isVisible = true
                }
                getString(R.string.nothing_found) -> {
                    placeholderImageNotResult.isVisible = true
                }
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DELAY)
        }
        return current
    }
}