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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thekami.kamiplay.data.local.MusicScanner
import com.thekami.kamiplay.data.model.Song
import com.thekami.kamiplay.service.MusicPlaybackService
import com.thekami.kamiplay.ui.adapter.SongAdapter

class LibraryFragment : Fragment() {
    private var musicService: MusicPlaybackService? = null
    private var bound = false
    private lateinit var recyclerView: RecyclerView
    private var songs = emptyList<Song>()

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
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        if (checkPermission()) {
            loadSongs()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                REQUEST_CODE
            )
        }

        // Bind to service
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
        songs = MusicScanner.getAllSongs(requireContext())
        recyclerView.adapter = SongAdapter(songs) { song, _ ->
            musicService?.play(song)
        }
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
