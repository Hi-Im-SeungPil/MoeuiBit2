package org.jeonfeel.moeuibit2.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.jeonfeel.moeuibit2.ui.main.coinsite.CoinSiteScreen
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeRoute
import org.jeonfeel.moeuibit2.ui.main.portfolio.PortfolioScreen
import org.jeonfeel.moeuibit2.ui.main.portfolio.PortfolioViewModel
import org.jeonfeel.moeuibit2.ui.main.setting.SettingScreen
import org.jeonfeel.moeuibit2.ui.main.setting.SettingViewModel

@Composable
fun MainBottomNavGraph(
    navController: NavHostController,
    networkErrorState: MutableIntState
) {
    NavHost(navController, startDestination = MainBottomNavItem.Exchange.screen_route) {
        composable(MainBottomNavItem.Exchange.screen_route) {
            ExchangeRoute(networkErrorState = networkErrorState)
        }
        composable(MainBottomNavItem.CoinSite.screen_route) {
            CoinSiteScreen()
        }
        composable(MainBottomNavItem.Portfolio.screen_route) {
            PortfolioScreen()
        }
        composable(MainBottomNavItem.Setting.screen_route) {
            SettingScreen()
        }
    }
}