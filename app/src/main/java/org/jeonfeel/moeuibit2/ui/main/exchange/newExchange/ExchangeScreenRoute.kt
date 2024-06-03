package org.jeonfeel.moeuibit2.ui.main.exchange.newExchange

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.channels.ticker
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel

@Composable
fun ExchangeScreenRoute(
    viewModel: ExchangeViewModel = hiltViewModel()
) {
    ExchangeScreen(
        tickerList = viewModel.getTickerList(),
        isUpdateExchange = viewModel.isUpdateExchange,
        sortTickerList = viewModel::sortTickerList
    )
}