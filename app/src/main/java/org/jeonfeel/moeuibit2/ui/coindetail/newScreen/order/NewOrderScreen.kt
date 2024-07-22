package org.jeonfeel.moeuibit2.ui.coindetail.newScreen.order

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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.OrderBookModel
import org.jeonfeel.moeuibit2.data.usecase.OrderBookKind
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedString
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
    coinPrice: BigDecimal,
    orderBookIndicationState: State<String>,
    saveOrderBookIndicationState: () -> Unit,
    changeOrderBookIndicationState: () -> Unit
//    quantityState: Int
) {
    val state = rememberCoinOrderStateHolder(
        commonExchangeModelState = commonExchangeModelState,
        coinPrice = coinPrice,
        maxOrderBookSize = maxOrderBookSize
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
        Column(Modifier.weight(3f)) {
            OrderBookSection(
                orderBookList = orderBookList,
                getOrderBookItemFluctuateRate = state::getOrderBookItemFluctuateRate,
                getOrderBookItemBackground = state::getOrderBookItemBackground,
                getOrderBookItemTextColor = state::getOrderBookItemTextColor,
                getOrderBookBlockColor = state::getOrderBookBlockColor,
                getOrderBookBlockSize = state::getOrderBookBlockSize,
                isMatchTradePrice = state::getIsMatchedTradePrice,
                orderBookIndicationState = orderBookIndicationState,
                getOrderBookIndicationText = state::getOrderBookIndicationText
            )
            Row(
                modifier = Modifier
                    .border(1.dp, Color.Black)
                    .clickable {
                        changeOrderBookIndicationState()
                    }
                    .padding(vertical = 10.dp)
                    .padding(horizontal = 5.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.img_sync_alt),
                    contentDescription = "",
                    modifier = Modifier.size(24.dp).align(Alignment.CenterVertically)
                )
                Text(
                    text = state.getOrderBookIndicationText(orderBookIndicationState.value),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 3.dp)
                        .align(Alignment.CenterVertically),
                    style = TextStyle(fontSize = DpToSp(12.dp), textAlign = TextAlign.Center)
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(7f)
                .fillMaxHeight()
                .background(color = androidx.compose.material3.MaterialTheme.colorScheme.background)
        )
    }
}

@Composable
fun ColumnScope.OrderBookSection(
    orderBookList: List<OrderBookModel>,
    getOrderBookItemFluctuateRate: (Double) -> String,
    getOrderBookItemBackground: (OrderBookKind) -> Color,
    getOrderBookItemTextColor: (Double) -> Color,
    getOrderBookBlockColor: (OrderBookKind) -> Color,
//    getOrderBookText: (Double, Double) -> String,
    getOrderBookIndicationText: (String, Double) -> String,
    getOrderBookBlockSize: (Double) -> Float,
    isMatchTradePrice: (BigDecimal) -> Boolean,
    orderBookIndicationState: State<String>
) {

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val centralItemIndex = 15
    val density = LocalDensity.current
    val local = LocalConfiguration.current
    val orderBookSectionHeight = remember { mutableIntStateOf(0) }
    LaunchedEffect(true) {
        delay(1000L)
        coroutineScope.launch {
            listState.animateScrollAndCentralizeItem(15,coroutineScope)
        }
    }
    LazyColumn(modifier = Modifier.weight(1f).onGloballyPositioned { coordinates ->
        orderBookSectionHeight.intValue = coordinates.size.height
    }, state = listState) {
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
                isMatchTradePrice = isMatchTradePrice(orderBookModel.price)
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

fun LazyListState.animateScrollAndCentralizeItem(index: Int, scope: CoroutineScope) {
    val itemInfo = this.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
    scope.launch {
        if (itemInfo != null) {
            val center = this@animateScrollAndCentralizeItem.layoutInfo.viewportEndOffset / 2
            val childCenter = itemInfo.offset + itemInfo.size / 2
            this@animateScrollAndCentralizeItem.scrollBy((childCenter - center).toFloat())
        } else {
            this@animateScrollAndCentralizeItem.scrollToItem(index)
        }
    }
}