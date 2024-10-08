package org.jeonfeel.moeuibit2.ui.coindetail

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.network.websocket.bitthumb.BitthumbTickerWebSocket
import org.jeonfeel.moeuibit2.data.network.websocket.upbit.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.ui.activities.CoinDetailActivity
import org.jeonfeel.moeuibit2.ui.common.OneButtonCommonDialog
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.utils.OnLifecycleEvent

@Composable
fun CoinDetailScreen(
    coinKoreanName: String,
    coinSymbol: String,
    warning: String,
    coinDetailViewModel: CoinDetailViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val market = coinDetailViewModel.market
    OneButtonCommonDialog(dialogState = coinDetailViewModel.coinOrder.state.errorDialogState,
        title = stringResource(id = R.string.NETWORK_ERROR_TITLE),
        content = stringResource(id = R.string.NO_INTERNET_CONNECTION),
        buttonText = stringResource(id = R.string.confirm),
        buttonAction = {
            coinDetailViewModel.coinOrder.state.errorDialogState.value = false
        })

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> {
                if (coinDetailViewModel.rootExchange == ROOT_EXCHANGE_UPBIT) {
                    UpBitTickerWebSocket.currentPage = IS_ANOTHER_SCREEN
                    UpBitTickerWebSocket.onPause()
                } else if (coinDetailViewModel.rootExchange == ROOT_EXCHANGE_BITTHUMB) {
                    BitthumbTickerWebSocket.currentScreen = IS_ANOTHER_SCREEN
                    BitthumbTickerWebSocket.onPause()
                }
            }

            Lifecycle.Event.ON_RESUME -> {
                if (coinDetailViewModel.rootExchange == ROOT_EXCHANGE_UPBIT) {
                    UpBitTickerWebSocket.currentPage = IS_DETAIL_SCREEN
                } else if (coinDetailViewModel.rootExchange == ROOT_EXCHANGE_BITTHUMB) {
                    BitthumbTickerWebSocket.currentScreen = IS_DETAIL_SCREEN
                }
                coinDetailViewModel.initCoinDetailScreen()
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CoinDetailTopAppBar(
                coinKoreanName = coinKoreanName,
                coinSymbol = coinSymbol,
                warning = warning
            )
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            CoinDetailMain(
                currentPrice = coinDetailViewModel.coinOrder.state.currentTradePriceState.value,
                symbol = coinSymbol,
                coinDetailViewModel = coinDetailViewModel
            )
        }

        BackHandler(true) {
            val intent = Intent()
            intent.putExtra(KeyConst.INTENT_MARKET, market.substring(0, 4).plus(coinSymbol))
            intent.putExtra(KeyConst.INTENT_IS_FAVORITE, coinDetailViewModel.favoriteMutableState.value)
            (context as CoinDetailActivity).setResult(-1, intent)
            (context).finish()
            context.overridePendingTransition(R.anim.none, R.anim.lazy_column_item_slide_right)
        }
    }
}