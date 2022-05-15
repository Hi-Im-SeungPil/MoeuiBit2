package org.jeonfeel.moeuibit2.ui.coindetail

import androidx.compose.foundation.layout.height
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ChartScreen
import org.jeonfeel.moeuibit2.ui.coindetail.order.OrderScreen
import org.jeonfeel.moeuibit2.viewmodel.coindetail.CoinDetailViewModel


sealed class CoinDetailMainTabRowItem(var title: String, var screen_route: String) {
    object Order : CoinDetailMainTabRowItem("주문", "order")
    object AskingPrice : CoinDetailMainTabRowItem("호가", "AskingPrice")
    object Chart : CoinDetailMainTabRowItem("차트", "Chart")
    object MarketPrice : CoinDetailMainTabRowItem("시세", "MarketPrice")
    object CoinInfo : CoinDetailMainTabRowItem("정보", "CoinInfo")
}

@Composable
fun CoinDetailMainTabRow(navController: NavController) {
    val tabState = remember { mutableStateOf(0) }
    val items = listOf(
        CoinDetailMainTabRowItem.Order,
        CoinDetailMainTabRowItem.AskingPrice,
        CoinDetailMainTabRowItem.Chart,
        CoinDetailMainTabRowItem.MarketPrice,
        CoinDetailMainTabRowItem.CoinInfo
    )

    TabRow(selectedTabIndex = tabState.value,
        modifier = Modifier.height(40.dp),
        backgroundColor = colorResource(id = R.color.white)) {
        items.forEachIndexed { index, tab ->
            Tab(text = { Text(tab.title) },
                selected = tabState.value == index,
                selectedContentColor = colorResource(id = R.color.C0F0F5C),
                unselectedContentColor = colorResource(id = R.color.CDCDCDC),
                onClick = {
                    tabState.value = index
                    navController.navigate(tab.screen_route){
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

    NavHost(navController = navController,startDestination = CoinDetailMainTabRowItem.Order.screen_route) {
        composable(CoinDetailMainTabRowItem.Order.screen_route) {
            OrderScreen(coinDetailViewModel)
        }
        composable(CoinDetailMainTabRowItem.AskingPrice.screen_route) {
            AskedPriceScreen()
        }
        composable(CoinDetailMainTabRowItem.Chart.screen_route) {
            ChartScreen(coinDetailViewModel)
        }
        composable(CoinDetailMainTabRowItem.MarketPrice.screen_route) {
            MarketPriceScreen()
        }
        composable(CoinDetailMainTabRowItem.CoinInfo.screen_route) {
            CoinInfoScreen(coinDetailViewModel)
        }
    }
}



//                Tab(
//                    text = { Text(title, style = TextStyle(fontSize = 16.sp)) },
//                    selected = state.value == index,
//                    onClick = { state.value = index },
//                    selectedContentColor = colorResource(id = R.color.C0F0F5C),
//                    unselectedContentColor = colorResource(id = R.color.CDCDCDC)
//                )
