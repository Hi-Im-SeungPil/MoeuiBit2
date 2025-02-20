package org.jeonfeel.moeuibit2

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.constants.KeyConst
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import org.jeonfeel.moeuibit2.ui.theme.ThemeHelper
import org.jeonfeel.moeuibit2.utils.manager.AppOpenAdManager
import javax.inject.Inject

@HiltAndroidApp
class MoeuiBitApp : Application() {

    @Inject
    lateinit var preferencesManager: PreferencesManager
    private lateinit var appOpenAdManager: AppOpenAdManager

    override fun onCreate() {
        super.onCreate()
        mBitApplication = this
        ThemeHelper.applyTheme(ThemeHelper.ThemeMode.LIGHT)
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
            val themeMode = preferencesManager.getString(KeyConst.PREF_KEY_THEME_MODE).first()
            when (themeMode) {
                ThemeHelper.ThemeMode.LIGHT.name -> ThemeHelper.ThemeMode.LIGHT
                ThemeHelper.ThemeMode.DARK.name -> ThemeHelper.ThemeMode.DARK
                else -> ThemeHelper.ThemeMode.DEFAULT
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