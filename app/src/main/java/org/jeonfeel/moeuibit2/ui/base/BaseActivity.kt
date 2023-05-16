package org.jeonfeel.moeuibit2.ui.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import org.jeonfeel.moeuibit2.constants.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.constants.NO_INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.utils.ConnectionType
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var networkMonitorUtil: NetworkMonitorUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    /**
     * 네트워크 연결상태 모니터링
     */
    protected fun initNetworkStateMonitor(
        connectedWifiAction: (() -> Unit)? = null,
        connected5G: (() -> Unit)? = null,
        noInternetAction: (() -> Unit)? = null
    ) {
        networkMonitorUtil.result = { isAvailable, type ->
            when (isAvailable) {
                true -> {
                    if (type == ConnectionType.Wifi) {
                        NetworkMonitorUtil.currentNetworkState = INTERNET_CONNECTION
                        connectedWifiAction?.let { it() }
                    } else if (type == ConnectionType.Cellular) {
                        NetworkMonitorUtil.currentNetworkState = INTERNET_CONNECTION
                        connected5G?.let { it() }
                    }
                }
                false -> {
                    if (NetworkMonitorUtil.currentNetworkState != NO_INTERNET_CONNECTION) {
                        NetworkMonitorUtil.currentNetworkState = NO_INTERNET_CONNECTION
                        noInternetAction?.let { it() }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            networkMonitorUtil.register()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            networkMonitorUtil.unregister()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}