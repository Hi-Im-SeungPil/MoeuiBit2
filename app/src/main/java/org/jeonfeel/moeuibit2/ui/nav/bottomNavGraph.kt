package org.jeonfeel.moeuibit2.ui.nav

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.jeonfeel.moeuibit2.ui.main.additional_features.AdditionalFeaturesRoute
import org.jeonfeel.moeuibit2.ui.main.coinsite.CoinMarketConditionRoute
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeScreenRoute
import org.jeonfeel.moeuibit2.ui.main.portfolio.PortfolioScreenRoute
import org.jeonfeel.moeuibit2.ui.main.setting.SettingScreen

@Composable
fun MainBottomNavGraph(
    bottomNavController: NavHostController,
    appNavController: NavHostController,
) {
    NavHost(bottomNavController, startDestination = MainBottomNavItem.Exchange.screenRoute.name,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }) {
        composable(MainBottomNavItem.Exchange.screenRoute.name) {
            ExchangeScreenRoute(appNavController = appNavController)
        }

        composable(MainBottomNavItem.CoinSite.screenRoute.name) {
//            CoinSiteScreen()
            CoinMarketConditionRoute(appNavController = appNavController)
        }

        composable(MainBottomNavItem.AdditionalFeatures.screenRoute.name) {
            AdditionalFeaturesRoute()
        }

        composable(MainBottomNavItem.Portfolio.screenRoute.name) {
            PortfolioScreenRoute(
                appNavController = appNavController,
                bottomNavController = bottomNavController
            )
        }

        composable(MainBottomNavItem.Setting.screenRoute.name) {
            SettingScreen()
        }
    }
}