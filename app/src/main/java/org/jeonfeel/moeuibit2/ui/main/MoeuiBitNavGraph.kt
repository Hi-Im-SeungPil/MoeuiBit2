package org.jeonfeel.moeuibit2.ui.main

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.jeonfeel.moeuibit2.ui.AppScreen
import org.jeonfeel.moeuibit2.ui.coindetail.newS.NewCoinDetailScreen
import org.jeonfeel.moeuibit2.ui.main.coinsite.CoinSiteScreen
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeRoute
import org.jeonfeel.moeuibit2.ui.main.portfolio.PortfolioScreenRoute
import org.jeonfeel.moeuibit2.ui.main.setting.SettingScreen

@Composable
fun MainBottomNavGraph(
    navController: NavHostController,
    networkErrorState: MutableIntState,
    appNavController: NavHostController
) {
    NavHost(navController, startDestination = MainBottomNavItem.Exchange.screen_route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }) {
        composable(MainBottomNavItem.Exchange.screen_route) {
            ExchangeRoute(
                networkErrorState = networkErrorState,
                appNavController = appNavController
            )
        }
        composable(MainBottomNavItem.CoinSite.screen_route) {
            CoinSiteScreen()
        }
        composable(MainBottomNavItem.Portfolio.screen_route) {
            PortfolioScreenRoute()
        }
        composable(MainBottomNavItem.Setting.screen_route) {
            SettingScreen()
        }
    }
}