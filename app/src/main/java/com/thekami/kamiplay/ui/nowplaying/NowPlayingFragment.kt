package com.thekami.kamiplay.ui.nowplaying

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import coil.load
import com.thekami.kamiplay.R
import com.thekami.kamiplay.service.MusicPlaybackService

class NowPlayingFragment(private val service: MusicPlaybackService) : Fragment() {

    private lateinit var albumArt: ImageView
    private lateinit var songTitle: TextView
    private lateinit var songArtist: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var btnPlayPause: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_now_playing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        albumArt = view.findViewById(R.id.albumArt)
        songTitle = view.findViewById(R.id.songTitle)
        songArtist = view.findViewById(R.id.songArtist)
        seekBar = view.findViewById(R.id.seekBar)
        btnPlayPause = view.findViewById(R.id.btnPlayPause)

        btnPlayPause.setOnClickListener {
            if (service.isPlaying()) service.pause() else service.resume()
            updatePlayPause()
        }

        view.findViewById<ImageButton>(R.id.btnNext).setOnClickListener { service.playNext() }
        view.findViewById<ImageButton>(R.id.btnPrev).setOnClickListener { service.playPrevious() }

        // Shuffle & Repeat placeholder
        updateNowPlaying()
    }

    private fun updateNowPlaying() {
        val song = service.getCurrentSong()
        song?.let {
            songTitle.text = it.title
            songArtist.text = it.artist
            albumArt.load(it.albumArtUri) {
                placeholder(R.drawable.ic_music_note)
                error(R.drawable.ic_music_note)
            }
        }
        updatePlayPause()
    }

    private fun updatePlayPause() {
        btnPlayPause.setImageResource(
            if (service.isPlaying()) R.drawable.ic_pause else R.drawable.ic_play
        )
    }

    override fun onResume() {
        super.onResume()
        updateNowPlaying()
    }
}
