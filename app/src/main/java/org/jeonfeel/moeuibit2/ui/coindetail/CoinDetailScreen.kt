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
import org.jeonfeel.moeuibit2.activity.coindetail.CoinDetailActivity
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.CoinDetailViewModel
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitCoinDetailWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitOrderBookWebSocket
import org.jeonfeel.moeuibit2.ui.common.OneButtonCommonDialog
import org.jeonfeel.moeuibit2.util.OnLifecycleEvent

@Composable
fun CoinDetailScreen(
    coinKoreanName: String,
    coinSymbol: String,
    warning: String,
    coinDetailViewModel: CoinDetailViewModel = viewModel(),
) {
    val context = LocalContext.current
    val market = coinDetailViewModel.market

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> {
                UpBitCoinDetailWebSocket.onPause()
                UpBitOrderBookWebSocket.onPause()
            }
            Lifecycle.Event.ON_RESUME -> {
                coinDetailViewModel.initOrderScreen()
            }
            else -> {}
        }
    }

    OneButtonCommonDialog(dialogState = coinDetailViewModel.errorDialogState,
        title = "네트워크 오류",
        content = "네트워크 연결 상태를 확인해 주세요.",
        buttonText = "확인",
        buttonAction = {

            coinDetailViewModel.errorDialogState.value = false })

    Scaffold(
        topBar = {
            CoinDetailTopAppBar(coinKoreanName = coinKoreanName,
                coinSymbol = coinSymbol,
                warning = warning)
        },
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
            CoinDetailMain(coinDetailViewModel.currentTradePriceState,
                coinSymbol,
                coinDetailViewModel)
        }

        BackHandler(true) {
            val intent = Intent()
            intent.putExtra("market", market.plus(coinSymbol))
            intent.putExtra("isFavorite", coinDetailViewModel.favoriteMutableState.value)
            (context as CoinDetailActivity).setResult(-1, intent)
            (context).finish()
            context.overridePendingTransition(R.anim.none, R.anim.lazy_column_item_slide_right)
        }
    }
}