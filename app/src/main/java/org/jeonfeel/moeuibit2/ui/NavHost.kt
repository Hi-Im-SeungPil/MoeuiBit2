package org.jeonfeel.moeuibit2.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
        composable(
            "${AppScreen.CoinDetail.name}/{market}/{warning}",
            arguments = listOf(
                navArgument("market") { type = NavType.StringType },
                navArgument("warning") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val market = backStackEntry.arguments?.getString("market") ?: ""
            val warning =
                backStackEntry.arguments?.getBoolean("warning") ?: false
            CoinDetailScreenRoute(market = market, warning = warning, navController = navController)
        }
    }
}