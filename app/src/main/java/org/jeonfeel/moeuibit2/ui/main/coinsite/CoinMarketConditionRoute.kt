package org.jeonfeel.moeuibit2.ui.main.coinsite

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent

@Composable
fun CoinMarketConditionRoute(
    viewModel: CoinMarketConditionViewModel = hiltViewModel()
) {
    AddLifecycleEvent(
        onResumeAction = {
            viewModel.onResume()
        }
    )

    CoinMarketConditionScreen(
        uiState = viewModel.uiState.value
    )
}