
package code.sn.monkey.retromusic.fragments.settings

import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import code.sn.monkey.appthemehelper.common.prefs.supportv7.ATEListPreference
import code.sn.monkey.retromusic.LANGUAGE_NAME
import code.sn.monkey.retromusic.LAST_ADDED_CUTOFF
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.fragments.LibraryViewModel
import code.sn.monkey.retromusic.fragments.ReloadType.HomeSections
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * @author Hemanth S (h4h13).
 */

class OtherSettingsFragment : AbsSettingsFragment() {
    private val libraryViewModel by sharedViewModel<LibraryViewModel>()

    override fun invalidateSettings() {
        val languagePreference: ATEListPreference? = findPreference(LANGUAGE_NAME)
        languagePreference?.setOnPreferenceChangeListener { _, _ ->
            requireActivity().recreate()
            return@setOnPreferenceChangeListener true
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_advanced)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val preference: Preference? = findPreference(LAST_ADDED_CUTOFF)
        preference?.setOnPreferenceChangeListener { lastAdded, newValue ->
            setSummary(lastAdded, newValue)
            libraryViewModel.forceReload(HomeSections)
            true
        }
        val languagePreference: Preference? = findPreference(LANGUAGE_NAME)
        languagePreference?.setOnPreferenceChangeListener { prefs, newValue ->
            setSummary(prefs, newValue)
            true
        }
    }
}
