package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent

@Composable
fun CoinInfoRoute(
    viewModel: CoinInfoViewModel = hiltViewModel(),
    engName: String,
    symbol: String,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val coinInfoStateHolder = rememberCoinInfoScreenStateHolder()

    AddLifecycleEvent(
        onStartAction = {
            viewModel.init(engName = engName, symbol = symbol)
        },
        onStopAction = {

        }
    )

    CoinInfoScreen(
        uiState = uiState.value,
        moveToWeb = coinInfoStateHolder.actionHandler::moveToUrl
    )
}