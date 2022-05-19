package org.jeonfeel.moeuibit2.view.activity.coindetail

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.core.view.WindowInsetsControllerCompat
import dagger.hilt.android.AndroidEntryPoint
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.coindetail.CoinDetailScreen
import org.jeonfeel.moeuibit2.viewmodel.coindetail.CoinDetailViewModel

@AndroidEntryPoint
class CoinDetailActivity : AppCompatActivity() {

    lateinit var coinKoreanName: String
    private lateinit var coinSymbol: String
    private var openingPrice: Double = 0.0
    private var isFavorite = false
    private var koreanName = ""
    private val coinDetailViewModel: CoinDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initActivity()
        setContent {
            CoinDetailActivityScreen()
        }
    }

    private fun initActivity() {
        window.statusBarColor = this.getColor(R.color.C0F0F5C)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
            false
        coinKoreanName = intent.getStringExtra("coinKoreanName") ?: ""
        coinSymbol = intent.getStringExtra("coinSymbol") ?: ""
        openingPrice = intent.getDoubleExtra("openingPrice", 0.0)
        isFavorite = intent.getBooleanExtra("isFavorite", false)
        coinDetailViewModel.initViewModel("KRW-".plus(coinSymbol), openingPrice, isFavorite)
    }

    @Composable
    fun CoinDetailActivityScreen() {
        CoinDetailScreen(coinKoreanName, coinSymbol, coinDetailViewModel)
    }
}