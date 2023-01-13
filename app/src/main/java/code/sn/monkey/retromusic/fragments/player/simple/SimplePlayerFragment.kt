
package code.sn.monkey.retromusic.fragments.player.simple

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import code.sn.monkey.appthemehelper.util.ATHUtil
import code.sn.monkey.appthemehelper.util.ToolbarContentTintHelper
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.fragments.base.AbsPlayerFragment
import code.sn.monkey.retromusic.fragments.player.PlayerAlbumCoverFragment
import code.sn.monkey.retromusic.helper.MusicPlayerRemote
import code.sn.monkey.retromusic.model.Song
import code.sn.monkey.retromusic.util.color.MediaNotificationProcessor
import kotlinx.android.synthetic.main.fragment_simple_player.*

/**
 * @author Hemanth S (h4h13).
 */

class SimplePlayerFragment : AbsPlayerFragment(R.layout.fragment_simple_player) {

    override fun playerToolbar(): Toolbar {
        return playerToolbar
    }

    private var lastColor: Int = 0
    override val paletteColor: Int
        get() = lastColor

    private lateinit var controlsFragment: SimplePlaybackControlsFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSubFragments()
        setUpPlayerToolbar()
    }

    private fun setUpSubFragments() {
        val playerAlbumCoverFragment =
            childFragmentManager.findFragmentById(R.id.playerAlbumCoverFragment) as PlayerAlbumCoverFragment
        playerAlbumCoverFragment.setCallbacks(this)
        controlsFragment =
            childFragmentManager.findFragmentById(R.id.playbackControlsFragment) as SimplePlaybackControlsFragment
    }

    override fun onShow() {
        controlsFragment.show()
    }

    override fun onHide() {
        controlsFragment.hide()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun toolbarIconColor(): Int {
        return ATHUtil.resolveColor(requireContext(), R.attr.colorControlNormal)
    }

    override fun onColorChanged(color: MediaNotificationProcessor) {
        lastColor = color.backgroundColor
        libraryViewModel.updateColor(color.backgroundColor)
        controlsFragment.setColor(color)
        ToolbarContentTintHelper.colorizeToolbar(
            playerToolbar,
            ATHUtil.resolveColor(requireContext(), R.attr.colorControlNormal),
            requireActivity()
        )
    }

    override fun onFavoriteToggled() {
        toggleFavorite(MusicPlayerRemote.currentSong)
    }

    override fun toggleFavorite(song: Song) {
        super.toggleFavorite(song)
        if (song.id == MusicPlayerRemote.currentSong.id) {
            updateIsFavorite()
        }
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
