package org.jeonfeel.moeuibit2.view.activity.coindetail

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import org.jeonfeel.moeuibit2.ui.coindetail.CoinDetailScreen
import org.jeonfeel.moeuibit2.viewmodel.CoinDetailViewModel


class CoinDetailActivity : AppCompatActivity() {

    lateinit var coinKoreanName: String
    lateinit var coinSymbol: String
    val coinDetailViewModel : CoinDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initActivity()

        setContent {
            CoinDetailActivityScreen()
        }
    }

    private fun initActivity() {
        coinKoreanName = intent.getStringExtra("coinKoreanName") ?: ""
        coinSymbol = intent.getStringExtra("coinSymbol") ?: ""
        coinDetailViewModel.requestCoinTicker("KRW-".plus(coinSymbol))
    }

    @Composable
    fun CoinDetailActivityScreen() {
        CoinDetailScreen(coinKoreanName, coinSymbol,coinDetailViewModel)
    }
}