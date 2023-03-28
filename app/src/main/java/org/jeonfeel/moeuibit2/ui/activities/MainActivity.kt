package org.jeonfeel.moeuibit2.ui.activities

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
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitOrderBookWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.ui.base.BaseActivity
import org.jeonfeel.moeuibit2.ui.main.MainBottomNavigation
import org.jeonfeel.moeuibit2.ui.main.MainNavigation
import org.jeonfeel.moeuibit2.ui.viewmodels.MainViewModel
import org.jeonfeel.moeuibit2.utils.ConnectionType
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil.Companion.currentNetworkState
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.showToast
import java.util.*
import javax.inject.Inject

const val APP_UPDATE_CODE = 123
const val APP_UPDATE_FLEXIBLE_CODE = 124

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Inject
    lateinit var localRepository: LocalRepository
    private lateinit var auth: FirebaseAuth
    private val mainViewModel: MainViewModel by viewModels()
    private val appUpdateManager by lazy {
        AppUpdateManagerFactory.create(this)
    }
    private val startForActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val resultData = it.data
                if (resultData != null) {
                    val isFavorite = resultData.getBooleanExtra(INTENT_IS_FAVORITE, false)
                    val market = resultData.getStringExtra(INTENT_MARKET) ?: ""
                    updateFavorite(market = market, isFavorite = isFavorite)
                }
            }
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
//        mainViewModel.requestUsdPrice()
        initNetworkStateMonitor(
            noInternetAction = {
                UpBitTickerWebSocket.onPause()
                UpBitTickerWebSocket.getListener().setTickerMessageListener(null)
            }
        )
        checkUpdate()
    }

    /**
     * 스토어 업데이트 확인 로직 (인앱 업데이트)
     */
    private fun checkUpdate() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            ) {
                requestUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
            }
        }.addOnFailureListener {
            this.showToast(this.getString(R.string.updateFail))
        }.addOnCanceledListener {
            this.showToast(this.getString(R.string.updateFail))
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
        Utils.getLocale()
    }

    override fun onStop() {
        super.onStop()
        networkMonitorUtil.unregister()
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.e("Call onDestroy")
        UpBitTickerWebSocket.socket.cancel()
        UpBitOrderBookWebSocket.socket.cancel()
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
            .setNegativeButton(
                this.getString(R.string.cancel)
            ) { dialog, _ -> dialog?.dismiss() }
            .show()
    }

    private fun updateFavorite(market: String, isFavorite: Boolean) {
        CoroutineScope(ioDispatcher).launch {
            when {
                MoeuiBitDataStore.favoriteHashMap[market] == null && isFavorite -> {
                    MoeuiBitDataStore.favoriteHashMap[market] = 0
                    try {
                        localRepository.getFavoriteDao().insert(market)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                MoeuiBitDataStore.favoriteHashMap[market] != null && !isFavorite -> {
                    MoeuiBitDataStore.favoriteHashMap.remove(market)
                    try {
                        localRepository.getFavoriteDao().delete(market)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}