package org.jeonfeel.moeuibit2.ui.coindetail

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitOrderBookWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailOrderBookModel
import org.jeonfeel.moeuibit2.util.Calculator
import org.jeonfeel.moeuibit2.util.OnLifecycleEvent
import org.jeonfeel.moeuibit2.viewmodel.CoinDetailViewModel
import kotlin.math.round

@Composable
fun OrderScreen(
    coinDetailViewModel: CoinDetailViewModel,
) {

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> UpBitOrderBookWebSocket.onPause()
            Lifecycle.Event.ON_RESUME -> {
                coinDetailViewModel.setOrderBookWebSocketMessageListener()
                UpBitOrderBookWebSocket.onResume(coinDetailViewModel.market)
            }
            else -> {}
        }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        AskingPriceLazyColumn(Modifier
            .weight(3f)
            .fillMaxHeight(), coinDetailViewModel)
        Box(modifier = Modifier
            .weight(7f)
            .fillMaxHeight()) {
        }
    }
}

@Composable
fun AskingPriceLazyColumn(
    modifier: Modifier,
    coinDetailViewModel: CoinDetailViewModel,
) {
    val scrollState = rememberLazyListState(initialFirstVisibleItemIndex = 10)
    LazyColumn(modifier = modifier, state = scrollState) {
        itemsIndexed(items = coinDetailViewModel.orderBookMutableStateList) { _, item ->
            AskingPriceLazyColumnItem(item,
                coinDetailViewModel.preClosingPrice,
                coinDetailViewModel.currentTradePriceStateForOrderBook.value,
                coinDetailViewModel.maxOrderBookSize)
        }
    }
}

@Composable
fun AskingPriceLazyColumnItem(
    orderBook: CoinDetailOrderBookModel,
    preClosingPrice: Double,
    currentTradePrice: Double,
    maxOrderBookSize: Double,
) {
    val price = Calculator.orderBookPriceCalculator(orderBook.price)
    val rate = Calculator.orderBookRateCalculator(preClosingPrice, orderBook.price)
    val textColor = getOrderBookTextColor(rate)
    val borderModifier = getOrderBookBorder(currentTradePrice, orderBook.price)
    val rateResult = String.format("%.2f", rate).plus("%")
    val orderBookSize = String.format("%.3f", orderBook.size)
    val orderBookBlock = round(orderBook.size / maxOrderBookSize * 100)
    val orderBokBlockColor = getBlockColor(orderBook.state)

    Row(modifier = borderModifier
        .fillMaxWidth()
        .height(40.dp)
        .background(getOrderBookBackGround(orderBook.state))) {
        Column(modifier = Modifier.weight(5f)) {
            Text(text = price,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .wrapContentHeight(),
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = 15.sp, color = textColor))
            Text(text = rateResult,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .wrapContentHeight(),
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = 13.sp, color = textColor))
        }
        Box(modifier = Modifier
            .weight(3f)
            .fillMaxHeight()
            .drawWithContent {
                drawContent()
                clipRect {
                    drawLine(
                        brush = SolidColor(Color.White),
                        strokeWidth = 10f,
                        cap = StrokeCap.Square,
                        start = Offset.Zero,
                        end = Offset(y = size.height, x = 0f)
                    )
                }
            }) {
            Box(modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight()
                .height(20.dp)
                .fillMaxWidth(1f * orderBookBlock.toFloat() / 100)
                .background(orderBokBlockColor))

            val textStyleBody1 = MaterialTheme.typography.body1
            val textStyle = remember { mutableStateOf(textStyleBody1) }
            AutoSizeText(
                text = orderBookSize, textStyle = textStyle.value,
                modifier = Modifier
                    .padding(2.dp, 0.dp, 0.5.dp, 0.dp)
                    .fillMaxHeight()
                    .wrapContentHeight()
            )
        }
    }
}

@Composable
fun getOrderBookBackGround(kind: Int): Color {
    return if (kind == 0) {
        colorResource(id = R.color.orderBookAskBackground)
    } else {
        colorResource(id = R.color.orderBookBidBackground)
    }
}

fun getOrderBookTextColor(rate: Double): Color {
    return when {
        rate > 0.0 -> {
            Color.Red
        }
        rate < 0.0 -> {
            Color.Blue
        }
        else -> {
            Color.Black
        }
    }
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun getOrderBookBorder(currentPrice: Double, orderBookPrice: Double): Modifier {
    return if (currentPrice == orderBookPrice) {
        Modifier.border(1.dp, color = Color.Black)
    } else {
        Modifier.border(0.5.dp, color = Color.White)
    }
}

@Composable
fun getBlockColor(kind: Int): Color {
    return if (kind == 0) {
        colorResource(id = R.color.orderBookAskBlock)
    } else {
        colorResource(id = R.color.orderBookBidBlock)
    }
}

@Composable
fun AutoSizeText(
    text: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    val scaledTextStyle = remember { mutableStateOf(textStyle) }
    val readyToDraw = remember { mutableStateOf(false) }

    Text(
        text,
        modifier.drawWithContent {
            if (readyToDraw.value) {
                drawContent()
            }
        },
        style = scaledTextStyle.value,
        softWrap = false,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                scaledTextStyle.value =
                    scaledTextStyle.value.copy(fontSize = scaledTextStyle.value.fontSize * 0.9)
            } else {
                readyToDraw.value = true
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
fun preask() {
//    askingPriceLazyColumnItem()
}