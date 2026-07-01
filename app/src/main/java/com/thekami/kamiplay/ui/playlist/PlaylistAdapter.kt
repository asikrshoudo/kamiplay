package com.thekami.kamiplay.ui.playlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thekami.kamiplay.data.local.PlaylistEntity

class PlaylistAdapter(
    private var playlists: List<PlaylistEntity>,
    private val onItemClick: (PlaylistEntity) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.textView.text = playlist.name
        holder.itemView.setOnClickListener { onItemClick(playlist) }
    }

    override fun getItemCount(): Int = playlists.size

    fun updateList(newList: List<PlaylistEntity>) {
        playlists = newList
        notifyDataSetChanged()
    }
}
