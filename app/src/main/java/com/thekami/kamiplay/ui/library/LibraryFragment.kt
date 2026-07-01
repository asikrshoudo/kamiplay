package com.thekami.kamiplay.ui.library

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thekami.kamiplay.MainActivity
import com.thekami.kamiplay.R
import com.thekami.kamiplay.data.local.MusicScanner
import com.thekami.kamiplay.data.model.Song
import com.thekami.kamiplay.service.MusicPlaybackService
import com.thekami.kamiplay.ui.adapter.SongAdapter
import com.thekami.kamiplay.ui.playlist.AddToPlaylistDialog

class LibraryFragment : Fragment() {
    private var musicService: MusicPlaybackService? = null
    private var bound = false
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var searchView: SearchView
    private var allSongs = emptyList<Song>()
    private lateinit var adapter: SongAdapter

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlaybackService.LocalBinder
            musicService = binder.getService()
            bound = true
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
            musicService = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        emptyView = view.findViewById(R.id.emptyView)
        searchView = view.findViewById(R.id.searchView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = SongAdapter(emptyList(), { song, _ ->
            musicService?.play(song)
            (requireActivity() as? MainActivity)?.updateMiniPlayer()
        }, { song ->
            val dialog = AddToPlaylistDialog(song)
            dialog.show(parentFragmentManager, "addToPlaylist")
        })
        recyclerView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterSongs(newText.orEmpty())
                return true
            }
        })

        if (checkPermission()) {
            loadSongs()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                REQUEST_CODE
            )
        }

        val intent = Intent(requireContext(), MusicPlaybackService::class.java)
        requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.READ_MEDIA_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadSongs()
        }
    }

    private fun loadSongs() {
        allSongs = MusicScanner.getAllSongs(requireContext())
        filterSongs(searchView.query?.toString().orEmpty())
        emptyView.visibility = if (allSongs.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun filterSongs(query: String) {
        val filtered = if (query.isBlank()) allSongs
        else allSongs.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.artist.contains(query, ignoreCase = true) ||
                    it.album.contains(query, ignoreCase = true)
        }
        adapter.updateSongs(filtered)
        emptyView.visibility = if (filtered.isEmpty() && allSongs.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (bound) {
            requireActivity().unbindService(connection)
            bound = false
        }
    }

    companion object {
        const val REQUEST_CODE = 101
    }
}
