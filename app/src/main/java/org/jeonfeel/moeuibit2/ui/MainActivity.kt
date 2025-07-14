package org.jeonfeel.moeuibit2.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
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
        checkUpdate()
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
                safeRequestUpdate(appUpdateInfo)
            }
        }.addOnFailureListener {
            this.showToast(this.getString(R.string.updateFail))
        }.addOnCanceledListener {
            this.showToast(this.getString(R.string.updateFail))
        }
    }

    private fun safeRequestUpdate(appUpdateInfo: AppUpdateInfo) {
        val dialog = CommonDialogXML(
            image = R.drawable.img_update,
            message = getString(R.string.updateDialogMessage),
            onConfirm = {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    appUpdateCallback,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            },
            onCancel = {}
        )

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                if (!supportFragmentManager.isStateSaved) {
                    dialog.show(supportFragmentManager, "update")
                }
                this.cancel() // 한 번만 실행
            }
        }
    }
}