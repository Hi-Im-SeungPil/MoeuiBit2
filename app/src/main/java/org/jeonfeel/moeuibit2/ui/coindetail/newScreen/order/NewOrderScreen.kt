package org.jeonfeel.moeuibit2.ui.coindetail.newScreen.order

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.OrderBookModel
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent

@Composable
fun OrderScreen(
    newCoinDetailViewModel: NewCoinDetailViewModel = hiltViewModel(),
    market: String
) {
    AddLifecycleEvent(
        onCreateAction = {
            newCoinDetailViewModel.initCoinOrder(market)
        },
        onPauseAction = {
            newCoinDetailViewModel.coinOrderScreenOnPause()
        },
        onResumeAction = {

        }
    )
}

@Composable
fun OrderBookSection(orderBookList: List<OrderBookModel>) {
    LazyColumn {

    }
}

@Composable
fun OrderBookView(item: OrderBookModel) {

}