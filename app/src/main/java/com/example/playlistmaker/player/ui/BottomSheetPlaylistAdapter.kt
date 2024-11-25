package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.entity.Playlist

class BottomSheetPlaylistAdapter(
    private var playlists: List<Playlist>,
    private val clickListener: (Playlist) -> Unit
) : RecyclerView.Adapter<BottomSheetPlaylistAdapter.BottomSheetPlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetPlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist_item_bottom_sheet, parent, false)
        return BottomSheetPlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: BottomSheetPlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size

    fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    inner class BottomSheetPlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coverImageView: ImageView = itemView.findViewById(R.id.cover)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTrack)
        private val artistTextView: TextView = itemView.findViewById(R.id.artist)

        init {
            itemView.setOnClickListener {
                clickListener(playlists[adapterPosition])
            }
        }

        fun bind(playlist: Playlist) {
            if (!playlist.coverImagePath.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(playlist.coverImagePath)
                    .placeholder(R.drawable.placeholder_without_cover)
                    .transform(RoundedCorners(16))
                    .into(coverImageView)
            } else {
                coverImageView.setImageResource(R.drawable.placeholder_without_cover)
            }

            nameTextView.text = playlist.name

            artistTextView.text = "${playlist.songCount} треков"
        }
    }
}