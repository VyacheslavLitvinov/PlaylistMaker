package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Constants
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Song
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {

    private companion object {
        const val CURRENT_POSITION = "CurrentPosition"
        const val IS_PLAYING = "IsPlaying"
        const val SONG_URL = "SongUrl"
    }

    private lateinit var binding: ActivityPlayerBinding
    private val viewModel by viewModel<PlayerViewModel>()
    private var songUrl: String? = null
    private var currentPosition = 0
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
            viewModel.resetPlayer()
        }

        val song = Gson().fromJson(intent.getStringExtra(Constants.SONG), Song::class.java)

        viewModel.loadFavoriteState(song.trackId)

        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(CURRENT_POSITION, 0)
            isPlaying = savedInstanceState.getBoolean(IS_PLAYING, false)
            songUrl = savedInstanceState.getString(SONG_URL) ?: song.previewUrl
            viewModel.preparePlayer(songUrl ?: "", startPosition = currentPosition, shouldPlay = isPlaying)
        } else {
            songUrl = song.previewUrl
            viewModel.preparePlayer(songUrl ?: "", startPosition = currentPosition, shouldPlay = isPlaying)
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

        viewModel.state.observe(this) { state ->
            when (state) {
                PlayerState.STATE_PLAYING -> {
                    binding.playButton.setImageResource(R.drawable.pause_button)
                }
                PlayerState.STATE_PAUSED, PlayerState.STATE_COMPLETE -> {
                    binding.playButton.setImageResource(R.drawable.play_button)
                }
                PlayerState.STATE_DEFAULT -> Unit
            }
        }

        viewModel.currentTime.observe(this) { time ->
            binding.timeView.text = time
        }

        binding.favoriteBorder.setOnClickListener {
            viewModel.onFavoriteClicked(song)
        }

        viewModel.isFavorite.observe(this) {isFavorite ->
            if (isFavorite){
                binding.favoriteBorder.setImageResource(R.drawable.favorite_active)
            } else {
                binding.favoriteBorder.setImageResource(R.drawable.favorite_border)
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SONG_URL, songUrl)
        outState.putInt(CURRENT_POSITION, viewModel.getCurrentPosition())
        outState.putBoolean(IS_PLAYING, viewModel.isPlaying())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredPosition = savedInstanceState.getInt(CURRENT_POSITION, 0)
        val wasPlaying = savedInstanceState.getBoolean(IS_PLAYING, false)
        songUrl = savedInstanceState.getString(SONG_URL)
        viewModel.preparePlayer(songUrl ?: "", startPosition = restoredPosition, shouldPlay = wasPlaying)
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.isPlaying()) {
            viewModel.pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.resetPlayer()
    }

}
