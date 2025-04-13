package com.smartpixel.themovielist.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    companion object {
        private val IS_GRID_LAYOUT = booleanPreferencesKey("is_grid_layout")
    }

    val isGridLayoutFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_GRID_LAYOUT] ?: false
        }

    suspend fun setGridLayout(isGridLayout: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_GRID_LAYOUT] = isGridLayout
        }
    }
}