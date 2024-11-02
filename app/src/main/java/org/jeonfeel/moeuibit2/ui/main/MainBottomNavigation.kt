package org.jeonfeel.moeuibit2.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.menuTitleArray
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.bottomNavigatorSelectedColor

sealed class MainBottomNavItem(var title: String, var icon: Int, var screen_route: String) {
    data object Exchange : MainBottomNavItem(menuTitleArray[0], R.drawable.img_exchange, "exchange")
    data object CoinSite : MainBottomNavItem(menuTitleArray[1], R.drawable.img_internet, "site")
    data object Portfolio : MainBottomNavItem(menuTitleArray[2], R.drawable.img_report, "portfolio")
    data object Setting : MainBottomNavItem(menuTitleArray[3], R.drawable.img_setting, "setting")
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
        backgroundColor = MaterialTheme.colorScheme.background
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painterResource(id = item.icon),
                        contentDescription = item.title,
                        modifier = Modifier
                            .padding(vertical = 7.dp)
                            .size(20.dp)
                    )
                },
                label = { Text(text = item.title, fontSize = DpToSp(dp = 10.dp)) },
                selectedContentColor = bottomNavigatorSelectedColor(),
                unselectedContentColor = Color.LightGray,
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
@Preview(showBackground = true)
fun MainBottomNavigationPreview() {
    val context = LocalContext.current
    MainBottomNavigation(navController = NavController(context))
}