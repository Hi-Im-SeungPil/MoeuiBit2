package org.jeonfeel.moeuibit2.ui.theme

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeHelper {
    enum class ThemeMode { LIGHT, DARK, DEFAULT }

    fun applyTheme(themeMode: ThemeMode) {
        when (themeMode) {
            ThemeMode.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            ThemeMode.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            ThemeMode.DEFAULT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            else ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
        }
    }
}

object ThemeManager {
    private val _themeState: MutableStateFlow<ThemeHelper.ThemeMode> =
        MutableStateFlow(ThemeHelper.ThemeMode.DEFAULT)
    val themeState: StateFlow<ThemeHelper.ThemeMode> = _themeState.asStateFlow()

    fun setDarkTheme(themeMode: ThemeHelper.ThemeMode) {
        _themeState.value = themeMode
    }
}