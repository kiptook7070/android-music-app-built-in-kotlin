package code.sn.monkey.retromusic.interfaces

import android.view.View
import code.sn.monkey.retromusic.db.PlaylistWithSongs

interface IPlaylistClickListener {
    fun onPlaylistClick(playlistWithSongs: PlaylistWithSongs, view: View)
}