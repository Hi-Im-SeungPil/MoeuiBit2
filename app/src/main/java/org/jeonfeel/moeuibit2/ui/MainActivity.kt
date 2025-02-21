package org.jeonfeel.moeuibit2.ui

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.nav.AppNavGraph
import org.jeonfeel.moeuibit2.ui.theme.AppTheme
import org.jeonfeel.moeuibit2.utils.ext.showToast

const val APP_UPDATE_CODE = 123
const val APP_UPDATE_FLEXIBLE_CODE = 124

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val appUpdateManager by lazy {
        AppUpdateManagerFactory.create(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                AppNavGraph()
            }
        }
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
            .setNegativeButton(
                this.getString(R.string.cancel)
            ) { dialog, _ -> dialog?.dismiss() }
            .show()
    }
}