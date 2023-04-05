package org.jeonfeel.moeuibit2.ui.main.exchange

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.ui.activities.MainActivity
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.ui.custom.DpToSp
import org.jeonfeel.moeuibit2.ui.custom.clearFocusOnKeyboardDismiss
import org.jeonfeel.moeuibit2.ui.viewmodels.ExchangeViewModel
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.showToast
import kotlin.system.exitProcess

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ExchangeScreen(
    exchangeViewModel: ExchangeViewModel = viewModel(),
    errorState: MutableState<Int>,
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState()
    val startForActivityResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultData = result.data
                if (resultData != null) {
                    val isFavorite = resultData.getBooleanExtra(INTENT_IS_FAVORITE, false)
                    val market = resultData.getStringExtra(INTENT_MARKET) ?: ""
                    exchangeViewModel.updateFavorite(market = market, isFavorite = isFavorite)
                }
            }
        }

    AddLifecycleEvent(
        onPauseAction = {
            exchangeViewModel.updateExchange = false
            UpBitTickerWebSocket.onPause()
        },
        onResumeAction = {
            exchangeViewModel.initExchangeData()
        }
    )

    when (exchangeViewModel.state.loadingExchange.value) {
        true -> {
            ExchangeScreenLoading()
        }
        false -> {
            Exchange(
                exchangeViewModel = exchangeViewModel,
                startForActivityResult = startForActivityResult,
                pagerState = pagerState,
                errorState = errorState
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
    pagerState: PagerState,
    errorState: MutableState<Int>
) {
    val scrollState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        if (errorState.value == INTERNET_CONNECTION) {
            SearchBasic(
                textFieldValueState = exchangeViewModel.state.searchTextFieldValue,
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
                        tint = MaterialTheme.colorScheme.onBackground
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
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                placeholderText = stringResource(id = R.string.textFieldText),
                fontSize = DpToSp(dp = 17.dp)
            )
            marketButtons(
                selectedMarketState = exchangeViewModel.state.selectedMarket,
                pagerState
            )
            SortButtons(
                sortButtonState = exchangeViewModel.state.sortButton,
                selectedMarketState = exchangeViewModel.state.selectedMarket,
                isUpdateExchange = exchangeViewModel.updateExchange,
                sortList = exchangeViewModel::sortList
            )
            ExchangeScreenLazyColumn(
                filteredExchangeCoinList = exchangeViewModel.getFilteredCoinList(),
                preCoinListAndPosition = exchangeViewModel.getPreCoinListAndPosition(),
                textFieldValueState = exchangeViewModel.state.searchTextFieldValue,
                loadingFavorite = exchangeViewModel.getFavoriteLoadingState(),
                btcPrice = exchangeViewModel.state.btcPrice,
                startForActivityResult = startForActivityResult,
                scrollState = scrollState
            )
            LaunchedEffect(key1 = exchangeViewModel.state.selectedMarket.value) {
                when (exchangeViewModel.state.selectedMarket.value) {
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
                scrollState.scrollToItem(index = 0)
            }
        } else {
            exchangeViewModel.updateExchange = false
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
            (context as MainActivity).moveTaskToBack(true)
            context.finishAndRemoveTask()
            exitProcess(0)
        } else {
            backBtnTime = curTime
            context.showToast(context.getString(R.string.backPressText))
        }
    }
}