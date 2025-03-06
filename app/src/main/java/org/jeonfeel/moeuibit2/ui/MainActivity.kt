package org.jeonfeel.moeuibit2.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.common.CommonDialogXML
import org.jeonfeel.moeuibit2.ui.nav.AppNavGraph
import org.jeonfeel.moeuibit2.ui.theme.AppTheme
import org.jeonfeel.moeuibit2.utils.NetworkConnectivityObserver
import org.jeonfeel.moeuibit2.utils.NetworkObserverLifecycle
import org.jeonfeel.moeuibit2.utils.ext.showToast

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val appUpdateManager by lazy {
        AppUpdateManagerFactory.create(this)
    }
    private lateinit var networkConnectivityObserver: NetworkConnectivityObserver

    private val appUpdateCallback =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                this.showToast("앱 업데이트가 완료 되었습니다.")
            } else {
                this.showToast(this.getString(R.string.updateFail))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                AppNavGraph()
            }
        }
        init()
    }

    fun init() {
//        checkUpdate()
//        requestUpdate()
        initData()
    }

    private fun initData() {
        networkConnectivityObserver =
            NetworkConnectivityObserver(this@MainActivity)
        lifecycle.addObserver(NetworkObserverLifecycle(networkConnectivityObserver))
    }

    /**
     * 스토어 업데이트 확인 로직 (인앱 업데이트)
     */
    private fun checkUpdate() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            ) {
//                requestUpdate(appUpdateInfo)
            }
        }.addOnFailureListener {
            this.showToast(this.getString(R.string.updateFail))
        }.addOnCanceledListener {
            this.showToast(this.getString(R.string.updateFail))
        }
    }

    override fun onResume() {
        super.onResume()
//        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
//                requestUpdate(appUpdateInfo)
//            }
//        }
    }

    private fun requestUpdate(
//        appUpdateInfo: AppUpdateInfo,
    ) {
        CommonDialogXML(
            image = R.drawable.img_update,
            message = this.getString(R.string.updateDialogMessage),
            onConfirm = {
//                appUpdateManager.startUpdateFlowForResult(
//                    appUpdateInfo,
//                    appUpdateCallback,
//                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
//                )
            },
            onCancel = { }
        ).show(supportFragmentManager, "updateDialog")
    }
}