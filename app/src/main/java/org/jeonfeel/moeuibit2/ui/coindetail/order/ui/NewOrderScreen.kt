package org.jeonfeel.moeuibit2.ui.coindetail.order.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.ASK_BID_SCREEN_BID_TAB
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.OrderBookModel
import org.jeonfeel.moeuibit2.data.usecase.OrderBookKind
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.clearFocusOnKeyboardDismiss
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedString
import org.jeonfeel.moeuibit2.utils.isTradeCurrencyKrw
import org.jeonfeel.moeuibit2.utils.ext.showToast
import java.math.BigDecimal

@Composable
fun NewOrderScreen(
    initCoinOrder: (String) -> Unit,
    coinOrderScreenOnPause: () -> Unit,
    coinOrderScreenOnResume: (String) -> Unit,
    orderBookList: List<OrderBookModel>,
    market: String,
    commonExchangeModelState: State<CommonExchangeModel?>,
    maxOrderBookSize: State<Double>,
    orderBookIndicationState: State<String>,
    saveOrderBookIndicationState: () -> Unit,
    changeOrderBookIndicationState: () -> Unit,
    requestBid: (String, Double, BigDecimal, Double) -> Unit,
    requestAsk: (String, Double, Long, BigDecimal, Double) -> Unit,
    userBTC: State<MyCoin>,
    userSeedMoney: State<Long>,
    userCoin: State<MyCoin>,
    btcPrice: State<BigDecimal>
) {
    val state = rememberCoinOrderStateHolder(
        commonExchangeModelState = commonExchangeModelState,
        maxOrderBookSize = maxOrderBookSize,
        userSeedMoney = userSeedMoney,
        requestBid = requestBid,
        requestAsk = requestAsk,
        market = market,
        userCoin = userCoin,
        userBTC = userBTC,
        btcPrice = btcPrice
    )

    AddLifecycleEvent(
        onCreateAction = {
            initCoinOrder(market)
        },
        onPauseAction = {
            coinOrderScreenOnPause()
        },
        onResumeAction = {
            coinOrderScreenOnResume(market)
        },
        onStopAction = {
            saveOrderBookIndicationState()
        }
    )

    Row(
        modifier = Modifier
            .background(color = androidx.compose.material3.MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        Column(Modifier.weight(4f)) {
            OrderBookSection(
                orderBookList = orderBookList,
                getOrderBookItemFluctuateRate = state::getOrderBookItemFluctuateRate,
                getOrderBookItemBackground = state::getOrderBookItemBackground,
                getOrderBookItemTextColor = state::getOrderBookItemTextColor,
                getOrderBookBlockColor = state::getOrderBookBlockColor,
                getOrderBookBlockSize = state::getOrderBookBlockSize,
                isMatchTradePrice = state::getIsMatchedTradePrice,
                orderBookIndicationState = orderBookIndicationState,
                getOrderBookIndicationText = state::getOrderBookIndicationText,
            )
            ChangeOrderBookIndicationSection(
                orderBookIndicationText = state.getOrderBookIndicationText(
                    orderBookIndicationState.value
                ), onClick = { changeOrderBookIndicationState() })
        }
        Column(
            modifier = Modifier
                .weight(7f)
                .fillMaxHeight()
                .background(color = androidx.compose.material3.MaterialTheme.colorScheme.background)
        ) {
            OrderSection(
                orderTabState = state.orderTabState,
                userSeedMoney = userSeedMoney,
                userBTC = userBTC,
                isKrw = market.isTradeCurrencyKrw(),
                symbol = commonExchangeModelState.value?.symbol ?: "",
                currentPrice = commonExchangeModelState.value?.tradePrice,
                updateBidCoinQuantity = state::updateBidCoinQuantity,
                updateAskCoinQuantity = state::updateAskCoinQuantity,
                bidQuantity = state.bidQuantity.value,
                askQuantity = state.askQuantity.value,
                quantityOnValueChanged = state::quantityOnValueChanged,
                getBidTotalPrice = state::getBidTotalPrice,
                getAskTotalPrice = state::getAskTotalPrice,
                requestBid = state::bid,
                requestAsk = state::ask,
                userCoin = userCoin,
                dropdownLabelList = state.percentageLabelList,
                askSelectedText = state.askQuantityPercentage.value,
                bidSelectedText = state.bidQuantityPercentage.value
            )
        }
    }
}

@Composable
fun ColumnScope.OrderBookSection(
    orderBookList: List<OrderBookModel>,
    getOrderBookItemFluctuateRate: (Double) -> String,
    getOrderBookItemBackground: (OrderBookKind) -> Color,
    getOrderBookItemTextColor: (Double) -> Color,
    getOrderBookBlockColor: (OrderBookKind) -> Color,
    getOrderBookIndicationText: (String, Double) -> String,
    getOrderBookBlockSize: (Double) -> Float,
    isMatchTradePrice: (BigDecimal) -> Boolean,
    orderBookIndicationState: State<String>,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val firstInit = remember { mutableStateOf(false) }
    LaunchedEffect(orderBookList.size == 30) {
        if (!firstInit.value && orderBookList.size == 30) {
            coroutineScope.launch {
                listState.scrollToItem(15)
                listState.scrollToCentralizeItem(15, coroutineScope)
                firstInit.value = true
            }
        }
    }
    LazyColumn(
        modifier = Modifier
            .weight(1f), state = listState
    ) {
        items(orderBookList) { orderBookModel ->
            OrderBookView(
                price = orderBookModel.price.formattedString(),
                fluctuateRate = getOrderBookItemFluctuateRate(orderBookModel.price.toDouble()),
                itemBackground = getOrderBookItemBackground(orderBookModel.kind),
                itemTextColor = getOrderBookItemTextColor(orderBookModel.price.toDouble()),
                orderBookText = getOrderBookIndicationText(
                    orderBookIndicationState.value,
                    orderBookModel.size
                ),
                orderBokBlockColor = getOrderBookBlockColor(orderBookModel.kind),
                orderBookBlockSize = getOrderBookBlockSize(orderBookModel.size),
                isMatchTradePrice = isMatchTradePrice(orderBookModel.price),
            )
        }
    }
}

@Composable
fun OrderBookView(
    itemBackground: Color,
    itemTextColor: Color,
    orderBokBlockColor: Color,
    price: String,
    isMatchTradePrice: Boolean,
    fluctuateRate: String,
    orderBookBlockSize: Float,
    orderBookText: String,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(41.dp)
                .background(itemBackground)
                .border(
                    width = 1.dp,
                    shape = RectangleShape,
                    color = if (isMatchTradePrice) Color.Black else Color.Transparent
                )
        ) {
            Column(modifier = Modifier.weight(5f)) {
                AutoSizeText(
                    text = price,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .wrapContentHeight(),
                    textStyle = TextStyle(
                        fontSize = DpToSp(15.dp),
                        textAlign = TextAlign.Center
                    ),
                    color = itemTextColor
                )
                Text(
                    text = fluctuateRate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = DpToSp(13.dp), color = itemTextColor)
                )
            }
            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxHeight()
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
                )
                Box(
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .fillMaxHeight()
                        .wrapContentHeight()
                        .height(35.dp)
                        .fillMaxWidth(orderBookBlockSize)
                        .background(orderBokBlockColor)
                )
                AutoSizeText(
                    text = orderBookText, textStyle = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .padding(2.dp, 0.dp, 0.5.dp, 0.dp)
                        .fillMaxHeight()
                        .wrapContentHeight()
                )
            }
        }
        Spacer(
            modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
        )
    }
}

