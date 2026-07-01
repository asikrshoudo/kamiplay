package com.thekami.kamiplay.ui.playlist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.thekami.kamiplay.R
import com.thekami.kamiplay.data.local.AppDatabase
import com.thekami.kamiplay.data.local.PlaylistEntity
import kotlinx.coroutines.launch

class PlaylistFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.playlistRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = PlaylistAdapter(emptyList()) { playlist ->
            val intent = Intent(requireContext(), PlaylistSongsActivity::class.java)
            intent.putExtra("playlistId", playlist.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        val fab = view.findViewById<FloatingActionButton>(R.id.fabAddPlaylist)
        fab.setOnClickListener {
            val editText = EditText(requireContext())
            AlertDialog.Builder(requireContext())
                .setTitle("New Playlist")
                .setView(editText)
                .setPositiveButton("Create") { _, _ ->
                    val name = editText.text.toString().trim()
                    if (name.isNotEmpty()) {
                        lifecycleScope.launch {
                            AppDatabase.getInstance(requireContext()).playlistDao()
                                .insertPlaylist(PlaylistEntity(name = name))
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        val db = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            db.playlistDao().getAllPlaylists().collect { playlists ->
                adapter.updateList(playlists)
            }
        }
    }

    inner class PlaylistAdapter(
        private var playlists: List<PlaylistEntity>,
        private val onItemClick: (PlaylistEntity) -> Unit
    ) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: android.widget.TextView = view.findViewById(android.R.id.text1)
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
}
