package org.jeonfeel.moeuibit2.ui.main.coinsite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import org.jeonfeel.moeuibit2.ui.nav.AppScreen
import org.jeonfeel.moeuibit2.utils.Utils

class CoinMarketConditionStateHolder(
    private val appNavHostController: NavHostController,
) {

    fun navigateDominanceChart(
        title: String,
        symbol: String,
    ) {
        appNavHostController.navigate("${AppScreen.DOMINANCE_CHART.name}/$title/$symbol") {
            launchSingleTop = true
            popUpTo(appNavHostController.graph.findStartDestination().id) {
                saveState = true
            }
            restoreState = true
        }
    }
}

@Composable
fun rememberCoinMarketConditionScreen(
    appNavHostController: NavHostController,
) = remember {
    CoinMarketConditionStateHolder(appNavHostController = appNavHostController)
}