@Composable
fun ChangeOrderBookIndicationSection(
    onClick: () -> Unit,
    orderBookIndicationText: String
) {
    Row(
        modifier = Modifier
            .border(1.dp, Color.Black)
            .clickable {
                onClick()
            }
            .padding(vertical = 10.dp)
            .padding(horizontal = 5.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.img_sync_alt),
            contentDescription = "",
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterVertically)
        )
        Text(
            text = orderBookIndicationText,
            modifier = Modifier
                .weight(1f)
                .padding(start = 3.dp)
                .align(Alignment.CenterVertically),
            style = TextStyle(fontSize = DpToSp(12.dp), textAlign = TextAlign.Center)
        )
    }
}


@Composable
fun OrderScreenQuantityTextField(
    modifier: Modifier = Modifier,
    placeholderText: String = "Placeholder",
    fontSize: TextUnit = MaterialTheme.typography.body2.fontSize,
    askBidSelectedTab: MutableState<Int>,
    bidQuantity: MutableState<String>,
    askQuantity: MutableState<String>,
    currentTradePriceState: MutableState<Double>
) {
    val context = LocalContext.current
    val value = if (askBidSelectedTab.value == ASK_BID_SCREEN_BID_TAB) {
        bidQuantity
    } else {
        askQuantity
    }

    BasicTextField(value = value.value, onValueChange = {
        if (it.toDoubleOrNull() == null && it != "") {
            value.value = ""
            context.showToast("숫자만 입력 가능합니다.")
        } else if (currentTradePriceState.value == 0.0) {
            context.showToast("네트워크 통신 오류입니다.")
        } else {
            value.value = it
        }
    }, singleLine = true,
        textStyle = TextStyle(
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
            fontSize = DpToSp(17.dp), textAlign = TextAlign.End
        ),
        modifier = modifier
            .clearFocusOnKeyboardDismiss()
            .padding(0.dp, 0.dp, 9.dp, 0.dp),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        decorationBox = { innerTextField ->
            Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f, true)) {
                    if (value.value.isEmpty()) {
                        Text(
                            placeholderText,
                            style = TextStyle(
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                                fontSize = fontSize,
                                textAlign = TextAlign.End
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    innerTextField()
                }
            }
        })
}

private fun LazyListState.scrollToCentralizeItem(index: Int, scope: CoroutineScope) {
    val itemInfo = this.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
    scope.launch {
        if (itemInfo != null) {
            val center = this@scrollToCentralizeItem.layoutInfo.viewportEndOffset / 2
            val childCenter = itemInfo.offset + itemInfo.size / 2
            val scrollDistance = childCenter - center
            this@scrollToCentralizeItem.scrollBy(scrollDistance.toFloat())
        } else {
            this@scrollToCentralizeItem.scrollToItem(index)
        }
    }
}