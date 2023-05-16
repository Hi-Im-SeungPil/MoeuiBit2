package org.jeonfeel.moeuibit2.utils.manager

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.MoeuiBitApp
import org.jeonfeel.moeuibit2.constants.AD_ID_OPENING
import org.jeonfeel.moeuibit2.constants.AD_ID_TEST
import java.util.*

interface OnShowAdCompleteListener {
    fun onShowAdComplete()
}

class AppOpenAdManager : Application.ActivityLifecycleCallbacks, LifecycleEventObserver {
    private var currentActivity: Activity? = null
    private var loadTime = 0L

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false

    fun initOpenAd(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
        MobileAds.initialize(application.applicationContext) {}
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun loadAd(context: Context) {
        if (isLoadingAd || isAdAvailable()) {
            return
        }

        isLoadingAd = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            AD_ID_OPENING,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    Logger.e(loadAdError.message)
                }
            }
        )
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    private fun showAdIfAvailable(
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


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        if (!isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(200L)
                    currentActivity?.let {
                        showAdIfAvailable(it)
                    }
                }
            }
            else -> {
            }
        }
    }
}