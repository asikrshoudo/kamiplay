package com.thekami.kamiplay.service

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.datasource.DefaultDataSource
import com.thekami.kamiplay.data.model.Song

class MusicPlaybackService : Service() {
    private lateinit var player: ExoPlayer
    private val binder = LocalBinder()
    private var currentSong: Song? = null
    private lateinit var notificationManager: NotificationManager

    inner class LocalBinder : Binder() {
        fun getService(): MusicPlaybackService = this@MusicPlaybackService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
        createNotificationChannel()
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true
            )
            .build()
    }

    fun play(song: Song) {
        if (currentSong?.id == song.id && player.isPlaying) return
        currentSong = song
        val dataSourceFactory = DefaultDataSource.Factory(this)
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(song.path))
        player.setMediaSource(mediaSource)
        player.prepare()
        player.play()
        startForeground(1, buildNotification(song))
    }

    fun pause() = player.pause()
    fun resume() = player.play()
    fun isPlaying(): Boolean = player.isPlaying
    fun stop() {
        player.stop()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    fun getCurrentSong(): Song? = currentSong

    private fun buildNotification(song: Song): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "kamiplay_music_channel"
    }
}
