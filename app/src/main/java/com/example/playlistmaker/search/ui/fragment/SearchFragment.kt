package com.example.playlistmaker.search.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.Constants
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.models.Song
import com.example.playlistmaker.search.ui.SearchAdapter
import com.example.playlistmaker.search.ui.state.MessageState
import com.example.playlistmaker.search.ui.state.SearchState
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding get() = _binding!!

    private companion object {
        const val CLICK_DELAY = 1000L
    }

    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var adapter: SearchAdapter
    private lateinit var historyAdapter: SearchAdapter
    private var isClickAllowed = true

    private val clickListener = SearchAdapter.SongClickListener { song, _ ->
        if (clickDebounce()) {
            viewModel.addSongToSearchHistory(song)
            navigateToPlayer(song)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        adapter = SearchAdapter(clickListener)
        historyAdapter = SearchAdapter(clickListener)

        binding.tracks.adapter = adapter
        binding.historyTracks.adapter = historyAdapter

        setupObservers()
        setupListeners()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initViews() {
        with(binding) {
            tracks.layoutManager = LinearLayoutManager(requireContext())
            historyTracks.layoutManager = LinearLayoutManager(requireContext())
            progressBar.isVisible = false
        }
    }

    private fun setupObservers() {
        viewModel.screenStateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchState.Loading -> showLoading()
                is SearchState.Content -> showContent(state)
                is SearchState.History -> showHistory(state)
                is SearchState.Empty -> showEmpty()
                is SearchState.Error -> showError()
                is SearchState.NotFound -> showNotFound()
            }
        }

        viewModel.isClearInputButtonVisibleLiveData.observe(viewLifecycleOwner) {
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

        binding.inputEditTextSearch.addTextChangedListener(
            beforeTextChanged = {_, _, _, _ ->},
            onTextChanged = {s: CharSequence?, _, _, _ ->
                viewModel.searchDebounce(s.toString())
                viewModel.onInputStateChanged(binding.inputEditTextSearch.hasFocus(), s)
            },
            afterTextChanged = {_ ->}
        )

        binding.inputEditTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(binding.inputEditTextSearch)
                viewModel.searchDebounce(binding.inputEditTextSearch.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun showMessage(messageState: MessageState) {
        with(binding) {
            placeholderMessage.isVisible = true
            placeholderImageNetwork.isVisible = false
            placeholderImageNotResult.isVisible = false
            updateButton.isVisible = false

            when (messageState) {
                MessageState.NETWORK_PROBLEMS -> {
                    placeholderImageNetwork.isVisible = true
                    updateButton.isVisible = true
                    placeholderMessage.text = getString(R.string.network_problems)
                }
                MessageState.NOTHING_FOUND -> {
                    placeholderImageNotResult.isVisible = true
                    placeholderMessage.text = getString(R.string.nothing_found)
                }
                MessageState.OTHER -> placeholderMessage.text = ""
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun clickDebounce(): Boolean {
        if (!isClickAllowed) return false
        isClickAllowed = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                delay(CLICK_DELAY)
            } finally {
                isClickAllowed = true
            }
        }
        return true
    }

    private fun showLoading(){
        with(binding){
            progressBar.isVisible = true
            historyView.isVisible = false
            tracks.isVisible = false
            placeholderMessage.isVisible = false
            placeholderImageNetwork.isVisible = false
            placeholderImageNotResult.isVisible = false
            updateButton.isVisible = false
        }
    }

    private fun showContent(state: SearchState.Content){
        with(binding){
            progressBar.isVisible = false
            historyView.isVisible = false
            tracks.isVisible = true
            placeholderMessage.isVisible = false
            placeholderImageNetwork.isVisible = false
            placeholderImageNotResult.isVisible = false
            updateButton.isVisible = false
        }
        adapter.songs = state.songs
        adapter.notifyDataSetChanged()
    }

    private fun showHistory(state: SearchState.History){
        with(binding){
            progressBar.isVisible = false
            historyView.isVisible = true
            tracks.isVisible = false
            placeholderMessage.isVisible = false
            placeholderImageNetwork.isVisible = false
            placeholderImageNotResult.isVisible = false
            updateButton.isVisible = false
        }
        historyAdapter.songs = state.songs
        historyAdapter.notifyDataSetChanged()
    }

    private fun showEmpty(){
        with(binding){
            progressBar.isVisible = false
            historyView.isVisible = false
            tracks.isVisible = false
        }
    }

    private fun showError(){
        with(binding){
            progressBar.isVisible = false
            historyView.isVisible = false
            tracks.isVisible = false
        }
        showMessage(MessageState.NETWORK_PROBLEMS)
    }

    private fun showNotFound(){
        with(binding){
            progressBar.isVisible = false
            historyView.isVisible = false
            tracks.isVisible = false
        }
        showMessage(MessageState.NOTHING_FOUND)
    }

    private fun navigateToPlayer(song: Song) {
        val bundle = Bundle().apply {
            putString(Constants.SONG, Gson().toJson(song))
        }
        findNavController().navigate(R.id.action_searchFragment_to_playerFragment, bundle)
    }
}