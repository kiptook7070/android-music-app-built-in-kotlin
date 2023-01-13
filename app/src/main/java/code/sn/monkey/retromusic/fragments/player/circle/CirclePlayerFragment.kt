
package code.sn.monkey.retromusic.fragments.player.circle

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.appcompat.widget.Toolbar
import code.sn.monkey.appthemehelper.ThemeStore
import code.sn.monkey.appthemehelper.util.ATHUtil
import code.sn.monkey.appthemehelper.util.ColorUtil
import code.sn.monkey.appthemehelper.util.TintHelper
import code.sn.monkey.appthemehelper.util.ToolbarContentTintHelper
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.extensions.accentColor
import code.sn.monkey.retromusic.extensions.applyColor
import code.sn.monkey.retromusic.extensions.hide
import code.sn.monkey.retromusic.extensions.show
import code.sn.monkey.retromusic.fragments.base.AbsPlayerControlsFragment
import code.sn.monkey.retromusic.fragments.base.AbsPlayerFragment
import code.sn.monkey.retromusic.helper.MusicPlayerRemote
import code.sn.monkey.retromusic.helper.MusicProgressViewUpdateHelper
import code.sn.monkey.retromusic.helper.MusicProgressViewUpdateHelper.Callback
import code.sn.monkey.retromusic.helper.PlayPauseButtonOnClickHandler
import code.sn.monkey.retromusic.misc.SimpleOnSeekbarChangeListener
import code.sn.monkey.retromusic.util.MusicUtil
import code.sn.monkey.retromusic.util.PreferenceUtil
import code.sn.monkey.retromusic.util.ViewUtil
import code.sn.monkey.retromusic.util.color.MediaNotificationProcessor
import code.sn.monkey.retromusic.views.SeekArc
import code.sn.monkey.retromusic.views.SeekArc.OnSeekArcChangeListener
import code.sn.monkey.retromusic.volume.AudioVolumeObserver
import code.sn.monkey.retromusic.volume.OnAudioVolumeChangedListener
import kotlinx.android.synthetic.main.fragment_circle_player.*

/**
 * Created by hemanths on 2020-01-06.
 */

class CirclePlayerFragment : AbsPlayerFragment(R.layout.fragment_circle_player), Callback,
    OnAudioVolumeChangedListener,
    OnSeekArcChangeListener {

    private lateinit var progressViewUpdateHelper: MusicProgressViewUpdateHelper
    private var audioVolumeObserver: AudioVolumeObserver? = null

    private val audioManager: AudioManager?
        get() = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressViewUpdateHelper = MusicProgressViewUpdateHelper(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_circle_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        title.isSelected = true
    }

    private fun setUpPlayerToolbar() {
        playerToolbar.apply {
            inflateMenu(R.menu.menu_player)
            setNavigationOnClickListener { requireActivity().onBackPressed() }
            setOnMenuItemClickListener(this@CirclePlayerFragment)
            ToolbarContentTintHelper.colorizeToolbar(
                this,
                ATHUtil.resolveColor(requireContext(), R.attr.colorControlNormal),
                requireActivity()
            )
        }
    }

    private fun setupViews() {
        setUpProgressSlider()
        ViewUtil.setProgressDrawable(
            progressSlider,
            ThemeStore.accentColor(requireContext()),
            false
        )
        volumeSeekBar.progressColor = accentColor()
        volumeSeekBar.arcColor = ColorUtil.withAlpha(accentColor(), 0.25f)
        setUpPlayPauseFab()
        setUpPrevNext()
        setUpPlayerToolbar()
    }

    private fun setUpPrevNext() {
        updatePrevNextColor()
        nextButton.setOnClickListener { MusicPlayerRemote.playNextSong() }
        previousButton.setOnClickListener { MusicPlayerRemote.back() }
    }

    private fun updatePrevNextColor() {
        val accentColor = ThemeStore.accentColor(requireContext())
        nextButton.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN)
        previousButton.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN)
    }

    private fun setUpPlayPauseFab() {
        TintHelper.setTintAuto(playPauseButton, ThemeStore.accentColor(requireContext()), false)
        playPauseButton.setOnClickListener(PlayPauseButtonOnClickHandler())
    }

    override fun onResume() {
        super.onResume()
        progressViewUpdateHelper.start()
        if (audioVolumeObserver == null) {
            audioVolumeObserver = AudioVolumeObserver(requireActivity())
        }
        audioVolumeObserver?.register(AudioManager.STREAM_MUSIC, this)

        val audioManager = audioManager
        if (audioManager != null) {
            volumeSeekBar.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            volumeSeekBar.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        }
        volumeSeekBar.setOnSeekArcChangeListener(this)
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

    override fun onBackPressed(): Boolean = false

    override fun toolbarIconColor(): Int =
        ATHUtil.resolveColor(requireContext(), android.R.attr.colorControlNormal)

    override val paletteColor: Int
        get() = Color.BLACK

    override fun onColorChanged(color: MediaNotificationProcessor) {
    }

    override fun onFavoriteToggled() {
    }

    override fun onPlayStateChanged() {
        updatePlayPauseDrawableState()
    }

    override fun onPlayingMetaChanged() {
        super.onPlayingMetaChanged()
        updateSong()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        updateSong()
        updatePlayPauseDrawableState()
    }

    private fun updateSong() {
        val song = MusicPlayerRemote.currentSong
        title.text = song.title
        text.text = song.artistName

        if (PreferenceUtil.isSongInfo) {
            songInfo.text = getSongInfo(song)
            songInfo.show()
        } else {
            songInfo.hide()
        }
    }

    private fun updatePlayPauseDrawableState() {
        when {
            MusicPlayerRemote.isPlaying -> playPauseButton.setImageResource(R.drawable.ic_pause)
            else -> playPauseButton.setImageResource(R.drawable.ic_play_arrow)
        }
    }

    override fun onAudioVolumeChanged(currentVolume: Int, maxVolume: Int) {
        if (volumeSeekBar == null) {
            return
        }
        volumeSeekBar.max = maxVolume
        volumeSeekBar.progress = currentVolume
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (audioVolumeObserver != null) {
            audioVolumeObserver!!.unregister()
        }
    }

    override fun onProgressChanged(seekArc: SeekArc?, progress: Int, fromUser: Boolean) {
        val audioManager = audioManager
        audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
    }

    override fun onStartTrackingTouch(seekArc: SeekArc?) {
    }

    override fun onStopTrackingTouch(seekArc: SeekArc?) {
    }

    fun setUpProgressSlider() {
        progressSlider.applyColor(accentColor())
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
        animator.duration = AbsPlayerControlsFragment.SLIDER_ANIMATION_TIME
        animator.interpolator = LinearInterpolator()
        animator.start()

        songTotalTime.text = MusicUtil.getReadableDurationString(total.toLong())
        songCurrentProgress.text = MusicUtil.getReadableDurationString(progress.toLong())
    }
}
