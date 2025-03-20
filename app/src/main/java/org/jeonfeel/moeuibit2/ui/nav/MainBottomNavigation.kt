package org.jeonfeel.moeuibit2.ui.nav

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.menuTitleArray
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.newtheme.APP_PRIMARY_COLOR
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonDividerColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonUnSelectedColor

enum class MainScreenState {
    EXCHANGE, COIN_SITE, ADDITIONAL_FEATURES, PORTFOLIO, SETTING
}

sealed class MainBottomNavItem(var title: String, var icon: Int, var screenRoute: MainScreenState) {
    data object Exchange :
        MainBottomNavItem(menuTitleArray[0], R.drawable.img_exchange, MainScreenState.EXCHANGE)

    data object CoinSite :
        MainBottomNavItem(menuTitleArray[1], R.drawable.img_internet, MainScreenState.COIN_SITE)

    data object AdditionalFeatures :
        MainBottomNavItem(menuTitleArray[2], R.drawable.img_license, MainScreenState.ADDITIONAL_FEATURES)

    data object Portfolio :
        MainBottomNavItem(menuTitleArray[3], R.drawable.img_portfolio, MainScreenState.PORTFOLIO)

    data object Setting :
        MainBottomNavItem(menuTitleArray[4], R.drawable.img_setting, MainScreenState.SETTING)
}

@Composable
fun MainBottomNavigation(navController: NavHostController) {
    val items = listOf(
        MainBottomNavItem.Exchange,
        MainBottomNavItem.CoinSite,
        MainBottomNavItem.AdditionalFeatures,
        MainBottomNavItem.Portfolio,
        MainBottomNavItem.Setting
    )

    Column {
        if (isSystemInDarkTheme()) {
            Divider(modifier = Modifier.fillMaxWidth(), color = commonDividerColor())
        }
        BottomNavigation(
            backgroundColor = commonBackground(),
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
                                .size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            fontSize = DpToSp(dp = 12.dp),
                            fontWeight = FontWeight.W600
                        )
                    },
                    selectedContentColor = APP_PRIMARY_COLOR,
                    unselectedContentColor = commonUnSelectedColor(),
                    alwaysShowLabel = true,
                    selected = currentDestination?.hierarchy?.any { it.route == item.screenRoute.name } == true,
                    onClick = {
                        if (currentDestination?.hierarchy?.any() { it.route == item.screenRoute.name } == true) {
                            return@BottomNavigationItem
                        }

                        navController.navigate(item.screenRoute.name) {
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
}

@Composable
@Preview(showBackground = true)
fun MainBottomNavigationPreview() {
    val context = LocalContext.current
//    MainBottomNavigation(navController = NavController(context))
}