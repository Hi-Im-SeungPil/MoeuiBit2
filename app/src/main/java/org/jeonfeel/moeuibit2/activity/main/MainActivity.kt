package org.jeonfeel.moeuibit2.activity.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Observer
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdRequest.ERROR_CODE_NETWORK_ERROR
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import org.jeonfeel.moeuibit2.constant.*
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.manager.PermissionManager
import org.jeonfeel.moeuibit2.ui.mainactivity.MainBottomNavigation
import org.jeonfeel.moeuibit2.ui.mainactivity.MainNavigation
import org.jeonfeel.moeuibit2.util.ConnectionType
import org.jeonfeel.moeuibit2.util.NetworkMonitorUtil
import org.jeonfeel.moeuibit2.util.NetworkMonitorUtil.Companion.currentNetworkState
import org.jeonfeel.moeuibit2.util.showToast
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity(), OnUserEarnedRewardListener {
    @Inject
    lateinit var networkMonitorUtil: NetworkMonitorUtil

    @Inject
    lateinit var permissionManager: PermissionManager
    private lateinit var auth: FirebaseAuth
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var startForActivityResult: ActivityResultLauncher<Intent>
    private val adRequest = AdRequest.Builder().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initActivity()
        setContent {
            MainScreen(mainViewModel)
        }
    }

    private fun initActivity() {
        auth = Firebase.auth
        if (auth.currentUser == null) {
            auth.signInAnonymously()
        }
        startForActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val resultData = it.data
                    if (resultData != null) {
                        val isFavorite = resultData.getBooleanExtra("isFavorite", false)
                        val market = resultData.getStringExtra("market") ?: ""
                        mainViewModel.updateFavorite(market, isFavorite)
                    }
                }
            }
//        permissionManager.requestPermission().let {
//            it?.check()
//        }
        initNetworkStateMonitor()
        initObserver()
    }

    private fun initObserver() {
        mainViewModel.adLiveData.observe(this, Observer {
            if (it == 1) {
                loadRewardVideoAd()
            }
        })
    }

    private fun initNetworkStateMonitor() {
        networkMonitorUtil.result = { isAvailable, type ->
            when (isAvailable) {
                true -> {
                    if (type == ConnectionType.Wifi) {
                        currentNetworkState = INTERNET_CONNECTION
                    } else if (type == ConnectionType.Cellular) {
                        currentNetworkState = INTERNET_CONNECTION
                    }
                }
                false -> {
                    if (currentNetworkState != NO_INTERNET_CONNECTION) {
                        currentNetworkState = NO_INTERNET_CONNECTION
                        mainViewModel.errorState.value = NO_INTERNET_CONNECTION
                        UpBitTickerWebSocket.onPause()
                        UpBitTickerWebSocket.getListener().setTickerMessageListener(null)
                        mainViewModel.isSocketRunning = false
                    }
                }
            }
        }
    }

    @Composable
    fun MainScreen(viewModel: MainViewModel) {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { MainBottomNavigation(navController) },
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                MainNavigation(navController, viewModel, startForActivityResult)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        networkMonitorUtil.register()
    }

    override fun onStop() {
        super.onStop()
        networkMonitorUtil.unregister()
    }

    private fun loadRewardVideoAd() {
        RewardedAd.load(
            this,
            rewardVideoAdId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    super.onAdLoaded(ad)
                    ad.show(this@MainActivity, this@MainActivity)
                    mainViewModel.adLoadingDialogState.value = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    if (error.code == ERROR_CODE_NETWORK_ERROR || error.code == 0) {
                        this@MainActivity.showToast("인터넷 상태를 확인해 주세요.")
                        mainViewModel.adLoadingDialogState.value = false
                    } else {
                        loadRewardFullScreenAd()
                    }
                }
            })

    }

    private fun loadRewardFullScreenAd() {
        MobileAds.initialize(this) {
            RewardedInterstitialAd.load(this,
                rewardFullScreenAdId, adRequest, object : RewardedInterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedInterstitialAd) {
                        super.onAdLoaded(ad)
                        ad.show(this@MainActivity, this@MainActivity)
                        mainViewModel.adLoadingDialogState.value = false
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        super.onAdFailedToLoad(error)
                        if (error.code == ERROR_CODE_NETWORK_ERROR || error.code == 0) {
                            this@MainActivity.showToast("인터넷 상태를 확인해 주세요.")
                            mainViewModel.adLoadingDialogState.value = false
                        } else {
                            loadFullScreenAd()
                        }
                    }
                })
        }
    }

    private fun loadFullScreenAd() {
        InterstitialAd.load(this, fullScreenAdId, adRequest, object : InterstitialAdLoadCallback() {

            override fun onAdLoaded(ad: InterstitialAd) {
                super.onAdLoaded(ad)
                ad.show(this@MainActivity)
                mainViewModel.adLoadingDialogState.value = false
                mainViewModel.earnReward()
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                super.onAdFailedToLoad(error)
                if (error.code == ERROR_CODE_NETWORK_ERROR || error.code == 0) {
                    this@MainActivity.showToast("인터넷 상태를 확인해 주세요.")
                    mainViewModel.adLoadingDialogState.value = false
                } else {
                    mainViewModel.errorReward()
                    this@MainActivity.showToast("광고 로드 오류로 1,000,000 KRW가 지급됩니다.")
                    mainViewModel.adLoadingDialogState.value = false
                }
            }
        })
    }

    override fun onUserEarnedReward(p0: RewardItem) {
        mainViewModel.earnReward()
    }
}