
package code.sn.monkey.retromusic.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import code.sn.monkey.retromusic.EXTRA_PLAYLISTS
import code.sn.monkey.retromusic.EXTRA_SONG
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.db.PlaylistEntity
import code.sn.monkey.retromusic.db.toSongsEntity
import code.sn.monkey.retromusic.extensions.colorButtons
import code.sn.monkey.retromusic.extensions.extraNotNull
import code.sn.monkey.retromusic.extensions.materialDialog
import code.sn.monkey.retromusic.fragments.LibraryViewModel
import code.sn.monkey.retromusic.fragments.ReloadType.Playlists
import code.sn.monkey.retromusic.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddToPlaylistDialog : DialogFragment() {
    private val libraryViewModel by sharedViewModel<LibraryViewModel>()

    companion object {
        fun create(playlistEntities: List<PlaylistEntity>, song: Song): AddToPlaylistDialog {
            val list: MutableList<Song> = mutableListOf()
            list.add(song)
            return create(playlistEntities, list)
        }

        fun create(playlistEntities: List<PlaylistEntity>, songs: List<Song>): AddToPlaylistDialog {
            return AddToPlaylistDialog().apply {
                arguments = bundleOf(
                    EXTRA_SONG to songs,
                    EXTRA_PLAYLISTS to playlistEntities
                )
            }
        }
    }

    private fun playlistAdapter(playlists: List<String>): ArrayAdapter<String> {
        val adapter = ArrayAdapter<String>(requireContext(), R.layout.item_simple_text, R.id.title)
        adapter.addAll(playlists)
        return adapter
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val playlistEntities = extraNotNull<List<PlaylistEntity>>(EXTRA_PLAYLISTS).value
        val songs = extraNotNull<List<Song>>(EXTRA_SONG).value
        val playlistNames = mutableListOf<String>()
        playlistNames.add(requireContext().resources.getString(R.string.action_new_playlist))
        for (entity: PlaylistEntity in playlistEntities) {
            playlistNames.add(entity.playlistName)
        }
        return materialDialog(R.string.add_playlist_title)
            .setAdapter(
                playlistAdapter(playlistNames)
            ) { dialog, which ->
                if (which == 0) {
                    showCreateDialog(songs)
                } else {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val songEntities = songs.toSongsEntity(playlistEntities[which - 1])
                        libraryViewModel.insertSongs(songEntities)
                        libraryViewModel.forceReload(Playlists)
                    }
                }
                dialog.dismiss()
            }
            .create().colorButtons()
    }

    private fun showCreateDialog(songs: List<Song>) {
        CreatePlaylistDialog.create(songs).show(requireActivity().supportFragmentManager, "Dialog")
    }
}
