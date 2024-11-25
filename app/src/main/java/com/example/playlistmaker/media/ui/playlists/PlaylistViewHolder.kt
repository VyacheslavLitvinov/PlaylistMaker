package com.example.playlistmaker.media.ui.playlists

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.entity.Playlist

class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val imageView: ImageView = itemView.findViewById(R.id.playlistImage)
    private val titleTextView: TextView = itemView.findViewById(R.id.playlistName)
    private val counterTextView: TextView = itemView.findViewById(R.id.playlistCounterSongs)

    fun bind(playlist: Playlist) {
        titleTextView.text = playlist.name

        if (playlist.coverImagePath.isNullOrEmpty()) {
            Glide.with(itemView.context)
                .load(R.drawable.placeholder_without_cover)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(CenterCrop(), RoundedCorners(16))
                .into(imageView)
        } else {
            Glide.with(itemView.context)
                .load(playlist.coverImagePath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(CenterCrop(), RoundedCorners(16))
                .placeholder(R.drawable.placeholder_without_cover)
                .error(R.drawable.placeholder_without_cover)
                .into(imageView)
        }

        counterTextView.text = "${playlist.songCount} ${getTrackCountLabel(playlist.songCount)}"
    }

    private fun getTrackCountLabel(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> "трек"
            count % 10 in 2..4 && (count % 100 !in 12..14) -> "трека"
            else -> "треков"
        }
    }
}