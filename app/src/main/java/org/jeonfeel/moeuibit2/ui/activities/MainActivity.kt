package org.jeonfeel.moeuibit2.ui.activities

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableIntStateOf
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.constants.KeyConst
import org.jeonfeel.moeuibit2.constants.NO_INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.data.network.websocket.bitthumb.BitthumbTickerWebSocket
import org.jeonfeel.moeuibit2.data.network.websocket.upbit.UpBitOrderBookWebSocket
import org.jeonfeel.moeuibit2.data.network.websocket.upbit.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.ui.MoeuiBitApp
import org.jeonfeel.moeuibit2.ui.NavGraph
import org.jeonfeel.moeuibit2.ui.base.BaseActivity
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.ui.theme.MainTheme
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil.Companion.currentNetworkState
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.manager.PreferencesManager
import org.jeonfeel.moeuibit2.utils.showToast
import javax.inject.Inject

const val APP_UPDATE_CODE = 123
const val APP_UPDATE_FLEXIBLE_CODE = 124

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Inject
    lateinit var localRepository: LocalRepository

    @Inject
    lateinit var preferenceManager: PreferencesManager

    private val appUpdateManager by lazy {
        AppUpdateManagerFactory.create(this)
    }
    private val networkErrorState by lazy {
        mutableIntStateOf(INTERNET_CONNECTION)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivity()
        setContent {
            val navController = rememberNavController()
            MainTheme(isMainActivity = true, content = {
                NavGraph(navController = navController, networkErrorState = networkErrorState)
//                MoeuiBitApp(networkErrorState)
            })
        }
    }

    /**
     * 초기화
     */
    private fun initActivity() {
        initNetworkStateMonitor(
            connected5G = {
                currentNetworkState = INTERNET_CONNECTION
            },
            connectedWifiAction = {
                currentNetworkState = INTERNET_CONNECTION
            },
            noInternetAction = {
                networkErrorState.value = NO_INTERNET_CONNECTION
                CoroutineScope(Dispatchers.Main).launch {
                    preferenceManager.getString(KeyConst.PREF_KEY_ROOT_EXCHANGE)
                        .collect { currentRootExchange ->
                            if (currentRootExchange == ROOT_EXCHANGE_UPBIT) {
                                UpBitTickerWebSocket.onPause()
                            } else {
                                BitthumbTickerWebSocket.onPause()
                            }
                        }
                }
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
            Logger.e(it.message.toString())
            this.showToast(this.getString(R.string.updateFail))
        }.addOnCanceledListener {
            this.showToast(this.getString(R.string.updateFail))
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                requestUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
            }
        }
        Utils.getLocale()
    }

    override fun onDestroy() {
        super.onDestroy()
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
}