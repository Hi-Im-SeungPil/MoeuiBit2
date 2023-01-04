package org.jeonfeel.moeuibit2.ui.coindetail

import androidx.compose.foundation.layout.height
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.jeonfeel.moeuibit2.MoeuiBitDataStore.isKor
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.viewmodels.CoinDetailViewModel
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ChartScreen
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.CoinInfoScreen
import org.jeonfeel.moeuibit2.ui.coindetail.order.OrderScreen
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil
import org.jeonfeel.moeuibit2.utils.showToast


sealed class CoinDetailMainTabRowItem(var title: String, var screen_route: String) {
    object Order : CoinDetailMainTabRowItem(COIN_DETAIL_MAIN_TAB_ROW_ITEM_ORDER, "order")
    object Chart : CoinDetailMainTabRowItem(COIN_DETAIL_MAIN_TAB_ROW_ITEM_CHART, "Chart")
    object CoinInfo : CoinDetailMainTabRowItem(COIN_DETAIL_MAIN_TAB_ROW_ITEM_COIN_INFO, "CoinInfo")
}

sealed class CoinDetailMainTabRowItemForEn(var title: String, var screen_route: String) {
    object Order : CoinDetailMainTabRowItem(COIN_DETAIL_MAIN_TAB_ROW_ITEM_ORDER_FOR_EN, "order")
    object Chart : CoinDetailMainTabRowItem(COIN_DETAIL_MAIN_TAB_ROW_ITEM_CHART_FOR_EN, "Chart")
    object CoinInfo : CoinDetailMainTabRowItem(COIN_DETAIL_MAIN_TAB_ROW_ITEM_COIN_INFO_FOR_EN, "CoinInfo")
}

@Composable
fun CoinDetailMainTabRow(navController: NavController) {
    val tabState = remember { mutableStateOf(0) }
    val items = if(isKor) {
        listOf(
            CoinDetailMainTabRowItem.Order,
            CoinDetailMainTabRowItem.Chart,
            CoinDetailMainTabRowItem.CoinInfo
        )
    } else {
        listOf(
            CoinDetailMainTabRowItemForEn.Order,
            CoinDetailMainTabRowItemForEn.Chart,
            CoinDetailMainTabRowItemForEn.CoinInfo
        )
    }


    TabRow(
        selectedTabIndex = tabState.value,
        modifier = Modifier.height(40.dp),
        backgroundColor = colorResource(id = R.color.white)
    ) {
        items.forEachIndexed { index, tab ->
            Tab(text = { Text(tab.title) },
                selected = tabState.value == index,
                selectedContentColor = colorResource(id = R.color.C0F0F5C),
                unselectedContentColor = colorResource(id = R.color.CDCDCDC),
                onClick = {
                    tabState.value = index
                    navController.navigate(tab.screen_route) {
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
fun TabRowMainNavigation(
    navController: NavHostController,
    coinDetailViewModel: CoinDetailViewModel = viewModel(),
) {
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = CoinDetailMainTabRowItem.Order.screen_route
    ) {
        composable(CoinDetailMainTabRowItem.Order.screen_route) {
            if (NetworkMonitorUtil.currentNetworkState != NO_INTERNET_CONNECTION) {
                OrderScreen(coinDetailViewModel)
            } else {
                context.showToast(stringResource(id = R.string.NO_INTERNET_CONNECTION))
            }
        }
        composable(CoinDetailMainTabRowItem.Chart.screen_route) {
            if (NetworkMonitorUtil.currentNetworkState != NO_INTERNET_CONNECTION) {
                ChartScreen(coinDetailViewModel)
            } else {
                context.showToast(stringResource(id = R.string.NO_INTERNET_CONNECTION))
            }
        }
        composable(CoinDetailMainTabRowItem.CoinInfo.screen_route) {
            if (NetworkMonitorUtil.currentNetworkState != NO_INTERNET_CONNECTION) {
                CoinInfoScreen(coinDetailViewModel)
            } else {
                context.showToast(stringResource(id = R.string.NO_INTERNET_CONNECTION))
            }
        }
    }
}
