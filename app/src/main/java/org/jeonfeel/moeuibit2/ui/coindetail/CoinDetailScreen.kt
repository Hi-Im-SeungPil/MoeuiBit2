package org.jeonfeel.moeuibit2.ui.coindetail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.util.OnLifecycleEvent
import org.jeonfeel.moeuibit2.view.activity.coindetail.CoinDetailActivity
import org.jeonfeel.moeuibit2.viewmodel.CoinDetailViewModel

@Composable
fun CoinDetailScreen(
    coinKoreanName: String,
    coinSymbol: String,
    coinDetailViewModel: CoinDetailViewModel,
) {
    val context = LocalContext.current
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> UpBitTickerWebSocket.onPause()
            Lifecycle.Event.ON_RESUME -> {
                coinDetailViewModel.setWebSocketMessageListener()
                UpBitTickerWebSocket.coinDetailScreenOnResume("KRW-".plus(coinSymbol))
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CoinDetailTopAppBar(coinKoreanName = coinKoreanName, coinSymbol = coinSymbol)
        },
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
            CoinDetailMain(coinDetailViewModel.priceState.value,
                coinSymbol,
                coinDetailViewModel)
        }
        BackHandler(true) {
            (context as CoinDetailActivity).finish()
            context.overridePendingTransition(R.anim.none, R.anim.lazy_column_item_slide_right)
        }
    }
}