package com.thekami.kamiplay.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thekami.kamiplay.R
import com.thekami.kamiplay.data.local.AppDatabase
import com.thekami.kamiplay.data.model.Song
import com.thekami.kamiplay.ui.adapter.SongAdapter
import kotlinx.coroutines.launch

class PlaylistSongsFragment : Fragment() {
    private lateinit var playlistId: Long
    private lateinit var adapter: SongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlistId = arguments?.getLong("playlistId") ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist_songs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = SongAdapter(emptyList(), { _, _ -> }, null)
        recyclerView.adapter = adapter

        val db = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            db.playlistDao().getSongsInPlaylist(playlistId).collect { songEntities ->
                val songs = songEntities.map {
                    Song(it.id, it.title, it.artist, it.album, it.path, it.duration, it.albumArtUri)
                }
                adapter.updateSongs(songs)
            }
        }
    }
}
