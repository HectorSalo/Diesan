package com.skysam.hchirinos.diesan.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.skysam.hchirinos.diesan.BuildConfig
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Constants
import kotlinx.coroutines.launch

/**
 * Created by Hector Chirinos (Home) on 27/12/2021.
 */
class SettingsFragment: PreferenceFragmentCompat() {
    
    private val viewModel: SettingsViewModel by activityViewModels()
    private lateinit var listTheme: ListPreference
    private lateinit var currentTheme: String
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        listTheme = findPreference(Constants.PREFERENCE_THEME)!!
    
        listTheme.setOnPreferenceChangeListener { _, newValue ->
            val themeSelected = newValue as String
            if (themeSelected != currentTheme) {
                lifecycleScope.launch {
                    viewModel.changeTheme(themeSelected)
                    val intent = Intent(requireContext(), SettingsActivity::class.java)
                    requireActivity().finish()
                    requireActivity().startActivity(intent)
                    requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            }
            true
        }

        val aboutPreference: PreferenceScreen = findPreference("about")!!
        aboutPreference.setOnPreferenceClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_settingsFragment_to_aboutFragment)
            true
        }

        val versionPreferenceScreen = findPreference<PreferenceScreen>("name_version")
        versionPreferenceScreen?.title = getString(R.string.version_name, BuildConfig.VERSION_NAME)
        
        loadViewModels()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().finish()
        }
        return super.onOptionsItemSelected(item)
    }
    
    private fun loadViewModels() {
        viewModel.theme.observe(viewLifecycleOwner) {
            currentTheme = it
            when(it) {
                Constants.PREFERENCE_THEME_SYSTEM -> listTheme.value = Constants.PREFERENCE_THEME_SYSTEM
                Constants.PREFERENCE_THEME_DARK -> listTheme.value = Constants.PREFERENCE_THEME_DARK
                Constants.PREFERENCE_THEME_LIGHT -> listTheme.value = Constants.PREFERENCE_THEME_LIGHT
            }
        }
    }
}