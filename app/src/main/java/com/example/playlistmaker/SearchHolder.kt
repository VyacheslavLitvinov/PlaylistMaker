package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class SearchHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val cover : ImageView = itemView.findViewById(R.id.cover)
    private val nameTrack : TextView = itemView.findViewById(R.id.nameTrack)
    private val artist : TextView = itemView.findViewById(R.id.artist)
    private val duration : TextView = itemView.findViewById(R.id.duration)

    fun bind(item : Song){

        Glide.with(cover)
            .load(item.artworkUrl100)
            .fitCenter()
            .placeholder(R.drawable.placeholder_without_cover)
            .transform(RoundedCorners(10))
            .into(cover)
        nameTrack.text = item.trackName
        artist.text = item.artistName
        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(293000L)
        artist.requestLayout()
    }
}