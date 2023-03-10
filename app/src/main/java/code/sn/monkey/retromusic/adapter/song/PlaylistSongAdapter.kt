
package code.sn.monkey.retromusic.adapter.song

import android.view.MenuItem
import android.view.View
import androidx.fragment.app.FragmentActivity
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.db.PlaylistEntity
import code.sn.monkey.retromusic.db.toSongEntity
import code.sn.monkey.retromusic.dialogs.RemoveSongFromPlaylistDialog
import code.sn.monkey.retromusic.interfaces.ICabHolder
import code.sn.monkey.retromusic.model.Song

class PlaylistSongAdapter(
    private val playlist: PlaylistEntity,
    activity: FragmentActivity,
    dataSet: MutableList<Song>,
    itemLayoutRes: Int,
    iCabHolder: ICabHolder?
) : SongAdapter(activity, dataSet, itemLayoutRes, iCabHolder) {

    init {
        this.setMultiSelectMenuRes(R.menu.menu_cannot_delete_single_songs_playlist_songs_selection)
    }

    override fun createViewHolder(view: View): SongAdapter.ViewHolder {
        return ViewHolder(view)
    }

    open inner class ViewHolder(itemView: View) : SongAdapter.ViewHolder(itemView) {

        override var songMenuRes: Int
            get() = R.menu.menu_item_playlist_song
            set(value) {
                super.songMenuRes = value
            }

        override fun onSongMenuItemClick(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.action_remove_from_playlist -> {
                    RemoveSongFromPlaylistDialog.create(song.toSongEntity(playlist.playListId))
                        .show(activity.supportFragmentManager, "REMOVE_FROM_PLAYLIST")
                    return true
                }
            }
            return super.onSongMenuItemClick(item)
        }
    }
}
