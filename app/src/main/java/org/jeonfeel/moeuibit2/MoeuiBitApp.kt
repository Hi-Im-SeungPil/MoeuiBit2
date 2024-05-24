package org.jeonfeel.moeuibit2

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log.isLoggable
import androidx.lifecycle.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.BuildConfig
import com.orhanobut.logger.Logger
import dagger.hilt.android.HiltAndroidApp
import org.jeonfeel.moeuibit2.constants.AD_ID_TEST
import org.jeonfeel.moeuibit2.constants.PREF_KEY_THEME_MODE
import org.jeonfeel.moeuibit2.ui.theme.ThemeHelper
import org.jeonfeel.moeuibit2.utils.manager.AppOpenAdManager
import org.jeonfeel.moeuibit2.utils.manager.PreferenceManager
import java.util.*
import javax.inject.Inject

@HiltAndroidApp
class MoeuiBitApp : Application() {

    @Inject
    lateinit var prefrenceManager: PreferenceManager
    private lateinit var appOpenAdManager: AppOpenAdManager

    override fun onCreate() {
        super.onCreate()
        mBitApplication = this
        applyTheme()
        initAd()

        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
    }

    private fun applyTheme() {
        val theme = when (prefrenceManager.getString(PREF_KEY_THEME_MODE) ?: "") {
            ThemeHelper.ThemeMode.LIGHT.name -> ThemeHelper.ThemeMode.LIGHT
            ThemeHelper.ThemeMode.DARK.name -> ThemeHelper.ThemeMode.DARK
            else -> ThemeHelper.ThemeMode.DEFAULT
        }
        ThemeHelper.applyTheme(theme)
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