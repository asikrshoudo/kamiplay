package com.thekami.kamiplay.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.thekami.kamiplay.R
import com.thekami.kamiplay.data.model.Song
import java.util.concurrent.TimeUnit

class SongAdapter(
    private var songs: List<Song>,
    private val onItemClick: (Song, Int) -> Unit,
    private val onAddToPlaylist: ((Song) -> Unit)? = null
) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    class ViewHolder(view: ConstraintLayout) : RecyclerView.ViewHolder(view) {
        val albumArt: ImageView = view.findViewById(R.id.albumArt)
        val titleView: TextView = view.findViewById(R.id.title)
        val artistView: TextView = view.findViewById(R.id.artist)
        val durationView: TextView = view.findViewById(R.id.duration)
        val moreButton: ImageView = view.findViewById(R.id.moreButton)
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
        holder.durationView.text = formatDuration(song.duration)
        holder.albumArt.load(song.albumArtUri) {
            placeholder(R.drawable.ic_music_note)
            error(R.drawable.ic_music_note)
        }
        holder.itemView.setOnClickListener { onItemClick(song, position) }
        holder.moreButton.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menu.add("Add to Playlist")
            popup.setOnMenuItemClickListener { item ->
                if (item.title == "Add to Playlist") {
                    onAddToPlaylist?.invoke(song)
                    true
                } else false
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = songs.size

    fun updateSongs(newSongs: List<Song>) {
        songs = newSongs
        notifyDataSetChanged()
    }

    private fun formatDuration(millis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}
