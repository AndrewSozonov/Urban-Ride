package com.andrewsozonov.urbanride.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.andrewsozonov.urbanride.R


/**
 *  Фрагмент с настройками приложения
 */
class SettingsFragment : PreferenceFragmentCompat() {


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, null)

    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        preference?.let {
            if (it.key == getString(R.string.dark_theme_pref_key)) {
                activity?.recreate()
            }
        }
        return super.onPreferenceTreeClick(preference)
    }
}