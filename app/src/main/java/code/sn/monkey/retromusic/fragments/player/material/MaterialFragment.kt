
package code.sn.monkey.retromusic.fragments.player.material

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import code.sn.monkey.appthemehelper.util.ATHUtil
import code.sn.monkey.appthemehelper.util.ToolbarContentTintHelper
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.fragments.base.AbsPlayerFragment
import code.sn.monkey.retromusic.fragments.player.PlayerAlbumCoverFragment
import code.sn.monkey.retromusic.fragments.player.normal.PlayerFragment
import code.sn.monkey.retromusic.helper.MusicPlayerRemote
import code.sn.monkey.retromusic.model.Song
import code.sn.monkey.retromusic.util.color.MediaNotificationProcessor
import kotlinx.android.synthetic.main.fragment_material.*

/**
 * @author Hemanth S (h4h13).
 */
class MaterialFragment : AbsPlayerFragment(R.layout.fragment_material) {

    override fun playerToolbar(): Toolbar {
        return playerToolbar
    }

    private var lastColor: Int = 0

    override val paletteColor: Int
        get() = lastColor

    private lateinit var playbackControlsFragment: MaterialControlsFragment

    override fun onShow() {
        playbackControlsFragment.show()
    }

    override fun onHide() {
        playbackControlsFragment.hide()
        onBackPressed()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun toolbarIconColor(): Int {
        return ATHUtil.resolveColor(requireContext(), R.attr.colorControlNormal)
    }

    override fun onColorChanged(color: MediaNotificationProcessor) {
        playbackControlsFragment.setColor(color)
        lastColor = color.backgroundColor
        libraryViewModel.updateColor(color.backgroundColor)

        ToolbarContentTintHelper.colorizeToolbar(
            playerToolbar,
            ATHUtil.resolveColor(requireContext(), R.attr.colorControlNormal),
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSubFragments()
        setUpPlayerToolbar()
    }

    private fun setUpSubFragments() {
        playbackControlsFragment =
            childFragmentManager.findFragmentById(R.id.playbackControlsFragment) as MaterialControlsFragment
        val playerAlbumCoverFragment =
            childFragmentManager.findFragmentById(R.id.playerAlbumCoverFragment) as PlayerAlbumCoverFragment
        playerAlbumCoverFragment.setCallbacks(this)
    }

    private fun setUpPlayerToolbar() {
        playerToolbar.apply {
            inflateMenu(R.menu.menu_player)
            setNavigationOnClickListener { requireActivity().onBackPressed() }
            setOnMenuItemClickListener(this@MaterialFragment)
            ToolbarContentTintHelper.colorizeToolbar(
                this,
                ATHUtil.resolveColor(context, R.attr.colorControlNormal),
                requireActivity()
            )
        }
    }

    override fun onServiceConnected() {
        updateIsFavorite()
    }

    override fun onPlayingMetaChanged() {
        updateIsFavorite()
    }

    companion object {

        fun newInstance(): PlayerFragment {
            return PlayerFragment()
        }
    }
}
