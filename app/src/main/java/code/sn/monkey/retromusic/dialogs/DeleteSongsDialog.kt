
package code.sn.monkey.retromusic.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import code.sn.monkey.retromusic.EXTRA_SONG
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.extensions.colorButtons
import code.sn.monkey.retromusic.extensions.extraNotNull
import code.sn.monkey.retromusic.extensions.materialDialog
import code.sn.monkey.retromusic.fragments.LibraryViewModel
import code.sn.monkey.retromusic.helper.MusicPlayerRemote
import code.sn.monkey.retromusic.model.Song
import code.sn.monkey.retromusic.util.MusicUtil
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DeleteSongsDialog : DialogFragment() {
    private val libraryViewModel by sharedViewModel<LibraryViewModel>()

    companion object {
        fun create(song: Song): DeleteSongsDialog {
            val list = ArrayList<Song>()
            list.add(song)
            return create(list)
        }

        fun create(songs: List<Song>): DeleteSongsDialog {
            return DeleteSongsDialog().apply {
                arguments = bundleOf(
                    EXTRA_SONG to ArrayList(songs)
                )
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val songs = extraNotNull<List<Song>>(EXTRA_SONG).value
        val pair = if (songs.size > 1) {
            Pair(
                R.string.delete_songs_title,
                HtmlCompat.fromHtml(
                    String.format(getString(R.string.delete_x_songs), songs.size),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            )
        } else {
            Pair(
                R.string.delete_song_title,
                HtmlCompat.fromHtml(
                    String.format(getString(R.string.delete_song_x), songs[0].title),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            )
        }

        return materialDialog(pair.first)
            .setMessage(pair.second)
            .setCancelable(false)
            .setPositiveButton(R.string.action_delete) { _, _ ->
                if (songs.isNotEmpty() and (songs.size == 1) and MusicPlayerRemote.isPlaying(songs.first())) {
                    MusicPlayerRemote.playNextSong()
                }
                MusicUtil.deleteTracks(requireActivity(), songs)
                libraryViewModel.deleteTracks(songs)
            }
            .create()
            .colorButtons()
    }
}
