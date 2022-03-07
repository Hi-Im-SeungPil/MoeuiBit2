package org.jeonfeel.moeuibit2.ui.mainactivity

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.jeonfeel.moeuibit2.R

sealed class MainBottomNavItem(var title: String, var icon: Int, var screen_route: String) {
    object Exchange : MainBottomNavItem("거래소", R.drawable.img_exchange, "exchange")
    object CoinSite : MainBottomNavItem("코인 사이트", R.drawable.img_internet, "site")
    object Portfolio : MainBottomNavItem("투자 내역", R.drawable.img_report, "portfolio")
    object Setting : MainBottomNavItem("설정", R.drawable.img_setting, "setting")
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
        backgroundColor = colorResource(id = R.color.statusBar),
        contentColor = colorResource(id = R.color.white),
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(painterResource(id = item.icon),
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize(0.4f))
                },
                label = { Text(text = item.title) },
                selectedContentColor = androidx.compose.ui.graphics.Color.White,
                unselectedContentColor = androidx.compose.ui.graphics.Color.White.copy(0.4f),
                alwaysShowLabel = true,
                selected = currentRoute == item.screen_route,
                onClick = {
                    navController.navigate(item.screen_route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
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
fun Navigation(navController: NavHostController) {
    NavHost(navController, startDestination = MainBottomNavItem.Exchange.screen_route) {
        composable(MainBottomNavItem.Exchange.screen_route) {
            ExchangeScreen()
        }
    }
}

