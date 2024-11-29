package com.example.playlistmaker.media.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Song
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val titleTextView: TextView = itemView.findViewById(R.id.nameTrack)
    private val artistTextView: TextView = itemView.findViewById(R.id.artist)
    private val coverImageView: ImageView = itemView.findViewById(R.id.cover)
    private val duration: TextView = itemView.findViewById(R.id.duration)

    fun bind(item: Song) {
        titleTextView.text = item.trackName
        artistTextView.text = item.artistName
        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis)
        if (item.artworkUrl100.isNullOrEmpty()) {
            coverImageView.setImageResource(R.drawable.placeholder_without_cover)
        } else {
            Glide.with(itemView.context)
                .load(item.artworkUrl100)
                .placeholder(R.drawable.placeholder_without_cover)
                .into(coverImageView)
        }
    }
}