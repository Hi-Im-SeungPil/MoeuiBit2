package org.jeonfeel.moeuibit2.ui.coindetail.newS

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import org.jeonfeel.moeuibit2.ui.coindetail.newScreen.NewCoinDetailViewModel

@Composable
fun CoinDetailScreenRoute(
    market: String,
    warning: Boolean
) {
    NewCoinDetailScreen(
        market = market,
        warning = warning
    )
}