
package code.sn.monkey.retromusic.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import code.sn.monkey.retromusic.EXTRA_SONG
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.db.PlaylistEntity
import code.sn.monkey.retromusic.db.toSongEntity
import code.sn.monkey.retromusic.extensions.colorButtons
import code.sn.monkey.retromusic.extensions.extra
import code.sn.monkey.retromusic.extensions.materialDialog
import code.sn.monkey.retromusic.fragments.LibraryViewModel
import code.sn.monkey.retromusic.fragments.ReloadType.Playlists
import code.sn.monkey.retromusic.model.Song
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.dialog_playlist.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CreatePlaylistDialog : DialogFragment() {
    private val libraryViewModel by sharedViewModel<LibraryViewModel>()

    companion object {
        fun create(song: Song): CreatePlaylistDialog {
            val list = mutableListOf<Song>()
            list.add(song)
            return create(list)
        }

        fun create(songs: List<Song>): CreatePlaylistDialog {
            return CreatePlaylistDialog().apply {
                arguments = bundleOf(EXTRA_SONG to songs)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_playlist, null)
        val songs: List<Song> = extra<List<Song>>(EXTRA_SONG).value ?: emptyList()
        val playlistView: TextInputEditText = view.actionNewPlaylist
        val playlistContainer: TextInputLayout = view.actionNewPlaylistContainer
        return materialDialog(R.string.new_playlist_title)
            .setView(view)
            .setPositiveButton(
                R.string.create_action
            ) { _, _ ->
                val playlistName = playlistView.text.toString()
                if (!TextUtils.isEmpty(playlistName)) {
                    libraryViewModel.addToPlaylist(playlistName, songs)

                } else {
                    playlistContainer.error = "Playlist is can't be empty"
                }
            }
            .create()
            .colorButtons()
    }
}
