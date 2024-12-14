package org.jeonfeel.moeuibit2.ui.nav

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jeonfeel.moeuibit2.ui.main.MainBottomNavItem
import org.jeonfeel.moeuibit2.ui.main.coinsite.CoinSiteScreen
import org.jeonfeel.moeuibit2.ui.main.exchange.newExchange.ExchangeScreenRoute
import org.jeonfeel.moeuibit2.ui.main.portfolio.PortfolioScreenRoute
import org.jeonfeel.moeuibit2.ui.main.setting.SettingScreen

@Composable
fun MainBottomNavGraph(
    bottomNavController: NavHostController,
    appNavController: NavHostController,
    networkErrorState: MutableIntState,
) {
    NavHost(bottomNavController, startDestination = MainBottomNavItem.Exchange.screenRoute.name,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }) {
        composable(MainBottomNavItem.Exchange.screenRoute.name) {
            ExchangeScreenRoute(appNavController = appNavController)
        }
        composable(MainBottomNavItem.CoinSite.screenRoute.name) {
            CoinSiteScreen()
        }
        composable(MainBottomNavItem.Portfolio.screenRoute.name) {
            PortfolioScreenRoute()
        }
        composable(MainBottomNavItem.Setting.screenRoute.name) {
            SettingScreen()
        }
    }
}