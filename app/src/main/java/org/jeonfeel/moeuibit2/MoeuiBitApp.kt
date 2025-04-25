package org.jeonfeel.moeuibit2

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.constants.KeyConst
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import org.jeonfeel.moeuibit2.ui.theme.ThemeHelper
import org.jeonfeel.moeuibit2.ui.theme.ThemeManager
import org.jeonfeel.moeuibit2.utils.manager.AppOpenAdManager
import javax.inject.Inject

object GlobalState {
    private val _globalExchangeState = mutableStateOf(EXCHANGE_UPBIT)
    val globalExchangeState: State<String> get() = _globalExchangeState

    fun setExchangeState(newState: String) {
        _globalExchangeState.value = newState
    }
}

@HiltAndroidApp
class MoeuiBitApp : Application() {

    @Inject
    lateinit var preferencesManager: PreferencesManager
    private lateinit var appOpenAdManager: AppOpenAdManager

    override fun onCreate() {
        super.onCreate()
        runBlocking {
            applyTheme()
            initExchange()
            initAd()
        }

        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
//                return BuildConfig.DEBUG
                return true
            }
        })
    }

    private suspend fun applyTheme() {
        val themeMode = preferencesManager.getString(KeyConst.PREF_KEY_THEME_MODE).first()
        val theme = when (themeMode) {
            ThemeHelper.ThemeMode.LIGHT.name -> ThemeHelper.ThemeMode.LIGHT
            ThemeHelper.ThemeMode.DARK.name -> ThemeHelper.ThemeMode.DARK
            else -> ThemeHelper.ThemeMode.DEFAULT
        }
        ThemeHelper.applyTheme(theme)
        joinAll()
    }

    private fun initAd() {
        appOpenAdManager = AppOpenAdManager()
        appOpenAdManager.initOpenAd(this)
    }

    private suspend fun initExchange() {
        val exchangeState = preferencesManager.getString(KeyConst.PREF_KEY_EXCHANGE_STATE).first()
        if (exchangeState.isEmpty()) {
            GlobalState.setExchangeState(EXCHANGE_UPBIT)
        } else {
            GlobalState.setExchangeState(exchangeState)
        }
    }
}