
package code.sn.monkey.retromusic.fragments.player.home

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import code.sn.monkey.appthemehelper.util.ATHUtil
import code.sn.monkey.appthemehelper.util.ToolbarContentTintHelper
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.fragments.base.AbsPlayerFragment
import code.sn.monkey.retromusic.helper.MusicPlayerRemote
import code.sn.monkey.retromusic.helper.MusicProgressViewUpdateHelper
import code.sn.monkey.retromusic.model.Song
import code.sn.monkey.retromusic.util.MusicUtil
import code.sn.monkey.retromusic.util.color.MediaNotificationProcessor
import kotlinx.android.synthetic.main.fragment_home_player.*

class HomePlayerFragment : AbsPlayerFragment(R.layout.fragment_home_player),
    MusicProgressViewUpdateHelper.Callback {
    private var lastColor: Int = 0
    private lateinit var progressViewUpdateHelper: MusicProgressViewUpdateHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressViewUpdateHelper = MusicProgressViewUpdateHelper(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPlayerToolbar()
    }

    override fun onResume() {
        super.onResume()
        progressViewUpdateHelper.start()
    }

    override fun onPause() {
        super.onPause()
        progressViewUpdateHelper.stop()
    }

    override fun playerToolbar(): Toolbar? {
        return playerToolbar
    }

    override fun onShow() {
    }

    override fun onHide() {
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        updateSong()
    }

    override fun onPlayingMetaChanged() {
        super.onPlayingMetaChanged()
        updateSong()
    }

    private fun updateSong() {
        val song = MusicPlayerRemote.currentSong
        title.text = song.title
        text.text = song.artistName
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun toolbarIconColor(): Int {
        return Color.WHITE
    }

    override val paletteColor: Int
        get() = lastColor

    override fun onColorChanged(color: MediaNotificationProcessor) {
        lastColor = color.backgroundColor
        libraryViewModel.updateColor(color.backgroundColor)
        ToolbarContentTintHelper.colorizeToolbar(
            playerToolbar,
            Color.WHITE,
            requireActivity()
        )
    }

    override fun toggleFavorite(song: Song) {
        super.toggleFavorite(song)
        if (song.id == MusicPlayerRemote.currentSong.id) {
            updateIsFavorite()
        }
    }

    override fun onFavoriteToggled() {
        toggleFavorite(MusicPlayerRemote.currentSong)
    }

    override fun onUpdateProgressViews(progress: Int, total: Int) {
        songTotalTime.text = MusicUtil.getReadableDurationString(progress.toLong())
    }

    private fun setUpPlayerToolbar() {
        playerToolbar.inflateMenu(R.menu.menu_player)
        playerToolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        playerToolbar.setOnMenuItemClickListener(this)

        ToolbarContentTintHelper.colorizeToolbar(
            playerToolbar,
            ATHUtil.resolveColor(requireContext(), R.attr.colorControlNormal),
            requireActivity()
        )
    }
}
