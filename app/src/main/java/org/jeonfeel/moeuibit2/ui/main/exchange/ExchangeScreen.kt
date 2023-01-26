package org.jeonfeel.moeuibit2.ui.main.exchange

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.activities.MainActivity
import org.jeonfeel.moeuibit2.constants.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.constants.SELECTED_BTC_MARKET
import org.jeonfeel.moeuibit2.constants.SELECTED_FAVORITE
import org.jeonfeel.moeuibit2.constants.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.ui.custom.DpToSp
import org.jeonfeel.moeuibit2.ui.custom.clearFocusOnKeyboardDismiss
import org.jeonfeel.moeuibit2.ui.viewmodels.ExchangeViewModel
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.showToast

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ExchangeScreen(
    exchangeViewModel: ExchangeViewModel = viewModel(),
    startForActivityResult: ActivityResultLauncher<Intent>,
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState()

    AddLifecycleEvent(
        onPauseAction = {
            exchangeViewModel.updateExchange = false
            UpBitTickerWebSocket
                .getListener()
                .setTickerMessageListener(onTickerMessageReceiveListener = null)
            UpBitTickerWebSocket.onPause()
        },
        onResumeAction = {
            exchangeViewModel.initExchangeData()
        }
    )

    when (exchangeViewModel.exchangeState.loadingExchange.value) {
        true -> {
            ExchangeScreenLoading()
        }
        false -> {
            Exchange(
                exchangeViewModel = exchangeViewModel,
                startForActivityResult = startForActivityResult,
                pagerState = pagerState
            )
        }
    }
    ExchangeBackHandler(context)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun Exchange(
    exchangeViewModel: ExchangeViewModel,
    startForActivityResult: ActivityResultLauncher<Intent>,
    pagerState: PagerState
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (exchangeViewModel.exchangeState.error.value == INTERNET_CONNECTION) {
            SearchBasic(
                textFieldValueState = exchangeViewModel.exchangeState.searchTextFieldValue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .clearFocusOnKeyboardDismiss(),
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(25.dp),
                        tint = colorResource(id = R.color.C0F0F5C)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { it.invoke() }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(25.dp),
                            tint = colorResource(id = R.color.C0F0F5C)
                        )
                    }
                },
                placeholderText = stringResource(id = R.string.textFieldText),
                fontSize = DpToSp(dp = 17)
            )
            marketButtons(
                selectedMarketState = exchangeViewModel.exchangeState.selectedMarket,
                pagerState
            )
            SortButtons(
                sortButtonState = exchangeViewModel.exchangeState.sortButton,
                selectedMarketState = exchangeViewModel.exchangeState.selectedMarket,
                isUpdateExchange = exchangeViewModel.updateExchange,
                sortList = exchangeViewModel::sortList
            )
            ExchangeScreenLazyColumn(
                filteredExchangeCoinList = exchangeViewModel.getFilteredCoinList(),
                preCoinListAndPosition = exchangeViewModel.getPreCoinListAndPosition(),
                textFieldValueState = exchangeViewModel.exchangeState.searchTextFieldValue,
                favoriteHashMap = exchangeViewModel.favoriteHashMap,
                loadingFavorite = exchangeViewModel.getFavoriteLoadingState(),
                btcPrice = exchangeViewModel.exchangeState.btcPrice ,
                startForActivityResult = startForActivityResult
            )
            when (exchangeViewModel.exchangeState.selectedMarket.value) {
                SELECTED_KRW_MARKET -> {
                    exchangeViewModel.marketChangeAction(marketState = SELECTED_KRW_MARKET)
                }
                SELECTED_BTC_MARKET -> {
                    exchangeViewModel.marketChangeAction(marketState = SELECTED_BTC_MARKET)
                }
                else -> {
                    exchangeViewModel.marketChangeAction(marketState = SELECTED_FAVORITE)
                }
            }
        } else {
            ExchangeErrorScreen(
                checkErrorScreen = exchangeViewModel::checkErrorScreen
            )
        }
    }
}

@Composable
private fun ExchangeBackHandler(context: Context) {
    var backBtnTime = remember { 0L }
    BackHandler(true) {
        val curTime = System.currentTimeMillis()
        val gapTime = curTime - backBtnTime
        if (gapTime in 0..2000) {
            (context as MainActivity).finish()
        } else {
            backBtnTime = curTime
            context.showToast(context.getString(R.string.backPressText))
        }
    }
}