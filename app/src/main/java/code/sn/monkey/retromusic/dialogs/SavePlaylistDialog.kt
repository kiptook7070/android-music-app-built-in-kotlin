
package code.sn.monkey.retromusic.dialogs

import android.app.Dialog
import android.media.MediaScannerConnection
import android.os.Bundle
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import code.sn.monkey.retromusic.App
import code.sn.monkey.retromusic.EXTRA_PLAYLIST
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.db.PlaylistWithSongs
import code.sn.monkey.retromusic.extensions.colorButtons
import code.sn.monkey.retromusic.extensions.extraNotNull
import code.sn.monkey.retromusic.extensions.materialDialog
import code.sn.monkey.retromusic.util.PlaylistsUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SavePlaylistDialog : DialogFragment() {
    companion object {
        fun create(playlistWithSongs: PlaylistWithSongs): SavePlaylistDialog {
            return SavePlaylistDialog().apply {
                arguments = bundleOf(
                    EXTRA_PLAYLIST to playlistWithSongs
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            val playlistWithSongs = extraNotNull<PlaylistWithSongs>(EXTRA_PLAYLIST).value
            val file = PlaylistsUtil.savePlaylistWithSongs(playlistWithSongs)
            MediaScannerConnection.scanFile(
                requireActivity(),
                arrayOf<String>(file.path),
                null
            ) { _, _ ->
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    requireContext(),
                    String.format(App.getContext().getString(R.string.saved_playlist_to), file),
                    Toast.LENGTH_LONG
                ).show()
                dismiss()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return materialDialog(R.string.save_playlist_title)
            .setView(R.layout.loading)
            .create().colorButtons()
    }
}
