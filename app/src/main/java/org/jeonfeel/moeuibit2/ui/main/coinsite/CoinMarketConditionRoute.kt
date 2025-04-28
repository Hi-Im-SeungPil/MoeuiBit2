package org.jeonfeel.moeuibit2.ui.main.coinsite

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import org.jeonfeel.moeuibit2.ui.main.coinsite.component.CoinSiteScreen
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent

@Composable
fun CoinMarketConditionRoute(
    viewModel: CoinMarketConditionViewModel = hiltViewModel(),
    bottomNavController: NavHostController,
) {
    val state = rememberCoinMarketConditionScreen(bottomNavController = bottomNavController)

    AddLifecycleEvent(
        onResumeAction = {
            viewModel.onResume()
        }
    )

    BackHandler {
        if (viewModel.uiState.value.marketConditionUIState.value == MarketConditionScreenState.COIN_SITE) {
            viewModel.updateScreenState(MarketConditionScreenState.COIN_MARKET_CONDITION)
        } else {
            bottomNavController.navigateUp()
        }
    }

    when (viewModel.uiState.value.marketConditionUIState.value) {
        MarketConditionScreenState.COIN_MARKET_CONDITION -> {
            CoinMarketConditionScreen(
                uiState = viewModel.uiState.value,
                navigateToCoinSite = { viewModel.updateScreenState(MarketConditionScreenState.COIN_SITE) }
            )
        }

        MarketConditionScreenState.COIN_SITE -> {
            CoinSiteScreen(
                uiState = viewModel.uiState.value.marketConditionUIState.value,
                navigateUp = { viewModel.updateScreenState(MarketConditionScreenState.COIN_MARKET_CONDITION) }
            )
        }
    }
}