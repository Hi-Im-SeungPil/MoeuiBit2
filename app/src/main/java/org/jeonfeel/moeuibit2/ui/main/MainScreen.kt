package org.jeonfeel.moeuibit2.ui.main

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.menuTitleArray
import org.jeonfeel.moeuibit2.ui.viewmodels.MainViewModel
import org.jeonfeel.moeuibit2.ui.main.coinsite.CoinSiteScreen
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeScreen
import org.jeonfeel.moeuibit2.ui.main.portfolio.PortfolioScreen
import org.jeonfeel.moeuibit2.ui.main.setting.SettingScreen
import org.jeonfeel.moeuibit2.ui.viewmodels.ExchangeViewModel
import org.jeonfeel.moeuibit2.ui.viewmodels.PortfolioViewModel

sealed class MainBottomNavItem(var title: String, var icon: Int, var screen_route: String) {
    object Exchange : MainBottomNavItem(menuTitleArray[0], R.drawable.img_exchange, "exchange")
    object CoinSite : MainBottomNavItem(menuTitleArray[1], R.drawable.img_internet, "site")
    object Portfolio : MainBottomNavItem(menuTitleArray[2], R.drawable.img_report, "portfolio")
    object Setting : MainBottomNavItem(menuTitleArray[3], R.drawable.img_setting, "setting")
}

@Composable
fun MainBottomNavigation(navController: NavController) {
    val items = listOf(
        MainBottomNavItem.Exchange,
        MainBottomNavItem.CoinSite,
        MainBottomNavItem.Portfolio,
        MainBottomNavItem.Setting
    )
    BottomNavigation(
        backgroundColor = colorResource(id = R.color.design_default_color_background)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painterResource(id = item.icon),
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize(0.4f)
                    )
                },
                label = { Text(text = item.title) },
                selectedContentColor = colorResource(id = R.color.C0F0F5C),
                unselectedContentColor = androidx.compose.ui.graphics.Color.LightGray,
                alwaysShowLabel = true,
                selected = currentDestination?.hierarchy?.any { it.route == item.screen_route } == true,
                onClick = {
                    if (currentDestination?.hierarchy?.any() { it.route == item.screen_route } == true) {
                        return@BottomNavigationItem
                    }

                    navController.navigate(item.screen_route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun MainNavigation(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    startForActivityResult: ActivityResultLauncher<Intent>,
    scaffoldState: ScaffoldState,
) {
    NavHost(navController, startDestination = MainBottomNavItem.Exchange.screen_route) {
        composable(MainBottomNavItem.Exchange.screen_route) {
            val exchangeViewModel: ExchangeViewModel = viewModel(
                factory = ExchangeViewModel.provideFactory(
                    mainViewModel.remoteRepository,
                    mainViewModel.localRepository
                )
            )
            ExchangeScreen(exchangeViewModel, startForActivityResult)
        }
        composable(MainBottomNavItem.CoinSite.screen_route) {
            CoinSiteScreen()
        }
        composable(MainBottomNavItem.Portfolio.screen_route) {
            val portfolioViewModel: PortfolioViewModel = viewModel(
                factory = PortfolioViewModel.provideFactory(
                    mainViewModel.remoteRepository,
                    mainViewModel.localRepository
                )
            )
            PortfolioScreen(portfolioViewModel, startForActivityResult, scaffoldState)
        }
        composable(MainBottomNavItem.Setting.screen_route) {
            SettingScreen(mainViewModel)
        }
    }
}