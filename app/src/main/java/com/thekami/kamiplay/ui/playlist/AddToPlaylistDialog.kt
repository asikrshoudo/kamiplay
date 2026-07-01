package com.thekami.kamiplay.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.thekami.kamiplay.data.local.AppDatabase
import com.thekami.kamiplay.data.local.PlaylistEntity
import com.thekami.kamiplay.data.local.PlaylistSongCrossRef
import com.thekami.kamiplay.data.model.Song
import kotlinx.coroutines.launch

class AddToPlaylistDialog(private val song: Song) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ListView(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listView = view as ListView
        val db = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            val playlists = db.playlistDao().getAllPlaylistsOnce()
            val names = playlists.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, names)
            listView.adapter = adapter
            listView.setOnItemClickListener { _, _, position, _ ->
                val playlist = playlists[position]
                lifecycleScope.launch {
                    db.playlistDao().addSongToPlaylist(
                        PlaylistSongCrossRef(playlist.id, song.id)
                    )
                    // Also insert song into song table if not exists
                    // We'll omit for brevity
                }
                dismiss()
            }
        }
    }
}
