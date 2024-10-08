package org.jeonfeel.moeuibit2.ui.main.exchange.newExchange

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.ui.AppScreen
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.CommonText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.SwipeDetector
import org.jeonfeel.moeuibit2.ui.common.clearFocusOnKeyboardDismiss
import org.jeonfeel.moeuibit2.ui.common.drawUnderLine
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.main.exchange.NoRippleTheme
import org.jeonfeel.moeuibit2.ui.main.exchange.TickerAskBidState
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortOrder
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortType
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedFluctuateString
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedString
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringForBtc
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedUnitString
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedUnitStringForBtc
import org.jeonfeel.moeuibit2.utils.FallColor
import org.jeonfeel.moeuibit2.utils.RiseColor
import org.jeonfeel.moeuibit2.utils.getFluctuateColor
import org.jeonfeel.moeuibit2.utils.isTradeCurrencyKrw
import java.math.BigDecimal

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ExchangeScreen(
    tickerList: List<CommonExchangeModel>,
    isUpdateExchange: State<Boolean>,
    sortTickerList: (targetTradeCurrency: Int?, sortType: SortType, sortOrder: SortOrder) -> Unit,
    tradeCurrencyState: State<Int>,
    changeTradeCurrency: (tradeCurrency: Int) -> Unit,
    onResume: () -> Unit,
    onPaused: () -> Unit,
    needAnimationList: List<State<String>>,
    stopAnimation: (market: String) -> Unit,
    btcKrwPrice: BigDecimal,
    appNavController: NavHostController
) {
    val state = rememberExchangeStateHolder(
        isUpdateExchange = isUpdateExchange.value,
        sortTickerList = sortTickerList,
        changeTradeCurrency = changeTradeCurrency,
        tradeCurrencyState = tradeCurrencyState
    )

    AddLifecycleEvent(
        onPauseAction = {
            onPaused()
        },
        onResumeAction = {
            onResume()
        }
    )

    Column(modifier = Modifier.fillMaxSize()) {
        SearchSection(
            textFieldValueState = state.textFieldValueState,
            focusManager = state.focusManaManager,
            modifier = Modifier
                .background(color = Color.White)
                .zIndex(1f)
        )
        CollapsingToolbarScaffold(
            state = rememberCollapsingToolbarScaffoldState(),
            scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed,
            toolbar = {
                SelectTradeCurrencySection(
                    pagerState = state.pagerState,
                    modifier = Modifier.zIndex(-1f),
                    tradeCurrencyState = tradeCurrencyState,
                    changeTradeCurrency = state::changeTradeCurrencyAction,
                    coroutineScope = state.coroutineScope
                )
            },
            modifier = Modifier
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                SortingSection(
                    sortOrder = state.sortOrder.value,
                    onSortClick = state::onSortClick,
                    selectedSortType = state.selectedSortType
                )
                CoinTickerSection(
                    lazyScrollState = state.lazyScrollState,
                    tickerList = state.getFilteredList(tickerList = tickerList),
                    needAnimationList = needAnimationList,
                    stopAnimation = stopAnimation,
                    btcKrwPrice = btcKrwPrice,
                    coinTickerListSwipeAction = state::coinTickerListSwipeAction,
                    pagerState = state.pagerState,
                    appNavController = appNavController
                )
            }
        }
    }
}

