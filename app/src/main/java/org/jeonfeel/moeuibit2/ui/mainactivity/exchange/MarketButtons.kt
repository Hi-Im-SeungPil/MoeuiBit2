package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.main.viewmodel.MainViewModel
import org.jeonfeel.moeuibit2.constant.SELECTED_BTC_MARKET
import org.jeonfeel.moeuibit2.constant.SELECTED_FAVORITE
import org.jeonfeel.moeuibit2.constant.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.constant.ioDispatcher
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.ui.util.drawUnderLine

@OptIn(ExperimentalPagerApi::class)
@Composable
fun marketButtons2(
    mainViewModel: MainViewModel,
    startForActivityResult: ActivityResultLauncher<Intent>,
) {
    val tabTitleList = listOf(
        stringResource(id = R.string.krw),
        stringResource(id = R.string.btc),
        stringResource(id = R.string.favorite),
    )
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    Column(modifier = Modifier.fillMaxSize()) {
        Row(Modifier
            .fillMaxWidth()
            .drawUnderLine(lineColor = Color.DarkGray)) {
            CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                        )
                    },
                    modifier = Modifier
                        .weight(3f),
                    backgroundColor = Color.White,
                    divider = {}
                ) {
                    tabTitleList.forEachIndexed { index, title ->
                        Tab(
                            text = {
                                Text(text = title,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                )
                            },
                            selectedContentColor = colorResource(R.color.C0F0F5C),
                            unselectedContentColor = Color.LightGray,
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    mainViewModel.selectedMarketState.value = index
                                    mainViewModel.updateExchange = false
                                    if (pagerState.currentPage == SELECTED_FAVORITE) {
                                        mainViewModel.requestFavoriteData()
                                    }
                                    pagerState.scrollToPage(index)
                                }
                            },
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        SortButtons(mainViewModel)

        HorizontalPager(
            count = tabTitleList.size,
            state = pagerState,
        ) { page ->
            when (page) {
                SELECTED_KRW_MARKET -> {
                    if (UpBitTickerWebSocket.currentMarket != SELECTED_KRW_MARKET) {
                        mainViewModel.updateExchange = false
                        UpBitTickerWebSocket.getListener().setTickerMessageListener(null)
                        UpBitTickerWebSocket.onPause()
                        mainViewModel.sortList()
                        mainViewModel.requestExchangeData()
                    }
                    mainViewModel.selectedMarketState.value = pagerState.currentPage
                    ExchangeScreenLazyColumn(mainViewModel, startForActivityResult)
                }
                SELECTED_BTC_MARKET -> {
                    if (UpBitTickerWebSocket.currentMarket != SELECTED_BTC_MARKET) {
                        UpBitTickerWebSocket.getListener().setTickerMessageListener(null)
                        UpBitTickerWebSocket.onPause()
                        mainViewModel.updateExchange = false
                        mainViewModel.sortList()
                        mainViewModel.requestExchangeData()
                    }
                    mainViewModel.selectedMarketState.value = pagerState.currentPage
                    BtcExchangeScreenLazyColumn(mainViewModel, startForActivityResult)
                }
                SELECTED_FAVORITE -> {
                    if (pagerState.currentPage == SELECTED_FAVORITE) {
                        CoroutineScope(ioDispatcher).launch {
                            if (UpBitTickerWebSocket.currentMarket != SELECTED_FAVORITE) {
                                UpBitTickerWebSocket.getListener().setTickerMessageListener(null)
                                UpBitTickerWebSocket.onPause()
                                mainViewModel.updateExchange = false
                                mainViewModel.requestFavoriteData()
                            }
                        }
                        mainViewModel.selectedMarketState.value = pagerState.currentPage
                        FavoriteExchangeScreenLazyColumn(mainViewModel, startForActivityResult)
                    }
                }
            }
        }
    }
}


/**
 * 거래소 화면에 원화, 관심, btc 버튼
 */
@Composable
fun MarketButtons(mainViewModel: MainViewModel = viewModel()) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .drawUnderLine(lineColor = Color.DarkGray)
    ) {
        MarketButton(interactionSource,
            mainViewModel,
            stringResource(id = R.string.krw),
            SELECTED_KRW_MARKET)
        MarketButton(interactionSource,
            mainViewModel,
            stringResource(id = R.string.btc),
            SELECTED_BTC_MARKET)
        MarketButton(interactionSource,
            mainViewModel,
            stringResource(id = R.string.favorite),
            SELECTED_FAVORITE)
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight()
        ) {
            Text(text = "")
        }
    }
}

/**
 * 원화 btc 관심 버튼 동작.
 */
@Composable
fun RowScope.MarketButton(
    interactionSource: MutableInteractionSource,
    mainViewModel: MainViewModel,
    text: String,
    buttonId: Int,
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .wrapContentHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                mainViewModel.selectedMarketState.value = buttonId
//                if(buttonId == SELECTED_FAVORITE) {
//                    mainViewModel.sortButtonState.value = -1
//                }
                mainViewModel.sortList()
            }
    ) {
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                color = getTextColor(
                    mainViewModel,
                    buttonId
                ), fontSize = 17.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
fun getTextColor(mainViewModel: MainViewModel = viewModel(), buttonId: Int): Color {
    return if (mainViewModel.selectedMarketState.value == buttonId) {
        colorResource(R.color.C0F0F5C)
    } else {
        Color.LightGray
    }
}

private object NoRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = Color.Unspecified

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(0.0f,0.0f,0.0f,0.0f)
}