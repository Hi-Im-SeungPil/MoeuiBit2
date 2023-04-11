package org.jeonfeel.moeuibit2

import android.app.Application
import android.content.Context
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import dagger.hilt.android.HiltAndroidApp
import org.jeonfeel.moeuibit2.ui.theme.ThemeHelper

@HiltAndroidApp
class MoeuiBitApp: Application() {
    override fun onCreate() {
        super.onCreate()
        ThemeHelper.applyTheme(ThemeHelper.ThemeMode.DEFAULT)
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