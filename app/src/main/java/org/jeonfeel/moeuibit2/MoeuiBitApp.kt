package org.jeonfeel.moeuibit2

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import dagger.hilt.android.HiltAndroidApp
import org.jeonfeel.moeuibit2.constants.AD_ID_TEST
import org.jeonfeel.moeuibit2.ui.theme.ThemeHelper
import org.jeonfeel.moeuibit2.utils.manager.PreferenceManager
import java.util.*
import javax.inject.Inject

@HiltAndroidApp
class MoeuiBitApp : Application(), Application.ActivityLifecycleCallbacks, LifecycleObserver {

    @Inject
    lateinit var prefrenceManager: PreferenceManager
    private lateinit var appOpenAdManager: AppOpenAdManager
    private var currentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        val theme = when (prefrenceManager.getString("themeMode") ?: "") {
            "라이트 모드" -> ThemeHelper.ThemeMode.LIGHT
            "다크 모드" -> ThemeHelper.ThemeMode.DARK
            else -> ThemeHelper.ThemeMode.DEFAULT
        }
        ThemeHelper.applyTheme(theme)
        registerActivityLifecycleCallbacks(this)
        MobileAds.initialize(this) {}
        appOpenAdManager = AppOpenAdManager()
        mBitApplication = this
        Logger.addLogAdapter(AndroidLogAdapter())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        currentActivity?.let {
            appOpenAdManager.showAdIfAvailable(it)
        }
    }

    companion object {
        lateinit var mBitApplication: MoeuiBitApp
        fun mBitApplicationContext(): Context? {
            return mBitApplication.applicationContext
        }
    }

    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }

    private inner class AppOpenAdManager {
        private var loadTime = 0L

        private var appOpenAd: AppOpenAd? = null
        private var isLoadingAd = false
        var isShowingAd = false

        fun loadAd(context: Context) {
            if (isLoadingAd || isAdAvailable()) {
                return
            }

            isLoadingAd = true
            val request = AdRequest.Builder().build()
            AppOpenAd.load(
                context,
                AD_ID_TEST,
                request,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(ad: AppOpenAd) {
                        appOpenAd = ad
                        isLoadingAd = false
                        loadTime = Date().time
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        isLoadingAd = false
                    }
                }
            )
        }

        private fun isAdAvailable(): Boolean {
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
        }

        fun showAdIfAvailable(
            activity: Activity,
            onShowAdCompleteListener: OnShowAdCompleteListener = object : OnShowAdCompleteListener {
                override fun onShowAdComplete() {
                }
            }
        ) {
            if (isShowingAd) {
                Logger.d("The app open ad is already showing.")
                return
            }

            if (!isAdAvailable()) {
                Logger.d("The app open ad is not ready yet.")
                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity)
                return
            }

            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

                override fun onAdDismissedFullScreenContent() {
                    Logger.d("Ad dismissed fullscreen content.")
                    appOpenAd = null
                    isShowingAd = false

                    onShowAdCompleteListener.onShowAdComplete()
                    loadAd(activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Logger.d(adError.message)
                    appOpenAd = null
                    isShowingAd = false

                    onShowAdCompleteListener.onShowAdComplete()
                    loadAd(activity)
                }

                override fun onAdShowedFullScreenContent() {
                    Logger.d("Ad Showed fullScreen content.")
                }
            }
            isShowingAd = true
            appOpenAd?.show(activity)
        }

        private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
            val dateDifference: Long = Date().time - loadTime
            val numMilliSecondsPerHour: Long = 3600000
            return dateDifference < numMilliSecondsPerHour * numHours
        }
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(p0: Activity) {
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }
}