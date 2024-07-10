package org.jeonfeel.moeuibit2.ui.coindetail.newS

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import org.jeonfeel.moeuibit2.ui.coindetail.newScreen.order.NewCoinDetailViewModel

@Composable
fun CoinDetailScreenRoute(
    viewModel: NewCoinDetailViewModel = hiltViewModel()
) {
//    val market = ""
    NewCoinDetailScreen(
        viewModel = viewModel,
        warning = ""
    )
}