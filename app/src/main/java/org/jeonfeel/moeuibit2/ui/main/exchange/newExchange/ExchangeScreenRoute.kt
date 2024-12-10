package org.jeonfeel.moeuibit2.ui.main.exchange.newExchange

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent

@Composable
fun ExchangeScreenRoute(
    viewModel: ExchangeViewModel = hiltViewModel(),
    appNavController: NavHostController
) {
    AddLifecycleEvent(
        onPauseAction = {
            viewModel.onPause()
        },
        onResumeAction = {
            viewModel.onResume()
        }
    )

    ExchangeScreen(
        tickerList = viewModel.getTickerList(),
        isUpdateExchange = viewModel.isUpdateExchange,
        sortTickerList = viewModel::sortTickerList,
        tradeCurrencyState = viewModel.tradeCurrencyState,
        changeTradeCurrency = viewModel::changeTradeCurrency,
        btcKrwPrice = viewModel.getBtcPrice(),
        appNavController = appNavController,
    )
}
