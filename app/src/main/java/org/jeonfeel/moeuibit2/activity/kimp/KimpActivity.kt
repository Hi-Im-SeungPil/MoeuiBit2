package org.jeonfeel.moeuibit2.activity.kimp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import org.jeonfeel.moeuibit2.activity.kimp.viewmodel.KimpViewModel
import org.jeonfeel.moeuibit2.constant.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.constant.NO_INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.ui.kimp.KimpScreen
import org.jeonfeel.moeuibit2.util.ConnectionType
import org.jeonfeel.moeuibit2.util.NetworkMonitorUtil

@AndroidEntryPoint
class KimpActivity : ComponentActivity() {

    private val networkMonitorUtil = NetworkMonitorUtil(this);
    private val kimpViewModel: KimpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            kimpScreen(kimpViewModel)
        }
        initActivity()
    }

    private fun initActivity() {
        initNetworkStateMonitor()
    }

    private fun initNetworkStateMonitor() {
        networkMonitorUtil.result = { isAvailable, type ->
            when (isAvailable) {
                true -> {
                    if (type == ConnectionType.Wifi) {
                        NetworkMonitorUtil.currentNetworkState = INTERNET_CONNECTION
                    } else if (type == ConnectionType.Cellular) {
                        NetworkMonitorUtil.currentNetworkState = INTERNET_CONNECTION
                    }
                }
                false -> {
                    if (NetworkMonitorUtil.currentNetworkState != NO_INTERNET_CONNECTION) {

                    }
                }
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

    @Composable
    fun kimpScreen(viewModel: KimpViewModel) {
        KimpScreen(kimpViewModel)
    }
}