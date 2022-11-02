package org.jeonfeel.moeuibit2.manager

import android.content.SharedPreferences

class PreferenceManager(private val prefrence: SharedPreferences) {
    fun getInt(key: String): Int {
        return prefrence.getInt(key, -999)
    }

    fun getString(key: String): String? {
        return prefrence.getString(key,"-999")
    }

    fun getBoolean(key: String): Boolean {
        return prefrence.getBoolean(key,true)
    }

    fun getLong(key: String): Long {
        return prefrence.getLong(key,-999)
    }

    fun getFloat(key: String): Float {
        return prefrence.getFloat(key,-999f)
    }

    suspend fun setValue(key: String, value: Any, completeAction: (() -> Unit)? = null) = with(prefrence.edit()) {
        when (value) {
            is Int -> {
                putInt(key, value)
                commit()
                completeAction?.let {
                    it()
                }
            }
            is String -> {
                putString(key, value)
                commit()
                completeAction?.let {
                    it()
                }
            }
            is Boolean -> {
                putBoolean(key, value)
                commit()
                completeAction?.let {
                    it()
                }
            }
            is Long -> {
                putLong(key, value)
                commit()
                completeAction?.let {
                    it()
                }
            }
            is Float -> {
                putFloat(key, value)
                commit()
                completeAction?.let {
                    it()
                }
            }
            else -> {}
        }
    }
}