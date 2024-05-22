package org.jeonfeel.moeuibit2.ui.coindetail.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.constants.IS_ANOTHER_SCREEN
import org.jeonfeel.moeuibit2.constants.IS_DETAIL_SCREEN
import org.jeonfeel.moeuibit2.data.remote.websocket.bitthumb.BitthumbOrderBookWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.bitthumb.BitthumbTickerWebSocket
import org.jeonfeel.moeuibit2.ui.coindetail.CoinDetailViewModel
import org.jeonfeel.moeuibit2.data.remote.websocket.upbit.UpBitOrderBookWebSocket
import org.jeonfeel.moeuibit2.ui.coindetail.order.ui.AskingPriceLazyColumn
import org.jeonfeel.moeuibit2.ui.coindetail.order.ui.OrderScreenAskBid
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.utils.OnLifecycleEvent

@Composable
fun OrderScreen(
    coinDetailViewModel: CoinDetailViewModel = viewModel(),
) {
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> {
                coinDetailViewModel.cancelUpdateOrderBlockJob()
                if (coinDetailViewModel.rootExchange == ROOT_EXCHANGE_UPBIT) {
                    UpBitOrderBookWebSocket.currentScreen = IS_ANOTHER_SCREEN
                    UpBitOrderBookWebSocket.onPause()
                } else {
                    UpBitOrderBookWebSocket.currentScreen = IS_ANOTHER_SCREEN
                    UpBitOrderBookWebSocket.onPause()
                }
            }
            Lifecycle.Event.ON_RESUME -> {
                if(coinDetailViewModel.rootExchange == ROOT_EXCHANGE_UPBIT) {
                    UpBitOrderBookWebSocket.currentScreen = IS_DETAIL_SCREEN
                } else {
                    BitthumbOrderBookWebSocket.currentScreen = IS_DETAIL_SCREEN
                }
                coinDetailViewModel.initOrderScreen()
            }
            else -> {}
        }
    }

    Row(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        AskingPriceLazyColumn(
            Modifier
                .weight(3f)
                .fillMaxHeight(), coinDetailViewModel
        )
        Box(
            modifier = Modifier
                .weight(7f)
                .fillMaxHeight()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            OrderScreenAskBid(coinDetailViewModel)
        }
    }
}