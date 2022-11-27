package org.jeonfeel.moeuibit2.manager

import android.content.SharedPreferences
import org.jeonfeel.moeuibit2.constant.PREF_KEY_KRW_ASK_FEE
import org.jeonfeel.moeuibit2.constant.PREF_KEY_KRW_BID_FEE

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
        val result = prefrence.getFloat(key,-999f)
        return if (result == -999f) {
            if (key == PREF_KEY_KRW_ASK_FEE || key == PREF_KEY_KRW_BID_FEE) {
                prefrence.getFloat(key,0.05f)
            } else {
                prefrence.getFloat(key,0.25f)
            }
        } else {
            result
        }
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