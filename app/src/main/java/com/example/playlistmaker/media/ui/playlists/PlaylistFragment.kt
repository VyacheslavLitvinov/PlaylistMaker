package com.example.playlistmaker.media.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlaylistFragment : Fragment() {

    private var isClickAllowed = true
    companion object {
        const val CLICK_DELAY = 1000L

        fun newInstance(): PlaylistFragment {
            return PlaylistFragment()
        }
    }

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistsViewModel by viewModel()
    private lateinit var playlistsAdapter: PlaylistsAdapter

    private val clickListener = { playlistId: Long ->
        if (clickDebounce()) {
            navigateToPlaylistInfo(playlistId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observePlaylists()
        openCreatePlaylistFragment()

        viewModel.loadPlaylists()
    }

    private fun setupRecyclerView() {
        binding.recyclerviewGrid.layoutManager = GridLayoutManager(requireContext(), 2)
        playlistsAdapter = PlaylistsAdapter(clickListener)
        binding.recyclerviewGrid.adapter = playlistsAdapter
    }

    private fun observePlaylists() {
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            if (playlists.isNullOrEmpty()) {
                binding.NotFoundPlaceholder.visibility = View.VISIBLE
                binding.recyclerviewGrid.visibility = View.GONE
            } else {
                binding.NotFoundPlaceholder.visibility = View.GONE
                binding.recyclerviewGrid.visibility = View.VISIBLE
                playlistsAdapter.updatePlaylists(playlists)
            }
        }
    }

    private fun openCreatePlaylistFragment() {
        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_createPlaylistFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun navigateToPlaylistInfo(playlistId: Long) {
        val bundle = Bundle().apply {
            putLong("playlistId", playlistId)
        }
        findNavController().navigate(R.id.action_mediaFragment_to_playlistInfoFragment, bundle)
    }
}