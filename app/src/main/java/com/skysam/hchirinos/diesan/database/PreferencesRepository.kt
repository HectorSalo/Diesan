package com.skysam.hchirinos.diesan.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.skysam.hchirinos.diesan.common.Constants
import com.skysam.hchirinos.diesan.common.Diesan
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Created by Hector Chirinos on 19/02/2022.
 */

object PreferencesRepository {
	
	private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(Constants.PREFERENCES)
	
	private val PREFERENCE_THEME = stringPreferencesKey(Constants.PREFERENCE_THEME)
	
	fun getThemeSaved(): Flow<String> {
		return Diesan.Diesan.getContext().dataStore.data
			.map {
				it[PREFERENCE_THEME] ?: Constants.PREFERENCE_THEME_SYSTEM
			}
	}
	
	suspend fun changeTheme(newTheme: String) {
		Diesan.Diesan.getContext().dataStore.edit {
			it[PREFERENCE_THEME] = newTheme
		}
	}
}