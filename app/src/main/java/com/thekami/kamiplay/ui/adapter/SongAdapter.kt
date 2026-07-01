package com.thekami.kamiplay.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thekami.kamiplay.R
import com.thekami.kamiplay.data.model.Song

class SongAdapter(
    private val songs: List<Song>,
    private val onItemClick: (Song, Int) -> Unit
) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    class ViewHolder(val itemView: androidx.constraintlayout.widget.ConstraintLayout) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.title)
        val artistView: TextView = itemView.findViewById(R.id.artist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false) as androidx.constraintlayout.widget.ConstraintLayout
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]
        holder.titleView.text = song.title
        holder.artistView.text = song.artist
        holder.itemView.setOnClickListener { onItemClick(song, position) }
    }

    override fun getItemCount(): Int = songs.size
}
