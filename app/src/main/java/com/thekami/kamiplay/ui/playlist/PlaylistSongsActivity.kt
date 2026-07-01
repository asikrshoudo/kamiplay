package com.thekami.kamiplay.ui.playlist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.thekami.kamiplay.R

class PlaylistSongsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_songs)
        val playlistId = intent.getLongExtra("playlistId", 0)
        val fragment = PlaylistSongsFragment().apply {
            arguments = Bundle().apply { putLong("playlistId", playlistId) }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}
