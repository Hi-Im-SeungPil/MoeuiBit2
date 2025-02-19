package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jeonfeel.moeuibit2.ui.common.CommonLoading
import org.jeonfeel.moeuibit2.ui.common.ResultState
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent

@Composable
fun CoinInfoRoute(
    viewModel: CoinInfoViewModel = hiltViewModel(),
    engName: String,
    symbol: String,
) {
    val coinInfoStateHolder = rememberCoinInfoScreenStateHolder()

    AddLifecycleEvent(
        onStartAction = {
            viewModel.init(engName = engName, symbol = symbol)
        },
        onStopAction = {

        }
    )

    when (viewModel.uiState.value.state) {
        UIState.SUCCESS -> {
            CoinInfoScreen(
                coinInfoModel = viewModel.uiState.value.coinInfoModel,
                coinLinkList = viewModel.uiState.value.coinLinkList,
                moveToWeb = coinInfoStateHolder.actionHandler::moveToUrl
            )
        }

        UIState.ERROR -> {

        }

        UIState.LOADING -> {
            CommonLoading()
        }
    }
}