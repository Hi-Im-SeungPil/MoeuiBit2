package org.jeonfeel.moeuibit2.ui.activities

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.constants.NO_INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitOrderBookWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.ui.base.BaseActivity
import org.jeonfeel.moeuibit2.ui.main.MainBottomNavigation
import org.jeonfeel.moeuibit2.ui.main.MainNavigation
import org.jeonfeel.moeuibit2.ui.theme.MainTheme
import org.jeonfeel.moeuibit2.ui.theme.ThemeHelper
import org.jeonfeel.moeuibit2.ui.viewmodels.MainViewModel
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil.Companion.currentNetworkState
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.manager.PreferenceManager
import org.jeonfeel.moeuibit2.utils.showToast
import javax.inject.Inject

const val APP_UPDATE_CODE = 123
const val APP_UPDATE_FLEXIBLE_CODE = 124

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Inject
    lateinit var localRepository: LocalRepository

    @Inject
    lateinit var preferenceManager: PreferenceManager
    private lateinit var auth: FirebaseAuth
    private val mainViewModel: MainViewModel by viewModels()
    private val appUpdateManager by lazy {
        AppUpdateManagerFactory.create(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initActivity()
        setContent {
            MainTheme(isMainActivity = true, content = {
                MainScreen(mainViewModel)
            })
        }
    }

    /**
     * 초기화
     */
    private fun initActivity() {
        val theme = when (preferenceManager.getString("themeMode") ?: "") {
            "라이트 모드" -> ThemeHelper.ThemeMode.LIGHT
            "다크 모드" -> ThemeHelper.ThemeMode.DARK
            else -> ThemeHelper.ThemeMode.DEFAULT
        }
//        ThemeHelper.applyTheme(ThemeHelper.ThemeMode.DARK)
//        ThemeHelper.applyTheme(theme)
        auth = Firebase.auth
        if (auth.currentUser == null) {
            auth.signInAnonymously()
        }
//        mainViewModel.requestUsdPrice()
        initNetworkStateMonitor(
            connected5G = {
                currentNetworkState = INTERNET_CONNECTION
            },
            connectedWifiAction = {
                currentNetworkState = INTERNET_CONNECTION
            },
            noInternetAction = {
                mainViewModel.state.errorState.value = NO_INTERNET_CONNECTION
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
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) { contentPadding ->
            Box(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                MainNavigation(navController, viewModel)
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