package org.jeonfeel.moeuibit2.ui.coindetail

import androidx.compose.foundation.layout.height
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.ui.coindetail.newScreen.NewCoinDetailViewModel
import org.jeonfeel.moeuibit2.ui.coindetail.newScreen.order.OrderScreenRoute
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.tabRowSelectedColor
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil
import org.jeonfeel.moeuibit2.utils.showToast


sealed class CoinDetailMainTabRowItem(var title: String, var screen_route: String) {
    object Order : CoinDetailMainTabRowItem(COIN_DETAIL_MAIN_TAB_ROW_ITEM_ORDER, "order")
    object Chart : CoinDetailMainTabRowItem(COIN_DETAIL_MAIN_TAB_ROW_ITEM_CHART, "Chart")
    object CoinInfo : CoinDetailMainTabRowItem(COIN_DETAIL_MAIN_TAB_ROW_ITEM_COIN_INFO, "CoinInfo")
}

@Composable
fun CoinDetailMainTabRow(navController: NavController) {
    val tabState = remember { mutableStateOf(0) }
    val items = listOf(
        CoinDetailMainTabRowItem.Order,
        CoinDetailMainTabRowItem.Chart,
        CoinDetailMainTabRowItem.CoinInfo
    )

    TabRow(
        selectedTabIndex = tabState.value,
        modifier = Modifier.height(35.dp),
        backgroundColor = MaterialTheme.colorScheme.background
    ) {
        items.forEachIndexed { index, tab ->
            Tab(text = { Text(tab.title, fontSize = DpToSp(14.dp), fontWeight = FontWeight.W600) },
                selected = tabState.value == index,
                selectedContentColor = tabRowSelectedColor(),
                unselectedContentColor = colorResource(id = R.color.CDCDCDC),
                onClick = {
                    if (tabState.value != index) {
                        tabState.value = index
                        navController.navigate(tab.screen_route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun TabRowMainNavigation(
    navHostController: NavHostController,
    viewModel: NewCoinDetailViewModel = hiltViewModel(),
    market: String
) {
    val context = LocalContext.current
    NavHost(
        navController = navHostController,
        startDestination = CoinDetailMainTabRowItem.Order.screen_route
    ) {
        composable(CoinDetailMainTabRowItem.Order.screen_route) {
            if (NetworkMonitorUtil.currentNetworkState != NO_INTERNET_CONNECTION) {
                OrderScreenRoute(
                    market = market,
                    initCoinOrder = viewModel::initCoinOrder,
                    coinOrderScreenOnPause = viewModel::coinOrderScreenOnPause,
                    coinOrderScreenOnResume = viewModel::coinOrderScreenOnResume,
                    preClosedPrice = viewModel.coinTicker,
                    orderBookList = viewModel.getOrderBookList(),
                    maxOrderBookSize = viewModel.getMaxOrderBookSize(),
                    orderBookIndication = viewModel.orderBookIndication,
                    changeOrderBookIndicationState = viewModel::changeOrderBookIndication,
                    saveOrderBookIndicationState = viewModel::saveOrderBookIndication,
                    getUserSeedMoney = viewModel::getUserSeedMoney
                )
            } else {
                context.showToast(stringResource(id = R.string.NO_INTERNET_CONNECTION))
            }
        }
        composable(CoinDetailMainTabRowItem.Chart.screen_route) {
            if (NetworkMonitorUtil.currentNetworkState != NO_INTERNET_CONNECTION) {
//                ChartScreen(coinDetailViewModel)
            } else {
                context.showToast(stringResource(id = R.string.NO_INTERNET_CONNECTION))
            }
        }
        composable(CoinDetailMainTabRowItem.CoinInfo.screen_route) {
            if (NetworkMonitorUtil.currentNetworkState != NO_INTERNET_CONNECTION) {
//                CoinInfoScreen(coinDetailViewModel)
            } else {
                context.showToast(stringResource(id = R.string.NO_INTERNET_CONNECTION))
            }
        }
    }
}
