package com.thekami.kamiplay.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists")
    suspend fun getAllPlaylistsOnce(): List<PlaylistEntity>

    @Query("SELECT songs.* FROM songs INNER JOIN playlist_song_cross_ref ON songs.id = playlist_song_cross_ref.songId WHERE playlist_song_cross_ref.playlistId = :playlistId")
    fun getSongsInPlaylist(playlistId: Long): Flow<List<SongEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSongToPlaylist(crossRef: PlaylistSongCrossRef)

    @Delete
    suspend fun removeSongFromPlaylist(crossRef: PlaylistSongCrossRef)

    @Query("DELETE FROM playlist_song_cross_ref WHERE playlistId = :playlistId")
    suspend fun clearPlaylist(playlistId: Long)
}
