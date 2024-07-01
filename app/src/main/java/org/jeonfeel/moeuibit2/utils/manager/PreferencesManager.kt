package org.jeonfeel.moeuibit2.utils.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jeonfeel.moeuibit2.constants.PREFRENCE_NAME

class PreferencesManager(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFRENCE_NAME)

    fun getInt(key: String): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[intPreferencesKey(key)] ?: -999
        }
    }

    fun getString(key: String): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)] ?: ""
        }
    }

    fun getBoolean(key: String): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[booleanPreferencesKey(key)] ?: false
        }
    }

    fun getLong(key: String): Flow<Long> {
        return context.dataStore.data.map { preferences ->
            preferences[longPreferencesKey(key)] ?: -999L
        }
    }

    fun getFloat(key: String): Flow<Float> {
        return context.dataStore.data.map { preferences ->
            preferences[floatPreferencesKey(key)] ?: -999f
        }
    }

    suspend fun setValue(key: String, value: Any) {
        context.dataStore.edit { preferences ->
            when (value) {
                is Int -> {
                    preferences[intPreferencesKey(key)] = value
                }

                is String -> {
                    preferences[stringPreferencesKey(key)] = value
                }

                is Boolean -> {
                    preferences[booleanPreferencesKey(key)] = value
                }

                is Long -> {
                    preferences[longPreferencesKey(key)] = value
                }

                is Float -> {
                    preferences[floatPreferencesKey(key)] = value
                }

                else -> {}
            }
        }
    }
}