@Composable
private fun SearchSection(
    textFieldValueState: MutableState<String>,
    focusManager: FocusManager,
    modifier: Modifier = Modifier
) {
    val hintFocusState: MutableState<Boolean> = remember { mutableStateOf(false) }

    BasicTextField(value = textFieldValueState.value, onValueChange = {
        textFieldValueState.value = it
    }, singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp)
            .clearFocusOnKeyboardDismiss()
            .onFocusChanged { focusState ->
                hintFocusState.value = focusState.isFocused
            },
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.primary,
            fontSize = DpToSp(17.dp)
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(25.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Box(Modifier.weight(1f)) {
                    if (textFieldValueState.value.isEmpty() && !hintFocusState.value) {
                        CommonText(
                            stringResource(id = R.string.textFieldText),
                            textStyle = LocalTextStyle.current.copy(
                                color = Color(0xff8f9297),
                            ),
                            fontSize = 17.dp,
                        )
                    }
                    innerTextField()
                }
                if (textFieldValueState.value.isNotEmpty()) {
                    IconButton(onClick = {
                        textFieldValueState.value = ""
                        focusManager.clearFocus(true)
                    }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(25.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        })
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun SelectTradeCurrencySection(
    tradeCurrencyState: State<Int>,
    pagerState: PagerState,
    tabTitleList: Array<String> = stringArrayResource(id = R.array.exchange_screen_tab_list),
    modifier: Modifier = Modifier,
    changeTradeCurrency: (Int) -> Unit,
    coroutineScope: CoroutineScope
) {
    CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
        Row(
            modifier
                .fillMaxWidth()
                .drawUnderLine(Color(0xfff0f0f2), strokeWidth = 10f)
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                        color = Color.Transparent
                    )
                },
                modifier = Modifier
                    .weight(3f),
                backgroundColor = MaterialTheme.colorScheme.background,
                divider = {}
            ) {
                tabTitleList.forEachIndexed { index, title ->
                    Tab(
                        text = {
                            Text(
                                text = title,
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                ),
                                fontSize = DpToSp(dp = 17.dp),
                                modifier = Modifier.weight(1f)
                            )
                        },
                        selectedContentColor = Color.Black,
                        unselectedContentColor = Color(0xff8C939E),
                        selected = tradeCurrencyState.value == index,
                        onClick = {
                            if (tradeCurrencyState.value != index) {
                                coroutineScope.launch {
                                    changeTradeCurrency(index)
                                }
                            }
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun SortingSection(
    sortOrder: SortOrder,
    onSortClick: (sortType: SortType) -> Unit,
    selectedSortType: MutableState<SortType>
) {
    Row(
        modifier = Modifier
            .drawUnderLine(Color(0xfff0f0f2), strokeWidth = 10f)
            .padding(top = 4.dp, bottom = 6.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        SortButton(
            text = "현재가",
            sortType = SortType.PRICE,
            sortOrder = sortOrder,
            onSortClick = onSortClick,
            selectedSortType = selectedSortType
        )
        SortButton(
            text = "변동",
            sortType = SortType.RATE,
            sortOrder = sortOrder,
            onSortClick = onSortClick,
            selectedSortType = selectedSortType
        )
        SortButton(
            text = "거래량",
            sortType = SortType.VOLUME,
            sortOrder = sortOrder,
            onSortClick = onSortClick,
            selectedSortType = selectedSortType
        )
    }
}

@Composable
private fun RowScope.SortButton(
    text: String,
    sortType: SortType,
    sortOrder: SortOrder,
    selectedSortType: MutableState<SortType>,
    onSortClick: (SortType) -> Unit
) {
    Row(
        modifier = Modifier
            .weight(1f)
            .noRippleClickable {
                onSortClick(sortType)
            }, verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = text,
            modifier = Modifier,
            style = TextStyle(
                fontSize = DpToSp(dp = 15.dp), textAlign = TextAlign.Center, color =
                if (selectedSortType.value == sortType) Color(0xff2454E6) else Color(0xff91959E)
            )
        )
        Column(modifier = Modifier.padding(start = 4.dp)) {
            Text(
                text = "▲",
                fontSize = DpToSp(dp = 6.dp),
                style = TextStyle(
                    color = if (selectedSortType.value == sortType && sortOrder == SortOrder.ASCENDING) Color(
                        0xff2454E6
                    ) else Color(0xff91959E)
                )
            )
            Text(
                text = "▼",
                fontSize = DpToSp(dp = 6.dp),
                style = TextStyle(
                    color = if (selectedSortType.value == sortType && sortOrder == SortOrder.DESCENDING) Color(
                        0xff2454E6
                    ) else Color(0xff91959E)
                )
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun CoinTickerSection(
    tickerList: List<CommonExchangeModel>,
    lazyScrollState: LazyListState,
    stopAnimation: (market: String) -> Unit,
    needAnimationList: List<State<String>>,
    btcKrwPrice: BigDecimal,
    coinTickerListSwipeAction: (isSwipeLeft: Boolean) -> Unit,
    pagerState: PagerState,
    appNavController: NavHostController
) {
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .SwipeDetector(
            onSwipeLeftAction = {
                coinTickerListSwipeAction(true)
            },
            onSwipeRightAction = {
                coinTickerListSwipeAction(false)
            }
        ), state = lazyScrollState) {
        itemsIndexed(items = tickerList) { index, item ->
            CoinTickerView(
                name = item.koreanName,
                symbol = item.symbol,
                lastPrice = item.tradePrice.formattedString(),
                fluctuateRate = item.signedChangeRate.toFloat(),
                fluctuatePrice = item.signedChangePrice.toFloat(),
                acc24h = item.accTradePrice24h.formattedUnitString(),
                onClickEvent = {
                    val market = item.market
                    val warning = item.warning
                    appNavController.navigate("${AppScreen.CoinDetail.name}/$market/$warning")
                },
                needAnimation = if (needAnimationList.isNotEmpty()) needAnimationList[index].value else TickerAskBidState.NONE.name,
                market = item.market,
                stopAnimation = stopAnimation,
                btcPriceMultiplyCoinPrice = item.tradePrice.multiply(btcKrwPrice)
                    .formattedStringForBtc(),
                btcPriceMultiplyAcc = item.accTradePrice24h.multiply(btcKrwPrice)
                    .formattedUnitStringForBtc()
            )
        }
    }
}

@Composable
fun CoinTickerView(
    modifier: Modifier = Modifier,
    name: String,
    symbol: String,
    lastPrice: String,
    fluctuateRate: Float,
    fluctuatePrice: Float,
    acc24h: String,
    onClickEvent: () -> Unit,
    infiniteTransition: InfiniteTransition = rememberInfiniteTransition(label = ""),
    needAnimation: String,
    stopAnimation: (market: String) -> Unit,
    market: String,
    btcPriceMultiplyCoinPrice: String,
    btcPriceMultiplyAcc: String,
) = Column {
    Row(
        modifier = modifier
            .clickable { onClickEvent.invoke() }
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val animationDurationTimeMills = 200
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.0f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = animationDurationTimeMills
                    0.5f at 100
                },
                repeatMode = RepeatMode.Reverse
            ),
            label = "animation alpha"
        )

        LaunchedEffect(key1 = needAnimation) {
            if (needAnimation != TickerAskBidState.NONE.name) {
                delay(animationDurationTimeMills.toLong())
                stopAnimation(market)
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = name,
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center,
                fontSize = DpToSp(13.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(fontWeight = FontWeight.W600)
            )
            Text(
                modifier = Modifier.fillMaxSize(),
                text = symbol,
                textAlign = TextAlign.Center,
                fontSize = DpToSp(11.dp),
                style = TextStyle(
                    color = Color(0xff8C939E)
                )
            )
        }
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            AutoSizeText(
                text = lastPrice,
                modifier = Modifier
                    .background(Color.White)
                    .drawBehind {
                        drawLine(
                            color = when (needAnimation) {
                                TickerAskBidState.ASK.name -> {
                                    FallColor.copy(alpha = alpha)
                                }

                                TickerAskBidState.BID.name -> {
                                    RiseColor.copy(alpha = alpha)
                                }

                                else -> {
                                    Color.White
                                }
                            },
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = if (needAnimation != TickerAskBidState.NONE.name) 2.dp.toPx() else 0.dp.toPx()
                        )
                    }
                    .padding(2.dp),
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = DpToSp(14.dp),
                    fontWeight = FontWeight.W400,
                ),
                color = fluctuatePrice.getFluctuateColor()
            )
            if (!market.isTradeCurrencyKrw()) {
                AutoSizeText(
                    text = "$btcPriceMultiplyCoinPrice KRW", modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        textAlign = TextAlign.End,
                        fontSize = DpToSp(11.dp)
                    ),
                    color = Color(0xff91959E)
                )
            }
        }
        AutoSizeText(
            text = fluctuateRate.formattedFluctuateString() + "%",
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = DpToSp(14.dp),
                fontWeight = FontWeight.W400,
            ),
            color = fluctuatePrice.getFluctuateColor(),
            modifier = Modifier.weight(1f)
        )
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            AutoSizeText(
                text = acc24h,
                textStyle = TextStyle(
                    fontSize = DpToSp(14.dp),
                    fontWeight = FontWeight.W400,
                    textAlign = TextAlign.Center
                )
            )
            if (!market.isTradeCurrencyKrw()) {
                AutoSizeText(
                    text = btcPriceMultiplyAcc,
                    textStyle = TextStyle(
                        fontSize = DpToSp(11.dp),
                        textAlign = TextAlign.End
                    ),
                    color = Color(0xff91959E)
                )
            }
        }
    }
    Divider(
        modifier = Modifier
            .padding(horizontal = 9.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(color = Color(0xfff0f0f2))
    )
}