

package code.sn.monkey.retromusic.preferences

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat.SRC_IN
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import code.sn.monkey.appthemehelper.common.prefs.supportv7.ATEDialogPreference
import code.sn.monkey.retromusic.App
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.dialogs.BlacklistFolderChooserDialog
import code.sn.monkey.retromusic.extensions.colorButtons
import code.sn.monkey.retromusic.extensions.colorControlNormal
import code.sn.monkey.retromusic.extensions.materialDialog
import code.sn.monkey.retromusic.providers.BlacklistStore
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.util.*

class BlacklistPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1,
    defStyleRes: Int = -1
) : ATEDialogPreference(context, attrs, defStyleAttr, defStyleRes) {

    init {
        icon?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                context.colorControlNormal(),
                SRC_IN
            )
    }
}

class BlacklistPreferenceDialog : DialogFragment(), BlacklistFolderChooserDialog.FolderCallback {
    companion object {
        fun newInstance(): BlacklistPreferenceDialog {
            return BlacklistPreferenceDialog()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val chooserDialog =
            childFragmentManager.findFragmentByTag("FOLDER_CHOOSER") as BlacklistFolderChooserDialog?
        chooserDialog?.setCallback(this)
        refreshBlacklistData()
        return materialDialog(R.string.blacklist)
            .setPositiveButton(R.string.done) { _, _ ->
                dismiss()
            }
            .setNeutralButton(R.string.clear_action) { _, _ ->
                materialDialog(R.string.clear_blacklist)
                    .setMessage(R.string.do_you_want_to_clear_the_blacklist)
                    .setPositiveButton(R.string.clear_action) { _, _ ->
                        BlacklistStore.getInstance(
                            requireContext()
                        ).clear()
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .create()
                    .colorButtons()
                    .show()
            }
            .setNegativeButton(R.string.add_action) { _, _ ->
                val dialog = BlacklistFolderChooserDialog.create()
                dialog.setCallback(this@BlacklistPreferenceDialog)
                dialog.show(requireActivity().supportFragmentManager, "FOLDER_CHOOSER")
            }
            .setItems(paths.toTypedArray()) { _, which ->
                materialDialog(R.string.remove_from_blacklist)
                    .setMessage(
                        HtmlCompat.fromHtml(
                            String.format(
                                getString(
                                    R.string.do_you_want_to_remove_from_the_blacklist
                                ),
                                paths[which]
                            ),
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                    )
                    .setPositiveButton(R.string.remove_action) { _, _ ->
                        BlacklistStore.getInstance(App.getContext())
                            .removePath(File(paths[which]))
                        refreshBlacklistData()
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .create()
                    .colorButtons()
                    .show()
            }
            .create().colorButtons()
    }

    private lateinit var paths: ArrayList<String>

    private fun refreshBlacklistData() {
        this.paths = BlacklistStore.getInstance(App.getContext()).paths
        val dialog = dialog as MaterialAlertDialogBuilder?
        dialog?.setItems(paths.toTypedArray(), null)
    }

    override fun onFolderSelection(dialog: BlacklistFolderChooserDialog, folder: File) {
        BlacklistStore.getInstance(App.getContext()).addPath(folder)
        refreshBlacklistData()
    }
}
