
package code.sn.monkey.retromusic.fragments.player.full

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.PopupMenu
import android.widget.SeekBar
import androidx.lifecycle.lifecycleScope
import code.sn.monkey.appthemehelper.util.ColorUtil
import code.sn.monkey.appthemehelper.util.TintHelper
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.db.PlaylistEntity
import code.sn.monkey.retromusic.db.SongEntity
import code.sn.monkey.retromusic.db.toSongEntity
import code.sn.monkey.retromusic.extensions.applyColor
import code.sn.monkey.retromusic.extensions.hide
import code.sn.monkey.retromusic.extensions.show
import code.sn.monkey.retromusic.fragments.LibraryViewModel
import code.sn.monkey.retromusic.fragments.ReloadType
import code.sn.monkey.retromusic.fragments.base.AbsPlayerControlsFragment
import code.sn.monkey.retromusic.helper.MusicPlayerRemote
import code.sn.monkey.retromusic.helper.MusicProgressViewUpdateHelper
import code.sn.monkey.retromusic.helper.PlayPauseButtonOnClickHandler
import code.sn.monkey.retromusic.misc.SimpleOnSeekbarChangeListener
import code.sn.monkey.retromusic.model.Song
import code.sn.monkey.retromusic.service.MusicService
import code.sn.monkey.retromusic.util.MusicUtil
import code.sn.monkey.retromusic.util.PreferenceUtil
import code.sn.monkey.retromusic.util.RetroUtil
import code.sn.monkey.retromusic.util.color.MediaNotificationProcessor
import kotlinx.android.synthetic.main.fragment_full_player_controls.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by hemanths on 20/09/17.
 */

