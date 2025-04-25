package org.jeonfeel.moeuibit2.ui.coindetail.order.ui

import androidx.compose.foundation.layout.height
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.jeonfeel.moeuibit2.constants.COIN_DETAIL_MAIN_TAB_ROW_ITEM_CHART
import org.jeonfeel.moeuibit2.constants.COIN_DETAIL_MAIN_TAB_ROW_ITEM_COIN_INFO
import org.jeonfeel.moeuibit2.constants.COIN_DETAIL_MAIN_TAB_ROW_ITEM_ORDER
import org.jeonfeel.moeuibit2.ui.coindetail.detail.NewCoinDetailViewModel
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.ChartScreen
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.CoinInfoRoute
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.newtheme.APP_PRIMARY_COLOR
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonUnSelectedColor

sealed class CoinDetailMainTabRowItem(var title: String, var screenRoute: String) {
    data object Order : CoinDetailMainTabRowItem(COIN_DETAIL_MAIN_TAB_ROW_ITEM_ORDER, "order")
    data object Chart : CoinDetailMainTabRowItem(COIN_DETAIL_MAIN_TAB_ROW_ITEM_CHART, "Chart")
    data object CoinInfo :
        CoinDetailMainTabRowItem(COIN_DETAIL_MAIN_TAB_ROW_ITEM_COIN_INFO, "CoinInfo")
}

@Composable
fun CoinDetailMainTabRow(navController: NavController, tabState: MutableIntState) {

    val items = listOf(
        CoinDetailMainTabRowItem.Order,
        CoinDetailMainTabRowItem.Chart,
        CoinDetailMainTabRowItem.CoinInfo
    )

    TabRow(
        selectedTabIndex = tabState.intValue,
        modifier = Modifier.height(35.dp),
        backgroundColor = commonBackground(),
        indicator = { tabPositions -> }
    ) {
        items.forEachIndexed { index, tab ->
            Tab(text = { Text(tab.title, fontSize = DpToSp(14.dp), fontWeight = FontWeight.W600) },
                selected = tabState.intValue == index,
                selectedContentColor = APP_PRIMARY_COLOR,
                unselectedContentColor = commonUnSelectedColor(),
                onClick = {
                    if (tabState.intValue != index) {
                        tabState.intValue = index
                        navController.navigate(tab.screenRoute) {
                            popUpTo(navController.graph.id) {
                                saveState = true
                                inclusive = true
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
    viewModel: NewCoinDetailViewModel,
    market: String,
) {
    NavHost(
        navController = navHostController,
        startDestination = CoinDetailMainTabRowItem.Order.screenRoute,
    ) {
        composable(CoinDetailMainTabRowItem.Order.screenRoute) {
            OrderScreenRoute(
                market = market,
                initCoinOrder = viewModel::initCoinOrder,
                coinOrderScreenOnStop = viewModel::coinOrderScreenOnStop,
                coinOrderScreenOnStart = viewModel::coinOrderScreenOnStart,
                commonExchangeModelState = viewModel.coinTicker,
                orderBookList = viewModel.getOrderBookList(),
                maxOrderBookSize = viewModel.getMaxOrderBookSize(),
                orderBookIndicationState = viewModel.orderBookIndication,
                changeOrderBookIndicationState = viewModel::changeOrderBookIndication,
                saveOrderBookIndicationState = viewModel::saveOrderBookIndication,
                userSeedMoney = viewModel.getUserSeedMoney(),
                userBTC = viewModel.getUserBtcCoin(),
                userCoin = viewModel.getUserCoin(),
                requestBid = viewModel::requestBid,
                requestAsk = viewModel::requestAsk,
                btcPrice = viewModel.btcPrice,
                transactionInfoList = viewModel.transactionInfo,
                getTransactionInfoList = viewModel::getTransactionInfoList,
                isCoinOrderStarted = viewModel.isCoinOrderStarted
            )
        }
        composable(CoinDetailMainTabRowItem.Chart.screenRoute) {
            ChartScreen(viewModel = viewModel, market = market)
        }
        composable(CoinDetailMainTabRowItem.CoinInfo.screenRoute) {
            CoinInfoRoute(
                engName = viewModel.engCoinName.value,
                symbol = market.substring(4)
            )
        }
    }
}