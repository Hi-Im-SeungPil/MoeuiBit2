package org.jeonfeel.moeuibit2.ui.coindetail.newS

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.Caution

@Composable
fun CoinDetailScreenRoute(
    market: String,
    warning: Boolean,
    appNavController: NavHostController,
    cautionModel: Caution
) {
    BackHandler {
        appNavController.popBackStack()
    }

    NewCoinDetailScreen(
        market = market,
        warning = warning,
        caution = cautionModel,
        navController = appNavController
    )
}