class FullPlaybackControlsFragment :
    AbsPlayerControlsFragment(R.layout.fragment_full_player_controls),
    PopupMenu.OnMenuItemClickListener {

    private var lastPlaybackControlsColor: Int = 0
    private var lastDisabledPlaybackControlsColor: Int = 0
    private lateinit var progressViewUpdateHelper: MusicProgressViewUpdateHelper
    private val libraryViewModel: LibraryViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressViewUpdateHelper = MusicProgressViewUpdateHelper(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMusicControllers()

        songTotalTime.setTextColor(Color.WHITE)
        songCurrentProgress.setTextColor(Color.WHITE)
        title.isSelected = true
    }

    override fun onResume() {
        super.onResume()
        progressViewUpdateHelper.start()
    }

    override fun onPause() {
        super.onPause()
        progressViewUpdateHelper.stop()
    }

    public override fun show() {
        playPauseButton!!.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    public override fun hide() {
        playPauseButton.apply {
            scaleX = 0f
            scaleY = 0f
            rotation = 0f
        }
    }

    override fun setColor(color: MediaNotificationProcessor) {
        lastPlaybackControlsColor = color.primaryTextColor
        lastDisabledPlaybackControlsColor = ColorUtil.withAlpha(color.primaryTextColor, 0.3f)

        val tintList = ColorStateList.valueOf(color.primaryTextColor)
        playerMenu.imageTintList = tintList
        songFavourite.imageTintList = tintList
        volumeFragment?.setTintableColor(color.primaryTextColor)
        progressSlider.applyColor(color.primaryTextColor)
        title.setTextColor(color.primaryTextColor)
        text.setTextColor(color.secondaryTextColor)
        songInfo.setTextColor(color.secondaryTextColor)
        songCurrentProgress.setTextColor(color.secondaryTextColor)
        songTotalTime.setTextColor(color.secondaryTextColor)

        playPauseButton.backgroundTintList = tintList
        playPauseButton.imageTintList = ColorStateList.valueOf(color.backgroundColor)

        updateRepeatState()
        updateShuffleState()
        updatePrevNextColor()
    }

    override fun onServiceConnected() {
        updatePlayPauseDrawableState()
        updateRepeatState()
        updateShuffleState()
        updateSong()
    }

    private fun updateSong() {
        val song = MusicPlayerRemote.currentSong
        title.text = song.title
        text.text = song.artistName
        updateIsFavorite()
        if (PreferenceUtil.isSongInfo) {
            songInfo.text = getSongInfo(song)
            songInfo.show()
        } else {
            songInfo.hide()
        }
    }

    override fun onPlayingMetaChanged() {
        super.onPlayingMetaChanged()
        updateSong()
    }

    override fun onPlayStateChanged() {
        updatePlayPauseDrawableState()
    }

    private fun updatePlayPauseDrawableState() {
        if (MusicPlayerRemote.isPlaying) {
            playPauseButton.setImageResource(R.drawable.ic_pause)
        } else {
            playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_32dp)
        }
    }

    private fun setUpPlayPauseFab() {
        playPauseButton.setOnClickListener(PlayPauseButtonOnClickHandler())
        playPauseButton.post {
            if (playPauseButton != null) {
                playPauseButton.pivotX = (playPauseButton.width / 2).toFloat()
                playPauseButton.pivotY = (playPauseButton.height / 2).toFloat()
            }
        }
    }

    private fun setUpMusicControllers() {
        setUpPlayPauseFab()
        setUpPrevNext()
        setUpRepeatButton()
        setUpShuffleButton()
        setUpProgressSlider()
        setupFavourite()
        setupMenu()
    }

    private fun setupMenu() {
        playerMenu.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.inflate(R.menu.menu_player)
            popupMenu.show()
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return (parentFragment as FullPlayerFragment).onMenuItemClick(item!!)
    }

    private fun setUpPrevNext() {
        updatePrevNextColor()
        nextButton.setOnClickListener { MusicPlayerRemote.playNextSong() }
        previousButton.setOnClickListener { MusicPlayerRemote.back() }
    }

    private fun updatePrevNextColor() {
        nextButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
        previousButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
    }

    override fun setUpProgressSlider() {
        progressSlider.setOnSeekBarChangeListener(object : SimpleOnSeekbarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    MusicPlayerRemote.seekTo(progress)
                    onUpdateProgressViews(
                        MusicPlayerRemote.songProgressMillis,
                        MusicPlayerRemote.songDurationMillis
                    )
                }
            }
        })
    }

    override fun onUpdateProgressViews(progress: Int, total: Int) {
        progressSlider.max = total

        val animator = ObjectAnimator.ofInt(progressSlider, "progress", progress)
        animator.duration = SLIDER_ANIMATION_TIME
        animator.interpolator = LinearInterpolator()
        animator.start()

        songTotalTime.text = MusicUtil.getReadableDurationString(total.toLong())
        songCurrentProgress.text = MusicUtil.getReadableDurationString(progress.toLong())
    }

    override fun onRepeatModeChanged() {
        updateRepeatState()
    }

    override fun onShuffleModeChanged() {
        updateShuffleState()
    }

    private fun setUpShuffleButton() {
        shuffleButton.setOnClickListener { MusicPlayerRemote.toggleShuffleMode() }
    }

    override fun updateShuffleState() {
        when (MusicPlayerRemote.shuffleMode) {
            MusicService.SHUFFLE_MODE_SHUFFLE -> shuffleButton.setColorFilter(
                lastPlaybackControlsColor,
                PorterDuff.Mode.SRC_IN
            )
            else -> shuffleButton.setColorFilter(
                lastDisabledPlaybackControlsColor,
                PorterDuff.Mode.SRC_IN
            )
        }
    }

    private fun setUpRepeatButton() {
        repeatButton.setOnClickListener { MusicPlayerRemote.cycleRepeatMode() }
    }

    override fun updateRepeatState() {
        when (MusicPlayerRemote.repeatMode) {
            MusicService.REPEAT_MODE_NONE -> {
                repeatButton.setImageResource(R.drawable.ic_repeat)
                repeatButton.setColorFilter(
                    lastDisabledPlaybackControlsColor,
                    PorterDuff.Mode.SRC_IN
                )
            }
            MusicService.REPEAT_MODE_ALL -> {
                repeatButton.setImageResource(R.drawable.ic_repeat)
                repeatButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
            }
            MusicService.REPEAT_MODE_THIS -> {
                repeatButton.setImageResource(R.drawable.ic_repeat_one)
                repeatButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
            }
        }
    }

    private fun setupFavourite() {
        songFavourite?.setOnClickListener {
            toggleFavorite(MusicPlayerRemote.currentSong)
        }
    }

    fun updateIsFavorite() {
        lifecycleScope.launch(Dispatchers.IO) {
            val playlist: PlaylistEntity? = libraryViewModel.favoritePlaylist()
            if (playlist != null) {
                val song: SongEntity =
                    MusicPlayerRemote.currentSong.toSongEntity(playlist.playListId)
                val isFavorite: Boolean = libraryViewModel.isFavoriteSong(song).isNotEmpty()
                withContext(Dispatchers.Main) {
                    val icon =
                        if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
                    val drawable = TintHelper.createTintedDrawable(activity, icon, Color.WHITE)
                    songFavourite?.setImageDrawable(drawable)
                }
            }
        }
    }

    private fun toggleFavorite(song: Song) {
        if (song.id == MusicPlayerRemote.currentSong.id) {
            updateIsFavorite()
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val playlist: PlaylistEntity? = libraryViewModel.favoritePlaylist()
            if (playlist != null) {
                val songEntity = song.toSongEntity(playlist.playListId)
                val isFavorite = libraryViewModel.isFavoriteSong(songEntity).isNotEmpty()
                if (isFavorite) {
                    libraryViewModel.removeSongFromPlaylist(songEntity)
                } else {
                    libraryViewModel.insertSongs(listOf(song.toSongEntity(playlist.playListId)))
                }
            }
            libraryViewModel.forceReload(ReloadType.Playlists)
            requireContext().sendBroadcast(Intent(MusicService.FAVORITE_STATE_CHANGED))
        }
    }

    fun onFavoriteToggled() {
        toggleFavorite(MusicPlayerRemote.currentSong)
    }
}
