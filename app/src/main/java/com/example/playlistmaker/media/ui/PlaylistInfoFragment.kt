package com.example.playlistmaker.media.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Constants
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentInfoPlaylistBinding
import com.example.playlistmaker.media.ui.playlists.PlaylistInfo
import com.example.playlistmaker.search.domain.models.Song
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistInfoFragment : Fragment() {

    private var _binding: FragmentInfoPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistInfoViewModel by viewModel()
    private lateinit var adapter: PlaylistInfoAdapter
    private var playlistId: Long = 0
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var playlistsBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

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
                binding.playlistDuration.text = formatDurationInMinutes(it.totalDuration)
                binding.quantitySongs.text = formatTrackCount(it.songCount)

                if (it.coverImagePath.isNullOrEmpty()) {
                    binding.songImage.setImageResource(R.drawable.placeholder_without_cover)
                } else {
                    val file = File(it.coverImagePath)
                    if (file.exists()) {
                        Glide.with(this)
                            .load(it.coverImagePath)
                            .placeholder(R.drawable.placeholder_without_cover)
                            .into(binding.songImage)
                    } else {
                        binding.songImage.setImageResource(R.drawable.placeholder_without_cover)
                    }
                }
            }
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            adapter.updateTracks(tracks)
            emptyPlaylist()
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        setupBottomSheet()
        setupRecyclerView()
        setupMenuBottomSheet()

        binding.menuButton.setOnClickListener {
            if (menuBottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                binding.overlay.visibility = View.VISIBLE
                binding.menuBottomSheet.visibility = View.VISIBLE
                binding.playlistBottomSheet.visibility = View.GONE
                updateMenuBottomSheet()
            } else {
                menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.overlay.visibility = View.GONE
                binding.menuBottomSheet.visibility = View.GONE
                binding.playlistBottomSheet.visibility = View.VISIBLE
            }
        }

        binding.overlay.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.overlay.visibility = View.GONE
            binding.menuBottomSheet.visibility = View.GONE
            binding.playlistBottomSheet.visibility = View.VISIBLE
        }

        binding.menuShare.setOnClickListener {
            sharePlaylist()
        }

        binding.sharePlaylist.setOnClickListener {
            sharePlaylist()
        }

        binding.menuDelete.setOnClickListener {
            showDeletePlaylistDialog()
        }

        binding.menuEdit.setOnClickListener {
            val bundle = Bundle().apply {
                putLong("playlistId", playlistId)
            }
            findNavController().navigate(R.id.action_playlistInfoFragment_to_editPlaylistFragment, bundle)
        }
    }

    private fun setupBottomSheet() {
        val playlistsBottomSheet = binding.playlistBottomSheet
        playlistsBottomSheetBehavior = BottomSheetBehavior.from(playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            isHideable = false
        }
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
        binding.recyclerviewSongsPlaylist.adapter = adapter
        binding.recyclerviewSongsPlaylist.layoutManager = LinearLayoutManager(context)
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

    private fun setupMenuBottomSheet() {
        val menuBottomSheet = binding.menuBottomSheet
        menuBottomSheetBehavior = BottomSheetBehavior.from(menuBottomSheet).apply {
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                        binding.playlistBottomSheet.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.overlay.visibility = View.VISIBLE
                        binding.playlistBottomSheet.visibility = View.GONE
                    }
                    else -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        menuBottomSheet.setOnClickListener {
        }
    }

    private fun sharePlaylist() {
        viewModel.tracks.value?.let { tracks ->
            if (tracks.isEmpty()) {
                menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                Toast.makeText(requireContext(), "В этом плейлисте нет списка треков, которым можно поделиться", Toast.LENGTH_SHORT).show()
            } else {
                menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                val playlistInfo = viewModel.playlistInfo.value
                val message = buildPlaylistShareMessage(playlistInfo, tracks)
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, message)
                }
                startActivity(Intent.createChooser(intent, "Поделиться плейлистом"))
            }
        }
    }

    private fun emptyPlaylist() {
        viewModel.tracks.value?.let { tracks ->
            if (tracks.isEmpty()) {
                binding.recyclerviewSongsPlaylist.visibility = View.GONE
                Toast.makeText(requireContext(), "В этом плейлисте нет треков", Toast.LENGTH_SHORT).show()
            } else {
                binding.recyclerviewSongsPlaylist.visibility = View.VISIBLE
            }
        }
    }

    private fun buildPlaylistShareMessage(playlistInfo: PlaylistInfo?, tracks: List<Song>): String {
        val sb = StringBuilder()
        sb.append(playlistInfo?.name).append("\n")
        sb.append(playlistInfo?.description).append("\n")
        sb.append("${tracks.size} треков").append("\n")
        tracks.forEachIndexed { index, song ->
            sb.append("${index + 1}. ${song.artistName} - ${song.trackName} (${formatDuration(song.trackTimeMillis)})").append("\n")
        }
        return sb.toString()
    }

    private fun formatDuration(duration: Int): String {
        val minutes = duration / 60000
        val seconds = (duration % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun formatDurationInMinutes(duration: String): String {
        val minutes = duration.toInt()
        val beforeLastDigit = minutes % 100 / 10
        val lastDigit = minutes % 10

        return when {
            beforeLastDigit == 1 -> "$minutes минут"
            lastDigit == 1 -> "$minutes минута"
            lastDigit in 2..4 -> "$minutes минуты"
            else -> "$minutes минут"
        }
    }

    private fun formatTrackCount(trackCount: Int): String {
        val beforeLastDigit = trackCount % 100 / 10
        val lastDigit = trackCount % 10

        return when {
            beforeLastDigit == 1 -> "$trackCount треков"
            lastDigit == 1 -> "$trackCount трек"
            lastDigit in 2..4 -> "$trackCount трека"
            else -> "$trackCount треков"
        }
    }

    private fun showDeletePlaylistDialog() {
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Center)
            .setTitle("Удалить плейлист")
            .setMessage("Вы уверены, что хотите удалить этот плейлист?")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deletePlaylist(playlistId)
                findNavController().navigateUp()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateMenuBottomSheet() {
        Glide.with(this)
            .load(binding.songImage.drawable)
            .transform(CenterCrop(), RoundedCorners(10))
            .placeholder(R.drawable.placeholder_without_cover)
            .into(binding.imageMenuBottomSheet)
        binding.nameMenuBottomSheet.text = binding.playlistName.text
        binding.countMenuBottomSheet.text = binding.quantitySongs.text
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylistInfo(playlistId)
    }

}