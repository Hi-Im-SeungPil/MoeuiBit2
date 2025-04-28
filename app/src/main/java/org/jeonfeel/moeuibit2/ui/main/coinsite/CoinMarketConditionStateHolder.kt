package org.jeonfeel.moeuibit2.ui.main.coinsite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController

enum class MarketConditionScreenState {
    COIN_MARKET_CONDITION,
    COIN_SITE,
}

class CoinMarketConditionStateHolder(
    private val bottomNavController: NavHostController,
) {

}

@Composable
fun rememberCoinMarketConditionScreen(
    bottomNavController: NavHostController,
) = remember {
    CoinMarketConditionStateHolder(bottomNavController = bottomNavController)
}