package org.jeonfeel.moeuibit2.ui.coindetail

import android.widget.Toast
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.jeonfeel.moeuibit2.constant.NO_INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ChartScreen
import org.jeonfeel.moeuibit2.ui.coindetail.order.OrderScreen
import org.jeonfeel.moeuibit2.util.NetworkMonitorUtil
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.CoinDetailViewModel
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.CoinInfoScreen


sealed class CoinDetailMainTabRowItem(var title: String, var screen_route: String) {
    object Order : CoinDetailMainTabRowItem("주문", "order")
    object Chart : CoinDetailMainTabRowItem("차트", "Chart")
    object CoinInfo : CoinDetailMainTabRowItem("정보", "CoinInfo")
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
                Toast.makeText(context, "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        composable(CoinDetailMainTabRowItem.Chart.screen_route) {
            if (NetworkMonitorUtil.currentNetworkState != NO_INTERNET_CONNECTION) {
                ChartScreen(coinDetailViewModel)
            } else {
                Toast.makeText(context, "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        composable(CoinDetailMainTabRowItem.CoinInfo.screen_route) {
            if (NetworkMonitorUtil.currentNetworkState != NO_INTERNET_CONNECTION) {
                CoinInfoScreen(coinDetailViewModel)
            } else {
                Toast.makeText(context, "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
