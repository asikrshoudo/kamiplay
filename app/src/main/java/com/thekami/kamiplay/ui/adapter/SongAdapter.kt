package com.thekami.kamiplay.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import com.thekami.kamiplay.R
import com.thekami.kamiplay.data.model.Song

class SongAdapter(
    private val songs: List<Song>,
    private val onItemClick: (Song, Int) -> Unit
) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    class ViewHolder(view: ConstraintLayout) : RecyclerView.ViewHolder(view) {
        val albumArt: ImageView = view.findViewById(R.id.albumArt)
        val titleView: TextView = view.findViewById(R.id.title)
        val artistView: TextView = view.findViewById(R.id.artist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false) as ConstraintLayout
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]
        holder.titleView.text = song.title
        holder.artistView.text = song.artist
        holder.albumArt.load(song.albumArtUri) {
            crossfade(true)
            placeholder(R.drawable.ic_music_note)
            error(R.drawable.ic_music_note)
            scale(Scale.FILL)
        }
        holder.itemView.setOnClickListener { onItemClick(song, position) }
    }

    override fun getItemCount(): Int = songs.size
}
