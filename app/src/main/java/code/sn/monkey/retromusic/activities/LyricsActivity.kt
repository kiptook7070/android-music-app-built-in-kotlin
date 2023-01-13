
package code.sn.monkey.retromusic.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import code.sn.monkey.appthemehelper.ThemeStore
import code.sn.monkey.appthemehelper.util.ToolbarContentTintHelper
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.activities.base.AbsMusicServiceActivity
import code.sn.monkey.retromusic.extensions.surfaceColor
import code.sn.monkey.retromusic.helper.MusicPlayerRemote
import code.sn.monkey.retromusic.helper.MusicProgressViewUpdateHelper
import code.sn.monkey.retromusic.lyrics.LrcView
import code.sn.monkey.retromusic.model.Song
import code.sn.monkey.retromusic.util.LyricUtil
import code.sn.monkey.retromusic.util.RetroUtil
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import kotlinx.android.synthetic.main.activity_lyrics.*

class LyricsActivity : AbsMusicServiceActivity(), MusicProgressViewUpdateHelper.Callback {
    private lateinit var updateHelper: MusicProgressViewUpdateHelper

    private lateinit var song: Song

    private val googleSearchLrcUrl: String
        get() {
            var baseUrl = "http://www.google.com/search?"
            var query = song.title + "+" + song.artistName
            query = "q=" + query.replace(" ", "+") + " .lrc"
            baseUrl += query
            return baseUrl
        }

    private fun buildContainerTransform(): MaterialContainerTransform {
        val transform = MaterialContainerTransform()
        transform.setAllContainerColors(
            MaterialColors.getColor(findViewById(R.id.container), R.attr.colorSurface)
        )
        transform.addTarget(R.id.container)
        transform.duration = 300
        return transform
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lyrics)
        ViewCompat.setTransitionName(container, "lyrics")
        setStatusbarColorAuto()
        setTaskDescriptionColorAuto()
        setNavigationbarColorAuto()

        setupWakelock()

        toolbar.setBackgroundColor(surfaceColor())
        ToolbarContentTintHelper.colorBackButton(toolbar)
        setSupportActionBar(toolbar)

        updateHelper = MusicProgressViewUpdateHelper(this, 500, 1000)
        setupLyricsView()
    }

    private fun setupLyricsView() {
        lyricsView.apply {
            setCurrentColor(ThemeStore.accentColor(context))
            setTimeTextColor(ThemeStore.accentColor(context))
            setTimelineColor(ThemeStore.accentColor(context))
            setTimelineTextColor(ThemeStore.accentColor(context))
            setDraggable(true, LrcView.OnPlayClickListener {
                MusicPlayerRemote.seekTo(it.toInt())
                return@OnPlayClickListener true
            })
        }
    }

    override fun onResume() {
        super.onResume()
        updateHelper.start()
    }

    override fun onPause() {
        super.onPause()
        updateHelper.stop()
    }

    override fun onUpdateProgressViews(progress: Int, total: Int) {
        lyricsView.updateTime(progress.toLong())
    }

    private fun loadLRCLyrics() {
        lyricsView.setLabel("Empty")
        val song = MusicPlayerRemote.currentSong
        if (LyricUtil.isLrcOriginalFileExist(song.data)) {
            lyricsView.loadLrc(LyricUtil.getLocalLyricOriginalFile(song.data))
        } else if (LyricUtil.isLrcFileExist(song.title, song.artistName)) {
            lyricsView.loadLrc(LyricUtil.getLocalLyricFile(song.title, song.artistName))
        }
    }

    override fun onPlayingMetaChanged() {
        super.onPlayingMetaChanged()
        updateTitleSong()
        loadLRCLyrics()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        updateTitleSong()
        loadLRCLyrics()
    }

    private fun updateTitleSong() {
        song = MusicPlayerRemote.currentSong
        toolbar.title = song.title
        toolbar.subtitle = song.artistName
    }

    private fun setupWakelock() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        if (item.itemId == R.id.action_search) {
            RetroUtil.openUrl(this, googleSearchLrcUrl)
        }
        return super.onOptionsItemSelected(item)
    }
}
