package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Song

class SearchAdapter(private val clickListener: SongClickListener) : RecyclerView.Adapter<SearchHolder>() {
    var songs : List<Song> = emptyList()
    private var historySongs = ArrayList<Song>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return SearchHolder(view)
    }

    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        val song = if (songs.isNotEmpty()) songs[position] else historySongs[position]
        holder.bind(song)
        holder.itemView.setOnClickListener {
            clickListener.onSongClick(song, position)
        }
    }

    override fun getItemCount(): Int = if (songs.isNotEmpty()) songs.size else historySongs.size

    fun interface SongClickListener {
        fun onSongClick(song: Song, position: Int)
    }
}