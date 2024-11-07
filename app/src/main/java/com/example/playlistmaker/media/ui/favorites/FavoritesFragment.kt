package com.example.playlistmaker.media.ui.favorites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.Constants
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.media.domain.FavouriteSongState
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.domain.models.Song
import com.example.playlistmaker.search.ui.SearchAdapter
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<FavouriteSongsViewModel>()
    private lateinit var adapter: SearchAdapter
    private var isClickAllowed = true

    private val clickListener = SearchAdapter.SongClickListener { song, _ ->
        if (clickDebounce()) {
            val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
            playerIntent.putExtra(Constants.SONG, Gson().toJson(song))
            startActivity(playerIntent)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getFavoritesSongs()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        adapter = SearchAdapter(clickListener)
        binding.favoritesRecyclerView.adapter = adapter
        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.favoriteState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavouriteSongState.Content -> {
                    showContent(state.songs)
                }
                is FavouriteSongState.Empty -> {
                    showEmptyState()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showContent(songs: List<Song>) {
        binding.apply {
            NotFoundPlaceholder.isVisible = false
            favoritesRecyclerView.isVisible = true
            adapter.songs = songs
            adapter.notifyDataSetChanged()
        }
    }

    private fun showEmptyState() {
        binding.apply {
            NotFoundPlaceholder.isVisible = true
            favoritesRecyclerView.isVisible = false
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    companion object {
        const val CLICK_DELAY = 1000L
        fun newInstance() = FavoritesFragment()
    }
}