package org.jeonfeel.moeuibit2.ui.main.coinsite

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent

@Composable
fun CoinMarketConditionRoute(
    viewModel: CoinMarketConditionViewModel = hiltViewModel(),
    appNavController: NavHostController
) {
    val state = rememberCoinMarketConditionScreen(appNavHostController = appNavController)

    AddLifecycleEvent(
        onResumeAction = {
            viewModel.onResume()
        }
    )

    CoinMarketConditionScreen(
        uiState = viewModel.uiState.value,
        navigateDominanceChart = state::navigateDominanceChart
    )
}