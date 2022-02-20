package com.skysam.hchirinos.diesan.common

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.diesan.database.PreferencesRepository

/**
 * Created by Hector Chirinos (Home) on 27/12/2021.
 */
class Diesan: Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        PreferencesRepository.getThemeSaved().asLiveData().observeForever {
            when (it) {
                Constants.PREFERENCE_THEME_SYSTEM -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                )
                Constants.PREFERENCE_THEME_DARK -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
                Constants.PREFERENCE_THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }
    }

    object Diesan {
        fun getContext(): Context = appContext
    }
}