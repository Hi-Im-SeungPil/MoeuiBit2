package org.jeonfeel.moeuibit2.activity.coindetail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.core.view.WindowInsetsControllerCompat
import dagger.hilt.android.AndroidEntryPoint
import org.jeonfeel.moeuibit2.BuildConfig
import org.jeonfeel.moeuibit2.constant.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.constant.NO_INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.coindetail.CoinDetailScreen
import org.jeonfeel.moeuibit2.util.ConnectionType
import org.jeonfeel.moeuibit2.util.NetworkMonitorUtil
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.CoinDetailViewModel
import org.jeonfeel.moeuibit2.util.showToast

@AndroidEntryPoint
class CoinDetailActivity : ComponentActivity() {

    private var coinKoreanName = ""
    private var openingPrice: Double = 0.0
    private var isFavorite = false
    private var warning = ""
    private val networkMonitorUtil = NetworkMonitorUtil(this)
    private lateinit var coinSymbol: String
    private val coinDetailViewModel: CoinDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initActivity()
        setContent {
            CoinDetailActivityScreen()
        }
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
                        NetworkMonitorUtil.currentNetworkState = NO_INTERNET_CONNECTION
                        coinDetailViewModel.isUpdateChart = false
                        this.showToast(this.getString(R.string.NO_INTERNET_CONNECTION))
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

    private fun initActivity() {
        window.statusBarColor = this.getColor(R.color.C0F0F5C)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        intent?.let {
            coinKoreanName = it.getStringExtra("coinKoreanName") ?: ""
            coinSymbol = it.getStringExtra("coinSymbol") ?: ""
            openingPrice = it.getDoubleExtra("openingPrice", 0.0)
            isFavorite = it.getBooleanExtra("isFavorite", false)
            warning = it.getStringExtra("warning") ?: ""
        }
        coinDetailViewModel.initViewModel("KRW-".plus(coinSymbol), openingPrice, isFavorite)
        initNetworkStateMonitor()
    }

    @Composable
    fun CoinDetailActivityScreen() {
        CoinDetailScreen(coinKoreanName, coinSymbol, warning, coinDetailViewModel)
    }
}