package org.jeonfeel.moeuibit2.activity.main

import android.app.AlertDialog
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
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.main.viewmodel.MainViewModel
import org.jeonfeel.moeuibit2.constant.INTENT_IS_FAVORITE
import org.jeonfeel.moeuibit2.constant.INTENT_MARKET
import org.jeonfeel.moeuibit2.constant.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.constant.NO_INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.manager.AdMobManager
import org.jeonfeel.moeuibit2.ui.mainactivity.MainBottomNavigation
import org.jeonfeel.moeuibit2.ui.mainactivity.MainNavigation
import org.jeonfeel.moeuibit2.util.ConnectionType
import org.jeonfeel.moeuibit2.util.NetworkMonitorUtil
import org.jeonfeel.moeuibit2.util.NetworkMonitorUtil.Companion.currentNetworkState
import org.jeonfeel.moeuibit2.util.showToast
import javax.inject.Inject

const val APP_UPDATE_CODE = 123
const val APP_UPDATE_FLEXIBLE_CODE = 124

@AndroidEntryPoint
class MainActivity : ComponentActivity(), OnUserEarnedRewardListener {
    @Inject
    lateinit var networkMonitorUtil: NetworkMonitorUtil

    @Inject
    lateinit var adMobManager: AdMobManager
    private lateinit var auth: FirebaseAuth
    private lateinit var startForActivityResult: ActivityResultLauncher<Intent>
    private val mainViewModel: MainViewModel by viewModels()
    private val appUpdateManager by lazy {
        AppUpdateManagerFactory.create(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initActivity()
        setContent {
            MainScreen(mainViewModel)
        }
    }

    /**
     * 초기화
     */
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
                        val isFavorite = resultData.getBooleanExtra(INTENT_IS_FAVORITE, false)
                        val market = resultData.getStringExtra(INTENT_MARKET) ?: ""
                        mainViewModel.updateFavorite(market, isFavorite)
                    }
                }
            }
        initNetworkStateMonitor()
        initObserver()
        checkUpdate()
    }

    /**
     * 스토어 업데이트 확인 로직 (인앱 업데이트)
     */
    private fun checkUpdate() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                requestUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                requestUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
            }
        }.addOnFailureListener {
            this.showToast(this.getString(R.string.updateFail))
        }.addOnCanceledListener {
            this.showToast(this.getString(R.string.updateFail))
        }
    }

    /**
     * 광고 옵저버
     */
    private fun initObserver() {
        mainViewModel.adLiveData.observe(this) {
            if (it == 1) {
                adMobManager.loadRewardVideoAd(
                    onAdLoaded = {
                        mainViewModel.adLoadingDialogState.value = false
                    },
                    onAdFailedToLoad = {
                        this@MainActivity.showToast(this.getString(R.string.NO_INTERNET_CONNECTION))
                        mainViewModel.adLoadingDialogState.value = false
                    },
                    this@MainActivity,
                    fullScreenOnAdLoad = {
                        mainViewModel.adLoadingDialogState.value = false
                        mainViewModel.earnReward()
                    },
                    fullScreenOnAdFailedToLoad = {
                        this.showToast(this.getString(R.string.adLoadError))
                        mainViewModel.errorReward()
                        mainViewModel.adLoadingDialogState.value = false
                    }
                )
            }
        }
    }

    /**
     * 네트워크 연결상태 모니터링
     */
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
                        mainViewModel.updateExchange = false
                    }
                }
            }
        }
    }

    @Composable
    fun MainScreen(viewModel: MainViewModel) {
        val navController = rememberNavController()
        val scaffoldState = rememberScaffoldState()
        Scaffold(
            scaffoldState = scaffoldState,
            bottomBar = { MainBottomNavigation(navController) },
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                MainNavigation(navController, viewModel, startForActivityResult, scaffoldState)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        networkMonitorUtil.register()
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                requestUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        networkMonitorUtil.unregister()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun requestUpdate(appUpdateInfo: AppUpdateInfo, appUpdateType: Int) {
        AlertDialog.Builder(this)
            .setTitle(this.getString(R.string.updateDialogTitle))
            .setMessage(this.getString(R.string.updateDialogMessage))
            .setPositiveButton(this.getString(R.string.confirm)) { dialog, _ ->
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    appUpdateType,
                    this@MainActivity,
                    APP_UPDATE_CODE
                )
                dialog?.dismiss()
            }
            .setNegativeButton(this.getString(R.string.cancel)
            ) { dialog, _ -> dialog?.dismiss() }
            .show()


    }

    override fun onUserEarnedReward(p0: RewardItem) {
        mainViewModel.earnReward()
    }
}