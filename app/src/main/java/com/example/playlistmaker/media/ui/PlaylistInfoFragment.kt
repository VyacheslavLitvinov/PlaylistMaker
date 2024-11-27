package com.example.playlistmaker.media.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.Constants
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.databinding.FragmentInfoPlaylistBinding
import com.example.playlistmaker.media.domain.FavouriteSongState
import com.example.playlistmaker.media.ui.favorites.FavoritesFragment
import com.example.playlistmaker.media.ui.favorites.FavouriteSongsViewModel
import com.example.playlistmaker.search.domain.models.Song
import com.example.playlistmaker.search.ui.SearchAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistInfoFragment : Fragment() {

    private var _binding: FragmentInfoPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistInfoViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = arguments?.getLong("playlistId") ?: 0
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

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}