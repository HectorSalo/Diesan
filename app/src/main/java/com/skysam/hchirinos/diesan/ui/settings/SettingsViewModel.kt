package com.skysam.hchirinos.diesan.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.diesan.database.PreferencesRepository
import com.skysam.hchirinos.diesan.notification.CloudMessaging

/**
 * Created by Hector Chirinos on 19/02/2022.
 */

class SettingsViewModel: ViewModel() {
 val theme: LiveData<String> = PreferencesRepository.getThemeSaved().asLiveData()
 val notificationActive: LiveData<Boolean> = PreferencesRepository.getNotificationStatus().asLiveData()
 
 suspend fun changeTheme(newTheme: String) {
  PreferencesRepository.changeTheme(newTheme)
 }
 
 suspend fun changeNotificationStatus(status: Boolean) {
  PreferencesRepository.changeNotificationStatus(status)
 }
}