
package code.sn.monkey.retromusic.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.extensions.colorButtons
import code.sn.monkey.retromusic.extensions.materialDialog
import code.sn.monkey.retromusic.fragments.LibraryViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ImportPlaylistDialog : DialogFragment() {
    private val libraryViewModel by sharedViewModel<LibraryViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return materialDialog(R.string.import_playlist)
            .setMessage(R.string.import_playlist_message)
            .setPositiveButton(R.string.import_label) { _, _ ->
                libraryViewModel.importPlaylists()
            }
            .create()
            .colorButtons()
    }
}
