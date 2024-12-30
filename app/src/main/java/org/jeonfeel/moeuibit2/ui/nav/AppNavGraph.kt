package org.jeonfeel.moeuibit2.ui.nav

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.Caution
import org.jeonfeel.moeuibit2.ui.MoeuiBitApp
import org.jeonfeel.moeuibit2.ui.coindetail.newS.CoinDetailScreenRoute
import org.jeonfeel.moeuibit2.utils.Utils

enum class AppScreen {
    Home,
    CoinDetail
}

@Composable
fun AppNavGraph(
    networkErrorState: MutableIntState
) {
    val appNavController = rememberNavController()
    val bottomNavController = rememberNavController()

    NavHost(appNavController, startDestination = AppScreen.Home.name) {
        composable(
            AppScreen.Home.name,
            popExitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None }) {
            MoeuiBitApp(
                networkErrorState = networkErrorState,
                appNavController = appNavController,
                bottomNavController = bottomNavController
            )
        }

        composable(
            "${AppScreen.CoinDetail.name}/{market}/{warning}/{caution}",
            arguments = listOf(
                navArgument("market") { type = NavType.StringType },
                navArgument("warning") { type = NavType.BoolType },
                navArgument("caution") { type = NavType.StringType }
            ),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                )
            }
        ) { backStackEntry ->
            val market = backStackEntry.arguments?.getString("market") ?: ""
            val warning =
                backStackEntry.arguments?.getBoolean("warning") ?: false
            val caution = backStackEntry.arguments?.getString("caution") ?: ""
            val cautionModel =
                caution?.let { Utils.gson.fromJson(caution, Caution::class.java) } ?: null
            CoinDetailScreenRoute(
                market = market,
                warning = warning,
                cautionModel = cautionModel,
                appNavController = appNavController
            )
        }
    }
}