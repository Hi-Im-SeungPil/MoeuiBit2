package org.jeonfeel.moeuibit2.manager

import android.app.Activity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import org.jeonfeel.moeuibit2.constant.fullScreenAdId
import org.jeonfeel.moeuibit2.constant.rewardFullScreenAdId
import org.jeonfeel.moeuibit2.constant.rewardVideoAdId

class AdMobManager(private val activity: Activity) {

    private val adRequest = AdRequest.Builder().build()

    fun loadRewardVideoAd(
        onAdLoaded: () -> Unit,
        onAdFailedToLoad: () -> Unit,
        rewardListener: OnUserEarnedRewardListener,
        fullScreenOnAdLoad: () -> Unit,
        fullScreenOnAdFailedToLoad: () -> Unit
    ) {
        RewardedAd.load(
            activity,
            rewardVideoAdId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    super.onAdLoaded(ad)
                    ad.show(activity, rewardListener)
                    onAdLoaded()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    if (error.code == AdRequest.ERROR_CODE_NETWORK_ERROR || error.code == 0) {
                        onAdFailedToLoad()
                    } else loadRewardFullScreenAd(
                        onAdLoaded = onAdLoaded,
                        onAdFailedToLoad = onAdFailedToLoad,
                        rewardListener = rewardListener,
                        fullScreenOnAdLoad = fullScreenOnAdLoad,
                        fullScreenOnAdFailedToLoad = fullScreenOnAdFailedToLoad
                    )
                }
            })
    }

    private fun loadRewardFullScreenAd(
        onAdLoaded: () -> Unit,
        onAdFailedToLoad: () -> Unit,
        rewardListener: OnUserEarnedRewardListener,
        fullScreenOnAdLoad: () -> Unit,
        fullScreenOnAdFailedToLoad: () -> Unit
    ) {
        MobileAds.initialize(activity) {
            RewardedInterstitialAd.load(activity,
                rewardFullScreenAdId, adRequest, object : RewardedInterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedInterstitialAd) {
                        super.onAdLoaded(ad)
                        ad.show(activity, rewardListener)
                        onAdLoaded()
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        super.onAdFailedToLoad(error)
                        if (error.code == AdRequest.ERROR_CODE_NETWORK_ERROR || error.code == 0) {
                            onAdFailedToLoad()
                        } else {
                            loadFullScreenAd(
                                fullScreenOnAdLoad,
                                fullScreenOnAdFailedToLoad,
                                onAdFailedToLoad
                            )
                        }
                    }
                })
        }
    }

    private fun loadFullScreenAd(
        onAdLoaded: () -> Unit,
        onAdFailedToLoad: () -> Unit,
        networkErrorOnAdFailedToLoad: () -> Unit
    ) {
        InterstitialAd.load(
            activity,
            fullScreenAdId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    super.onAdLoaded(ad)
                    ad.show(activity)
                    onAdLoaded()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    if (error.code == AdRequest.ERROR_CODE_NETWORK_ERROR || error.code == 0) {
                        networkErrorOnAdFailedToLoad()
                    } else {
                        onAdFailedToLoad()
                    }
                }
            })
    }
}