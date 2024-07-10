package org.jeonfeel.moeuibit2.ui.coindetail.newScreen.order

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import java.math.BigDecimal

@Composable
fun OrderScreenRoute(
    viewModel: NewCoinDetailViewModel = hiltViewModel(),
    market: String = "KRW-BTC"
) {
    NewOrderScreen(
        initCoinOrder = viewModel::initCoinOrder,
        coinOrderScreenOnPause = viewModel::coinOrderScreenOnPause,
        market = market,
        preClosedPrice = 0.0,
        orderBookList = viewModel.getOrderBookList(),
        maxOrderBookSize = viewModel.getMaxOrderBookSize(),
        coinPrice = BigDecimal(0.0)
    )
}