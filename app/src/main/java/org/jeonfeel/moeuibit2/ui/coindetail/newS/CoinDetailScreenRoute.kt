package org.jeonfeel.moeuibit2.ui.coindetail.newS

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun CoinDetailScreenRoute(
    market: String,
    warning: Boolean,
    appNavController: NavHostController
) {
    BackHandler {
        appNavController.popBackStack()
    }

    NewCoinDetailScreen(
        market = market,
        warning = warning,
        navController = appNavController
    )
}