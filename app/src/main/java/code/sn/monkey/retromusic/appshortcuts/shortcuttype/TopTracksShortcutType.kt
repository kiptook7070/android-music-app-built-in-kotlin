
package code.sn.monkey.retromusic.appshortcuts.shortcuttype

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.ShortcutInfo
import android.os.Build
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.appshortcuts.AppShortcutIconGenerator
import code.sn.monkey.retromusic.appshortcuts.AppShortcutLauncherActivity

@TargetApi(Build.VERSION_CODES.N_MR1)
class TopTracksShortcutType(context: Context) : BaseShortcutType(context) {

    override val shortcutInfo: ShortcutInfo
        get() = ShortcutInfo.Builder(
            context, id
        ).setShortLabel(context.getString(R.string.app_shortcut_top_tracks_short)).setLongLabel(
            context.getString(R.string.app_shortcut_top_tracks_long)
        ).setIcon(
            AppShortcutIconGenerator.generateThemedIcon(
                context, R.drawable.ic_app_shortcut_top_tracks
            )
        ).setIntent(getPlaySongsIntent(AppShortcutLauncherActivity.SHORTCUT_TYPE_TOP_TRACKS))
            .build()

    companion object {

        val id: String
            get() = BaseShortcutType.ID_PREFIX + "top_tracks"
    }
}
