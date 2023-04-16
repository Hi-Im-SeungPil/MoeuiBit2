package org.jeonfeel.moeuibit2

import android.app.Application
import android.content.Context
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import dagger.hilt.android.HiltAndroidApp
import org.jeonfeel.moeuibit2.ui.theme.ThemeHelper
import org.jeonfeel.moeuibit2.utils.manager.PreferenceManager
import javax.inject.Inject

@HiltAndroidApp
class MoeuiBitApp: Application() {

    @Inject
    lateinit var prefrenceManager: PreferenceManager

    override fun onCreate() {
        super.onCreate()
        val theme = when (prefrenceManager.getString("themeMode") ?: "") {
            "라이트 모드" -> ThemeHelper.ThemeMode.LIGHT
            "다크 모드" -> ThemeHelper.ThemeMode.DARK
            else -> ThemeHelper.ThemeMode.DEFAULT
        }
        ThemeHelper.applyTheme(theme)
        mBitApplication = this
        Logger.addLogAdapter(AndroidLogAdapter())
    }

    companion object {
        lateinit var mBitApplication: MoeuiBitApp
        fun mBitApplicationContext(): Context? {
            return mBitApplication.applicationContext
        }
    }
}