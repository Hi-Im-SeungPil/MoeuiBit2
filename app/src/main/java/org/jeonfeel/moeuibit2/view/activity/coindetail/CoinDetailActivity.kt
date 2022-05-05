package org.jeonfeel.moeuibit2.view.activity.coindetail

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import org.jeonfeel.moeuibit2.ui.coindetail.CoinDetailScreen
import org.jeonfeel.moeuibit2.viewmodel.CoinDetailViewModel

@AndroidEntryPoint
class CoinDetailActivity : AppCompatActivity() {

    lateinit var coinKoreanName: String
    private lateinit var coinSymbol: String
    private var openingPrice: Double = 0.0
    private var isFavorite = false
    private val coinDetailViewModel: CoinDetailViewModel by viewModels()

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
        openingPrice = intent.getDoubleExtra("openingPrice", 0.0)
        isFavorite = intent.getBooleanExtra("isFavorite", false)
        coinDetailViewModel.initOrder("KRW-".plus(coinSymbol), openingPrice, isFavorite)
    }

    @Composable
    fun CoinDetailActivityScreen() {
        CoinDetailScreen(coinKoreanName, coinSymbol, coinDetailViewModel)
    }
}