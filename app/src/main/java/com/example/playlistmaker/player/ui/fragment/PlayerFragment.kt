package com.example.playlistmaker.player.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Constants
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.player.ui.BottomSheetPlaylistAdapter
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Song
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private companion object {
        const val CURRENT_POSITION = "CurrentPosition"
        const val IS_PLAYING = "IsPlaying"
        const val SONG_URL = "SongUrl"
    }

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlayerViewModel>()
    private lateinit var bottomSheetPlaylistAdapter: BottomSheetPlaylistAdapter
    private var songUrl: String? = null
    private var currentPosition = 0
    private var isPlaying = false
    private lateinit var song: Song
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        song = Gson().fromJson(arguments?.getString(Constants.SONG), Song::class.java)

        bottomSheetPlaylistAdapter = BottomSheetPlaylistAdapter(emptyList()) { playlist ->
            viewModel.isTrackInPlaylist(playlist.id, song.trackId) { isInPlaylist ->
                if (isInPlaylist) {
                    Toast.makeText(requireContext(), "Трек уже добавлен в плейлист ${playlist.name}", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.addTrackToPlaylist(
                        playlist.id,
                        song.trackId,
                        song.trackName ?: "",
                        song.artistName ?: "",
                        song.trackTimeMillis,
                        song.artworkUrl100 ?: "",
                        song.collectionName ?: "",
                        song.releaseDate ?: "",
                        song.primaryGenreName ?: "",
                        song.country ?: "",
                        song.previewUrl ?: ""
                    )
                    Toast.makeText(requireContext(), "Добавлено в плейлист ${playlist.name}", Toast.LENGTH_SHORT).show()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }
        binding.recyclerviewPlaylists.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerviewPlaylists.adapter = bottomSheetPlaylistAdapter

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.queueButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            viewModel.loadPlaylists()
        }

        binding.newPlaylistButtonPlayer.setOnClickListener {
            binding.standardBottomSheet.isVisible = false
            binding.fragmentContainerPlayer.isVisible = true
            findNavController().navigate(R.id.createPlaylistFragment)
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.overlay.isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        viewModel.loadFavoriteState(song.trackId)

        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(CURRENT_POSITION, 0)
            isPlaying = savedInstanceState.getBoolean(IS_PLAYING, false)
            songUrl = savedInstanceState.getString(SONG_URL) ?: song.previewUrl
        } else {
            songUrl = song.previewUrl
        }

        songUrl?.let {
            viewModel.preparePlayer(it, startPosition = currentPosition, shouldPlay = isPlaying)
        }

        Glide.with(this)
            .load(viewModel.formatArtworkUrl(song.artworkUrl100))
            .fitCenter()
            .placeholder(R.drawable.album_placeholder_player)
            .transform(RoundedCorners(10))
            .into(binding.songImage)

        binding.songName.text = song.trackName
        binding.songArtist.text = song.artistName
        binding.durationInfoValue.text = viewModel.formatTime(song.trackTimeMillis)
        binding.albumInfoValue.text = song.collectionName
        binding.yearInfoValue.text = viewModel.formatReleaseDate(song.releaseDate)
        binding.genreInfoValue.text = song.primaryGenreName
        binding.countryInfoValue.text = song.country

        binding.playButton.setOnClickListener {
            viewModel.togglePlayback()
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.playButton.setImageResource(
                when (state) {
                    PlayerState.STATE_PLAYING -> R.drawable.pause_button
                    PlayerState.STATE_PAUSED, PlayerState.STATE_COMPLETE -> R.drawable.play_button
                    PlayerState.STATE_DEFAULT -> R.drawable.play_button
                }
            )
        }

        viewModel.currentTime.observe(viewLifecycleOwner) { time ->
            binding.timeView.text = time
        }

        binding.favoriteBorder.setOnClickListener {
            viewModel.onFavoriteClicked(song)
        }

        lifecycleScope.launch {
            viewModel.isFavorite.collect { isFavorite ->
                binding.favoriteBorder.setImageResource(
                    if (isFavorite) R.drawable.favorite_active else R.drawable.favorite_border
                )
            }
        }

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlists?.let {
                bottomSheetPlaylistAdapter.updatePlaylists(it)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SONG_URL, songUrl)
        outState.putInt(CURRENT_POSITION, viewModel.getCurrentPosition())
        outState.putBoolean(IS_PLAYING, viewModel.isPlaying())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            val restoredPosition = it.getInt(CURRENT_POSITION, 0)
            val wasPlaying = it.getBoolean(IS_PLAYING, false)
            songUrl = it.getString(SONG_URL)
            songUrl?.let { url ->
                viewModel.preparePlayer(url, startPosition = restoredPosition, shouldPlay = wasPlaying)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.isPlaying()) {
            viewModel.pausePlayer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetPlayer()
        _binding = null
    }
}

