package com.example.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Song
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistInfoAdapter(
    private val clickListener: (Long) -> Unit,
    private val deleteClickListener: (Long) -> Unit
) : RecyclerView.Adapter<PlaylistInfoViewHolder>() {

    private val tracks = mutableListOf<Song>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistInfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return PlaylistInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistInfoViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            clickListener(track.trackId)
        }
        holder.itemView.setOnLongClickListener {
            showDeleteDialog(holder, track.trackId)
            true
        }
    }

    override fun getItemCount(): Int = tracks.size

    fun updateTracks(newTracks: List<Song>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }

    fun getTrackById(trackId: Long): Song? {
        val result = tracks.find { it.trackId == trackId }
        return result
    }

    private fun showDeleteDialog(holder: PlaylistInfoViewHolder, trackId: Long) {
        MaterialAlertDialogBuilder(holder.itemView.context, R.style.MaterialAlertDialog_Center)
            .setTitle("Удалить трек")
            .setMessage("Вы уверены, что хотите удалить трек из плейлиста?")
            .setPositiveButton("Удалить") { _, _ ->
                deleteClickListener(trackId)
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}