
package code.sn.monkey.retromusic.appshortcuts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import code.sn.monkey.retromusic.appshortcuts.shortcuttype.LastAddedShortcutType
import code.sn.monkey.retromusic.appshortcuts.shortcuttype.ShuffleAllShortcutType
import code.sn.monkey.retromusic.appshortcuts.shortcuttype.TopTracksShortcutType
import code.sn.monkey.retromusic.extensions.extraNotNull
import code.sn.monkey.retromusic.model.Playlist
import code.sn.monkey.retromusic.model.smartplaylist.LastAddedPlaylist
import code.sn.monkey.retromusic.model.smartplaylist.ShuffleAllPlaylist
import code.sn.monkey.retromusic.model.smartplaylist.TopTracksPlaylist
import code.sn.monkey.retromusic.service.MusicService
import code.sn.monkey.retromusic.service.MusicService.*

class AppShortcutLauncherActivity : Activity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (extraNotNull(KEY_SHORTCUT_TYPE, SHORTCUT_TYPE_NONE).value) {
            SHORTCUT_TYPE_SHUFFLE_ALL -> {
                startServiceWithPlaylist(
                    SHUFFLE_MODE_SHUFFLE, ShuffleAllPlaylist()
                )
                DynamicShortcutManager.reportShortcutUsed(this, ShuffleAllShortcutType.id)
            }
            SHORTCUT_TYPE_TOP_TRACKS -> {
                startServiceWithPlaylist(
                    SHUFFLE_MODE_NONE, TopTracksPlaylist()
                )
                DynamicShortcutManager.reportShortcutUsed(this, TopTracksShortcutType.id)
            }
            SHORTCUT_TYPE_LAST_ADDED -> {
                startServiceWithPlaylist(
                    SHUFFLE_MODE_NONE, LastAddedPlaylist()
                )
                DynamicShortcutManager.reportShortcutUsed(this, LastAddedShortcutType.id)
            }
        }
        finish()
    }

    private fun startServiceWithPlaylist(shuffleMode: Int, playlist: Playlist) {
        val intent = Intent(this, MusicService::class.java)
        intent.action = ACTION_PLAY_PLAYLIST

        val bundle = Bundle()
        bundle.putParcelable(INTENT_EXTRA_PLAYLIST, playlist)
        bundle.putInt(INTENT_EXTRA_SHUFFLE_MODE, shuffleMode)

        intent.putExtras(bundle)

        startService(intent)
    }

    companion object {
        const val KEY_SHORTCUT_TYPE = "code.sn.monkey.retromusic.appshortcuts.ShortcutType"
        const val SHORTCUT_TYPE_SHUFFLE_ALL = 0L
        const val SHORTCUT_TYPE_TOP_TRACKS = 1L
        const val SHORTCUT_TYPE_LAST_ADDED = 2L
        const val SHORTCUT_TYPE_NONE = 4L
    }
}
