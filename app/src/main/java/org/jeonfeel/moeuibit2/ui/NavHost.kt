package org.jeonfeel.moeuibit2.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.jeonfeel.moeuibit2.ui.coindetail.newS.CoinDetailScreenRoute

enum class AppScreen {
    Home,
    CoinDetail
}

@Composable
fun NavGraph(
    navController: NavHostController,
    networkErrorState: MutableIntState
) {
    NavHost(navController, startDestination = AppScreen.Home.name) {
        composable(AppScreen.Home.name) {
            MoeuiBitApp(networkErrorState = networkErrorState, appNavController = navController)
        }
        composable(AppScreen.CoinDetail.name) {
            CoinDetailScreenRoute()
        }
    }
}