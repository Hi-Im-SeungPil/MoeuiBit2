package org.jeonfeel.moeuibit2.ui.coindetail

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.activities.CoinDetailActivity
import org.jeonfeel.moeuibit2.ui.viewmodels.CoinDetailViewModel
import org.jeonfeel.moeuibit2.constants.INTENT_IS_FAVORITE
import org.jeonfeel.moeuibit2.constants.INTENT_MARKET
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.ui.common.CommonLoadingDialog
import org.jeonfeel.moeuibit2.ui.common.OneButtonCommonDialog
import org.jeonfeel.moeuibit2.utils.OnLifecycleEvent

@Composable
fun CoinDetailScreen(
    coinKoreanName: String,
    coinSymbol: String,
    warning: String,
    coinDetailViewModel: CoinDetailViewModel = viewModel(),
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
                UpBitTickerWebSocket.onPause()
                Logger.d("coinDetailScreen on pause")
            }
            Lifecycle.Event.ON_RESUME -> {
                coinDetailViewModel.initCoinDetailScreen()
                Logger.d("coinDetailScreen on resume")
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
        Box(modifier = Modifier.padding(contentPadding)) {
            CoinDetailMain(
                currentPrice = coinDetailViewModel.coinOrder.state.currentTradePriceState.value,
                symbol = coinSymbol,
                coinDetailViewModel = coinDetailViewModel
            )
        }

        BackHandler(true) {
            val intent = Intent()
            intent.putExtra(INTENT_MARKET, market.substring(0, 4).plus(coinSymbol))
            intent.putExtra(INTENT_IS_FAVORITE, coinDetailViewModel.favoriteMutableState.value)
            (context as CoinDetailActivity).setResult(-1, intent)
            (context).finish()
            context.overridePendingTransition(R.anim.none, R.anim.lazy_column_item_slide_right)
        }
    }
}