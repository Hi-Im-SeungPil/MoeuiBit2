package org.jeonfeel.moeuibit2.ui.main.exchange

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.CommonText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.SwipeDetector
import org.jeonfeel.moeuibit2.ui.common.clearFocusOnKeyboardDismiss
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortOrder
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortType
import org.jeonfeel.moeuibit2.ui.nav.AppScreen
import org.jeonfeel.moeuibit2.ui.theme.newtheme.APP_PRIMARY_COLOR
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonDividerColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonHintTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonRiseColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.exchange.exchangeBTCtoKRWColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.exchange.exchangeCautionColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.exchange.exchangeCoinListDividerColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.exchange.exchangeSortColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.exchange.exchangeUnselectSortColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.exchange.exchangeWarningColor
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedFluctuateString
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedString
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringForBtc
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedUnitString
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedUnitStringForBtc
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.ext.FallColor
import org.jeonfeel.moeuibit2.utils.ext.RiseColor
import org.jeonfeel.moeuibit2.utils.ext.getFluctuateColor
import org.jeonfeel.moeuibit2.utils.isTradeCurrencyKrw
import java.math.BigDecimal
import kotlin.reflect.KFunction1

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ExchangeScreen(
    tickerList: List<CommonExchangeModel>,
    isUpdateExchange: State<Boolean>,
    sortTickerList: (targetTradeCurrency: Int?, sortType: SortType, sortOrder: SortOrder) -> Unit,
    tradeCurrencyState: State<Int>,
    changeTradeCurrency: (tradeCurrency: Int) -> Unit,
    btcKrwPrice: BigDecimal,
    appNavController: NavHostController,
    selectedSortType: State<SortType>,
    sortOrder: State<SortOrder>,
    updateSortType: KFunction1<SortType, Unit>,
    updateSortOrder: KFunction1<SortOrder, Unit>,
    textFieldValueState: State<String>,
    updateTextFieldValue: KFunction1<String, Unit>,
) {

    val stateHolder = rememberExchangeStateHolder(
        isUpdateExchange = isUpdateExchange.value,
        sortTickerList = sortTickerList,
        changeTradeCurrency = changeTradeCurrency,
        selectedSortType = selectedSortType,
        sortOrder = sortOrder,
        tradeCurrencyState = tradeCurrencyState,
        updateSortType = updateSortType,
        updateSortOrder = updateSortOrder,
        textFieldValueState = textFieldValueState
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = commonBackground())
    ) {
        SearchSection(
            textFieldValueState = textFieldValueState,
            focusManager = stateHolder.focusManaManager,
            modifier = Modifier
                .zIndex(1f),
            updateTextFieldValue = updateTextFieldValue
        )

        CollapsingToolbarScaffold(
            state = stateHolder.toolbarState,
            scrollStrategy = ScrollStrategy.EnterAlways,
            toolbar = {
                SelectTradeCurrencySection(
                    pagerState = stateHolder.pagerState,
                    modifier = Modifier
                        .zIndex(1f),
                    tradeCurrencyState = tradeCurrencyState,
                    changeTradeCurrency = stateHolder::changeTradeCurrencyAction,
                    coroutineScope = stateHolder.coroutineScope
                )
            },
            modifier = Modifier
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Divider(modifier = Modifier.height(2.dp), color = commonDividerColor())
                SortingSection(
                    sortOrder = sortOrder.value,
                    onSortClick = stateHolder::onSortClick,
                    selectedSortType = selectedSortType
                )
                Divider(modifier = Modifier.height(2.dp), color = commonDividerColor())
                CoinTickerSection(
                    lazyScrollState = stateHolder.lazyScrollState,
                    tickerList = stateHolder.getFilteredList(tickerList = tickerList),
                    btcKrwPrice = btcKrwPrice,
                    coinTickerListSwipeAction = stateHolder::coinTickerListSwipeAction,
                    pagerState = stateHolder.pagerState,
                    appNavController = appNavController,
                    textFieldValue = textFieldValueState
                )
            }
        }
    }
}

