package org.jeonfeel.moeuibit2.ui.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.core.view.WindowInsetsControllerCompat
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.coindetail.CoinDetailViewModel
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.ui.base.BaseActivity
import org.jeonfeel.moeuibit2.ui.coindetail.CoinDetailScreen
import org.jeonfeel.moeuibit2.ui.theme.MainTheme
import org.jeonfeel.moeuibit2.utils.showToast

@AndroidEntryPoint
class CoinDetailActivity : BaseActivity() {

    private var coinKoreanName = ""
    private var coinEngName = ""
    private var openingPrice: Double = 0.0
    private var isFavorite = false
    private var warning = ""
    private var marketState = -999
    private var rootExchange = ""
    private lateinit var coinSymbol: String
    private val coinDetailViewModel: CoinDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Logger.d("onCreate")
        initActivity()
        setContent {
            MainTheme(isMainActivity = false, content = {
                CoinDetailActivityScreen()
            })
        }
    }

    /**
     * 초기화
     */
    private fun initActivity() {
        window.statusBarColor = this.getColor(R.color.C0F0F5C)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        intent?.let {
            coinKoreanName = it.getStringExtra(KeyConst.INTENT_KOREAN_NAME) ?: ""
            coinEngName = it.getStringExtra(KeyConst.INTENT_ENG_NAME) ?: ""
            coinSymbol = it.getStringExtra(KeyConst.INTENT_COIN_SYMBOL) ?: ""
            openingPrice = it.getDoubleExtra(KeyConst.INTENT_OPENING_PRICE, 0.0)
            isFavorite = it.getBooleanExtra(KeyConst.INTENT_IS_FAVORITE, false)
            warning = it.getStringExtra(KeyConst.INTENT_WARNING) ?: ""
            marketState = it.getIntExtra(KeyConst.INTENT_MARKET_STATE, -999)
            rootExchange = it.getStringExtra(KeyConst.INTENT_ROOT_EXCHANGE) ?: ""
        }

        val market = if (marketState == SELECTED_BTC_MARKET) {
            "BTC-"
        } else {
            "KRW-"
        }

        coinDetailViewModel.initViewModel(
            market.plus(coinSymbol),
            openingPrice,
            isFavorite,
            rootExchange
        )
        initNetworkStateMonitor(
            noInternetAction = {
                coinDetailViewModel.chart.state.isUpdateChart.value = false
                this.showToast(this.getString(R.string.NO_INTERNET_CONNECTION))
            }
        )
    }

    @Composable
    fun CoinDetailActivityScreen() {
//        if (isKor) {
        CoinDetailScreen(coinKoreanName, coinSymbol, warning, coinDetailViewModel)
//        } else {
//            CoinDetailScreen(coinEngName, coinSymbol, warning, coinDetailViewModel)
//        }
    }
}