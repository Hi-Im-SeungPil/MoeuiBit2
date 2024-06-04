package org.jeonfeel.moeuibit2.ui.main.exchange.newExchange

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel

@Composable
fun ExchangeScreenRoute(
    viewModel: ExchangeViewModel = hiltViewModel()
) {
    ExchangeScreen(
        tickerList = viewModel.getTickerList(),
        isUpdateExchange = viewModel.isUpdateExchange,
        sortTickerList = viewModel::sortTickerList,
        tradeCurrencyState = viewModel.tradeCurrencyState,
        changeTradeCurrency = viewModel::changeTradeCurrency,
        onPaused = viewModel::onPause,
        onResume = viewModel::onResume
    )
}