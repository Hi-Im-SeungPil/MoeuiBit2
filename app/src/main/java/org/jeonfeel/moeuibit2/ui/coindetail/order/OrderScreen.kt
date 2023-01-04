package org.jeonfeel.moeuibit2.ui.coindetail.order

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.ui.viewmodels.CoinDetailViewModel
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitOrderBookWebSocket
import org.jeonfeel.moeuibit2.utils.OnLifecycleEvent

@Composable
fun OrderScreen(
    coinDetailViewModel: CoinDetailViewModel = viewModel(),
) {
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> UpBitOrderBookWebSocket.onPause()
            Lifecycle.Event.ON_RESUME -> {
                coinDetailViewModel.setOrderBookWebSocketMessageListener()
                UpBitOrderBookWebSocket.onResume(coinDetailViewModel.market)
            }
            else -> {}
        }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        AskingPriceLazyColumn(Modifier
            .weight(3f)
            .fillMaxHeight(), coinDetailViewModel)
        Box(modifier = Modifier
            .weight(7f)
            .fillMaxHeight()) {
            OrderScreenAskBid(coinDetailViewModel)
        }
    }
}