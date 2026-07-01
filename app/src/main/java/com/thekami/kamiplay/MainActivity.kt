package com.thekami.kamiplay

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import coil.load
import com.thekami.kamiplay.databinding.ActivityMainBinding
import com.thekami.kamiplay.service.MusicPlaybackService
import com.thekami.kamiplay.ui.nowplaying.NowPlayingActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var musicService: MusicPlaybackService? = null
    private var bound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlaybackService.LocalBinder
            musicService = binder.getService()
            bound = true
            updateMiniPlayer()
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
            musicService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.navView.setupWithNavController(navController)

        val intent = Intent(this, MusicPlaybackService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)

        binding.miniPlayer.root.setOnClickListener {
            startActivity(Intent(this, NowPlayingActivity::class.java))
        }

        binding.miniPlayer.miniPlayPause.setOnClickListener {
            musicService?.let {
                if (it.isPlaying()) it.pause() else it.resume()
                updateMiniPlayer()
            }
        }
    }

    fun updateMiniPlayer() {
        val song = musicService?.getCurrentSong()
        if (song != null && bound) {
            binding.miniPlayer.root.visibility = View.VISIBLE
            binding.miniPlayer.miniTitle.text = song.title
            binding.miniPlayer.miniAlbumArt.load(song.albumArtUri) {
                placeholder(R.drawable.ic_music_note)
                error(R.drawable.ic_music_note)
            }
            binding.miniPlayer.miniPlayPause.setImageResource(
                if (musicService?.isPlaying() == true) R.drawable.ic_pause else R.drawable.ic_play
            )
        } else {
            binding.miniPlayer.root.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (bound) unbindService(connection)
    }
}
