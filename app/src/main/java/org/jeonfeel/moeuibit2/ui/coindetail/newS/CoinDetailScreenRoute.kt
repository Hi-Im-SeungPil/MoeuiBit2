package org.jeonfeel.moeuibit2.ui.coindetail.newS

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import org.jeonfeel.moeuibit2.ui.AppScreen

@Composable
fun CoinDetailScreenRoute(
    market: String,
    warning: Boolean,
    navController: NavHostController
) {
//    BackHandler {
//        navController.navigate(AppScreen.Home.name)
//    }
    NewCoinDetailScreen(
        market = market,
        warning = warning
    )
}