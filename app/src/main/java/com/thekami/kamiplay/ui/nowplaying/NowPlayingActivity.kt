package com.thekami.kamiplay.ui.nowplaying

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.thekami.kamiplay.databinding.ActivityNowPlayingBinding
import com.thekami.kamiplay.service.MusicPlaybackService

class NowPlayingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNowPlayingBinding
    private var musicService: MusicPlaybackService? = null
    private var bound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlaybackService.LocalBinder
            musicService = binder.getService()
            bound = true
            if (musicService != null) {
                supportFragmentManager.beginTransaction()
                    .replace(binding.fragmentContainer.id, NowPlayingFragment(musicService!!))
                    .commit()
            }
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
            musicService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNowPlayingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = Intent(this, MusicPlaybackService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (bound) unbindService(connection)
    }
}
