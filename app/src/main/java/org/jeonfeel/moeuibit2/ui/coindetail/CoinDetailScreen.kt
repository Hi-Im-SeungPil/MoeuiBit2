package org.jeonfeel.moeuibit2.ui.coindetail

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.util.OnLifecycleEvent
import org.jeonfeel.moeuibit2.view.activity.coindetail.CoinDetailActivity
import org.jeonfeel.moeuibit2.viewmodel.CoinDetailViewModel

@Composable
fun CoinDetailScreen(
    coinKoreanName: String,
    coinSymbol: String,
    coinDetailViewModel: CoinDetailViewModel = viewModel(),
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
            CoinDetailMain(coinDetailViewModel.currentTradePriceState.value,
                coinSymbol,
                coinDetailViewModel)
        }
        BackHandler(true) {
            val intent = Intent()
            intent.putExtra("market","KRW-".plus(coinSymbol))
            intent.putExtra("isFavorite",coinDetailViewModel.favoriteMutableState.value)
            (context as CoinDetailActivity).setResult(-1,intent)
            (context).finish()
            context.overridePendingTransition(R.anim.none, R.anim.lazy_column_item_slide_right)
        }
    }
}