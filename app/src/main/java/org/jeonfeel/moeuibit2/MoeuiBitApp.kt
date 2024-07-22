package org.jeonfeel.moeuibit2

import android.app.Application
import android.content.Context
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.constants.KeyConst
import org.jeonfeel.moeuibit2.ui.theme.ThemeHelper
import org.jeonfeel.moeuibit2.utils.manager.AppOpenAdManager
import org.jeonfeel.moeuibit2.utils.manager.PreferencesManager
import javax.inject.Inject

@HiltAndroidApp
class MoeuiBitApp : Application() {

    @Inject
    lateinit var preferencesManager: PreferencesManager
    private lateinit var appOpenAdManager: AppOpenAdManager

    override fun onCreate() {
        super.onCreate()
        mBitApplication = this
        applyTheme()
        initAd()

        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
//                return BuildConfig.DEBUG
                return true
            }
        })
    }

    private fun applyTheme() {
        CoroutineScope(Dispatchers.Main).launch {
            preferencesManager.getString(KeyConst.PREF_KEY_THEME_MODE).collect { themeMode ->
                val theme = when (themeMode) {
                    ThemeHelper.ThemeMode.LIGHT.name -> ThemeHelper.ThemeMode.LIGHT
                    ThemeHelper.ThemeMode.DARK.name -> ThemeHelper.ThemeMode.DARK
                    else -> ThemeHelper.ThemeMode.DEFAULT
                }
                ThemeHelper.applyTheme(theme)
            }
        }
    }

    private fun initAd() {
        appOpenAdManager = AppOpenAdManager()
        appOpenAdManager.initOpenAd(this)
    }

    companion object {
        lateinit var mBitApplication: MoeuiBitApp
        fun mBitApplicationContext(): Context? {
            return mBitApplication.applicationContext
        }
    }
}