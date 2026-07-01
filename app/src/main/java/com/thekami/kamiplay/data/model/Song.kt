package com.thekami.kamiplay.data.model

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val path: String,
    val duration: Long,
    val albumArtUri: String? = null
)
