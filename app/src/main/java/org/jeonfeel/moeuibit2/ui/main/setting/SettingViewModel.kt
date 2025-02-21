package org.jeonfeel.moeuibit2.ui.main.setting

import android.content.res.Resources.Theme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.constants.KeyConst
import org.jeonfeel.moeuibit2.constants.ioDispatcher
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import org.jeonfeel.moeuibit2.ui.theme.ThemeHelper
import org.jeonfeel.moeuibit2.ui.theme.ThemeManager
import javax.inject.Inject

data class SettingScreenState(
    val openSourceState: MutableState<Boolean> = mutableStateOf(false),
    val currentTheme: MutableState<ThemeHelper.ThemeMode> = mutableStateOf(ThemeHelper.ThemeMode.DEFAULT),
)

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val preferenceManager: PreferencesManager,
) : BaseViewModel(preferenceManager) {
    val state = SettingScreenState()

    init {
        viewModelScope.launch {
            preferenceManager.getString(KeyConst.PREF_KEY_THEME_MODE).collect {
                state.currentTheme.value = when (it) {
                    ThemeHelper.ThemeMode.LIGHT.name -> ThemeHelper.ThemeMode.LIGHT
                    ThemeHelper.ThemeMode.DARK.name -> ThemeHelper.ThemeMode.DARK
                    else -> ThemeHelper.ThemeMode.DEFAULT
                }
            }
        }
    }

    fun removeAll() {
        viewModelScope.launch(ioDispatcher) {
            localRepository.getUserDao().deleteAll()
            localRepository.getFavoriteDao().deleteAll()
            localRepository.getTransactionInfoDao().deleteAll()
            localRepository.getMyCoinDao().deleteAll()
        }
    }

    fun updateTheme(themeMode: ThemeHelper.ThemeMode) {
        viewModelScope.launch {
            state.currentTheme.value = themeMode
            preferenceManager.setValue(KeyConst.PREF_KEY_THEME_MODE, themeMode.name)
        }
        ThemeHelper.applyTheme(themeMode)
    }

    fun resetTransactionInfo() {
        viewModelScope.launch(ioDispatcher) {
            localRepository.getTransactionInfoDao().deleteAll()
        }
    }
}