@Composable
private fun SearchSection(
    textFieldValueState: State<String>,
    focusManager: FocusManager,
    modifier: Modifier = Modifier,
    updateTextFieldValue: KFunction1<String, Unit>,
) {
    val hintFocusState: MutableState<Boolean> = remember { mutableStateOf(false) }

    BasicTextField(value = textFieldValueState.value, onValueChange = {
        updateTextFieldValue(it)
    }, singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp)
            .clearFocusOnKeyboardDismiss()
            .onFocusChanged { focusState ->
                hintFocusState.value = focusState.isFocused
            },
        textStyle = TextStyle(
            color = commonTextColor(),
            fontSize = DpToSp(17.dp)
        ),
        cursorBrush = SolidColor(commonTextColor()),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .fillMaxWidth()
                    .height(45.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp),
                    tint = commonTextColor()
                )
                Box(
                    Modifier
                        .padding(start = 15.dp)
                        .weight(1f)
                ) {
                    if (textFieldValueState.value.isEmpty() && !hintFocusState.value) {
                        CommonText(
                            stringResource(id = R.string.textFieldText),
                            textStyle = LocalTextStyle.current.copy(
                                color = commonHintTextColor(),
                            ),
                            fontSize = 17.dp,
                        )
                    }
                    innerTextField()
                }
                if (textFieldValueState.value.isNotEmpty()) {
                    IconButton(onClick = {
                        updateTextFieldValue("")
                        focusManager.clearFocus(true)
                    }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(25.dp),
                            tint = commonTextColor()
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
    coroutineScope: CoroutineScope,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = Modifier.height(0.dp))
    Row(
        modifier.fillMaxWidth()
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
            backgroundColor = commonBackground(),
            divider = {}
        ) {
            tabTitleList.forEachIndexed { index, title ->
                Tab(
                    interactionSource = object : MutableInteractionSource {
                        override val interactions: Flow<Interaction> = emptyFlow()
                        override suspend fun emit(interaction: Interaction) {}
                        override fun tryEmit(interaction: Interaction) = true
                    },
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
                    selectedContentColor = commonTextColor(),
                    unselectedContentColor = Color(0xff8C939E),
                    selected = tradeCurrencyState.value == index,
                    onClick = {
                        if (tradeCurrencyState.value != index) {
                            coroutineScope.launch {
                                keyboardController?.hide()
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

@Composable
private fun SortingSection(
    sortOrder: SortOrder,
    onSortClick: (sortType: SortType) -> Unit,
    selectedSortType: State<SortType>,
) {
    Row(
        modifier = Modifier
            .padding(top = 4.dp, bottom = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically), horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .background(color = exchangeWarningColor(), shape = CircleShape)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = " 유의",
                modifier = Modifier.align(Alignment.CenterVertically),
                style = TextStyle(color = exchangeWarningColor(), fontSize = DpToSp(dp = 11.dp))
            )
            Spacer(modifier = Modifier.size(5.dp))
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .align(Alignment.CenterVertically)
                    .background(color = exchangeCautionColor(), shape = CircleShape)
            )
            Text(
                text = " 주의",
                modifier = Modifier.align(Alignment.CenterVertically),
                style = TextStyle(color = exchangeCautionColor(), fontSize = DpToSp(dp = 11.dp))
            )
        }
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
    selectedSortType: State<SortType>,
    onSortClick: (SortType) -> Unit,
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
                if (selectedSortType.value == sortType) APP_PRIMARY_COLOR
                else commonHintTextColor()
            )
        )
        Column(modifier = Modifier.padding(start = 4.dp)) {
            Text(
                text = "▲",
                fontSize = DpToSp(dp = 6.dp),
                style = TextStyle(
                    color = if (selectedSortType.value == sortType && sortOrder == SortOrder.ASCENDING) APP_PRIMARY_COLOR
                    else commonHintTextColor()
                )
            )
            Text(
                text = "▼",
                fontSize = DpToSp(dp = 6.dp),
                style = TextStyle(
                    color = if (selectedSortType.value == sortType && sortOrder == SortOrder.DESCENDING) APP_PRIMARY_COLOR
                    else commonHintTextColor()
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
    btcKrwPrice: BigDecimal,
    coinTickerListSwipeAction: (isSwipeLeft: Boolean) -> Unit,
    pagerState: PagerState,
    appNavController: NavHostController,
    textFieldValue: State<String>,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .SwipeDetector(
            onSwipeLeftAction = {
                keyboardController?.hide()
                coinTickerListSwipeAction(true)
            },
            onSwipeRightAction = {
                keyboardController?.hide()
                coinTickerListSwipeAction(false)
            }
        ), state = lazyScrollState) {
        itemsIndexed(items = tickerList, key = { _, item -> item.market }) { index, item ->
            if (index < tickerList.size) {
                CoinTickerView(
                    name = item.koreanName,
                    symbol = item.symbol,
                    lastPrice = item.tradePrice.formattedString(),
                    fluctuateRate = item.signedChangeRate.toFloat(),
                    fluctuatePrice = item.signedChangePrice.toFloat(),
                    acc24h = item.accTradePrice24h.formattedUnitString(),
                    needAnimation = item.needAnimation,
                    market = item.market,
                    btcPriceMultiplyCoinPrice = item.tradePrice.multiply(btcKrwPrice)
                        .formattedStringForBtc(),
                    btcPriceMultiplyAcc = item.accTradePrice24h.multiply(btcKrwPrice)
                        .formattedUnitStringForBtc(),
                    textFieldValue = textFieldValue,
                    warning = item.warning,
                    caution = item.getIsCaution(),
                    onClickEvent = {
                        keyboardController?.hide()
                        val market = item.market
                        val warning = item.warning
                        val caution = Utils.gson.toJson(item.caution)
                        appNavController.navigate("${AppScreen.CoinDetail.name}/$market/$warning/$caution") {
                            launchSingleTop = true
                            popUpTo(appNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    }
                )
            }
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
    needAnimation: MutableState<String>,
    market: String,
    btcPriceMultiplyCoinPrice: String,
    btcPriceMultiplyAcc: String,
    textFieldValue: State<String>,
    warning: Boolean,
    caution: Boolean,
) = Column {
    Row(
        modifier = modifier
            .clickable { onClickEvent.invoke() }
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val animationDurationTimeMills = 150
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = animationDurationTimeMills
                    0.5f at 100
                },
                repeatMode = RepeatMode.Reverse
            ),
            label = "animation alpha"
        )

        LaunchedEffect(key1 = needAnimation.value) {
            delay(animationDurationTimeMills.toLong())
            needAnimation.value = TickerAskBidState.NONE.name
        }

        LaunchedEffect(key1 = textFieldValue.value) {
            needAnimation.value = TickerAskBidState.NONE.name
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
                style = TextStyle(fontWeight = FontWeight.W600, color = commonTextColor())
            )
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (symbolRef, warningRef, cautionRef) = createRefs()

                Text(
                    modifier = Modifier.constrainAs(symbolRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                    text = symbol,
                    textAlign = TextAlign.Center,
                    fontSize = DpToSp(11.dp),
                    style = TextStyle(
                        color = Color(0xff8C939E)
                    )
                )

                if (warning) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .constrainAs(warningRef) {
                                start.linkTo(symbolRef.end, margin = 3.dp)
                                top.linkTo(symbolRef.top)
                                bottom.linkTo(symbolRef.bottom)
                            }
                            .background(color = commonRiseColor(), shape = CircleShape),
                    )
                }
                if (caution) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .constrainAs(cautionRef) {
                                start.linkTo(symbolRef.end, margin = if (warning) 13.dp else 3.dp)
                                top.linkTo(symbolRef.top)
                                bottom.linkTo(symbolRef.bottom)
                            }
                            .background(color = exchangeCautionColor(), shape = CircleShape),
                    )
                }
            }
        }
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            AutoSizeText(
                text = lastPrice,
                modifier = Modifier
                    .drawBehind {
                        drawLine(
                            //TODO 다크모드 지원
                            color = when (needAnimation.value) {
                                TickerAskBidState.ASK.name -> {
                                    FallColor.copy(alpha = alpha)
                                }

                                TickerAskBidState.BID.name -> {
                                    RiseColor.copy(alpha = alpha)
                                }

                                else -> {
                                    Color.Transparent
                                }
                            },
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = if (needAnimation.value != TickerAskBidState.NONE.name) 2.dp.toPx() else 0.dp.toPx()
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
                    color = commonHintTextColor()
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
                ),
                color = commonTextColor()
            )
            if (!market.isTradeCurrencyKrw()) {
                AutoSizeText(
                    text = btcPriceMultiplyAcc,
                    textStyle = TextStyle(
                        fontSize = DpToSp(11.dp),
                        textAlign = TextAlign.End
                    ),
                    color = exchangeBTCtoKRWColor()
                )
            }
        }
    }
    Divider(
        modifier = Modifier
            .padding(horizontal = 9.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(color = commonDividerColor())
    )
}