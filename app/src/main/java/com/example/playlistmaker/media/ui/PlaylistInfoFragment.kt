package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.Constants
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentInfoPlaylistBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistInfoFragment : Fragment() {

    private var _binding: FragmentInfoPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistInfoViewModel by viewModel()
    private lateinit var adapter: PlaylistInfoAdapter
    private var playlistId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistId = arguments?.getLong("playlistId") ?: 0
        viewModel.loadPlaylistInfo(playlistId)

        viewModel.playlistInfo.observe(viewLifecycleOwner) { playlistInfo ->
            playlistInfo?.let {
                binding.playlistName.text = it.name
                binding.playlistContext.text = it.description ?: ""
                binding.playlistDuration.text = it.totalDuration
                binding.quantitySongs.text = "${it.songCount} треков"

                if (it.coverImagePath.isNullOrEmpty()) {
                    binding.songImage.setImageResource(R.drawable.placeholder_without_cover)
                } else {
                    Glide.with(this)
                        .load(it.coverImagePath)
                        .into(binding.songImage)
                }
            }
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            adapter.updateTracks(tracks)
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        setupBottomSheet()
        setupRecyclerView()
    }

    private fun setupBottomSheet() {
        val bottomSheet = binding.playlistsBottomSheet
        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.isHideable = false
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun setupRecyclerView() {
        adapter = PlaylistInfoAdapter(
            clickListener = { trackId ->
                navigateToPlayer(trackId)
            },
            deleteClickListener = { trackId ->
                viewModel.removeTrackFromPlaylist(playlistId, trackId)
            }
        )
        binding.recyclerviewTracksPlaylist.adapter = adapter
        binding.recyclerviewTracksPlaylist.layoutManager = LinearLayoutManager(context)
    }

    private fun navigateToPlayer(trackId: Long) {
        val track = adapter.getTrackById(trackId)
        track?.let {
            val bundle = Bundle().apply {
                putString(Constants.SONG, Gson().toJson(it))
            }
            findNavController().navigate(R.id.action_playlistInfoFragment_to_playerFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}