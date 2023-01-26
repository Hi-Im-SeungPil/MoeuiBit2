package org.jeonfeel.moeuibit2.ui.coindetail.order

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.ui.viewmodels.CoinDetailViewModel
import org.jeonfeel.moeuibit2.constants.SOCKET_IS_CONNECTED
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitOrderBookWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailOrderBookModel
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.ui.custom.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.*
import org.jeonfeel.moeuibit2.ui.custom.drawUnderLine
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator
import org.jeonfeel.moeuibit2.utils.secondDecimal
import org.jeonfeel.moeuibit2.utils.thirdDecimal
import kotlin.math.round

@Composable
fun AskingPriceLazyColumn(
    modifier: Modifier,
    coinDetailViewModel: CoinDetailViewModel = viewModel(),
) {
    val scrollState = rememberLazyListState(initialFirstVisibleItemIndex = 10)
    val preClosingPrice = coinDetailViewModel.preClosingPrice // 지난 가격
    val currentTradePriceStateForOrderBook = coinDetailViewModel.currentTradePriceStateForOrderBook // 현재가격
    val maxOrderBookSize = coinDetailViewModel.maxOrderBookSize // 벽돌
    val market = coinDetailViewModel.market // krw 인지 btc 인지

    LazyColumn(modifier = modifier, state = scrollState) {
        if (UpBitOrderBookWebSocket.currentSocketState == SOCKET_IS_CONNECTED && coinDetailViewModel.orderBookMutableStateList.size >= 30) {
            items(items = coinDetailViewModel.orderBookMutableStateList) { item ->
                AskingPriceLazyColumnItem(
                    item,
                    preClosingPrice,
                    currentTradePriceStateForOrderBook,
                    maxOrderBookSize,
                    market)
            }
        } else { // 로딩 되지 않았을 때 빈 화면
            items(15) {
                EmptyAskingPriceLazyColumnItem(0)
            }
            items(15) {
                EmptyAskingPriceLazyColumnItem(1)
            }
        }
    }
}

@Composable
fun AskingPriceLazyColumnItem(
    orderBook: CoinDetailOrderBookModel,
    preClosingPrice: Double,
    currentTradePrice: Double,
    maxOrderBookSize: Double,
    market: String,
) {
    val marketState = Utils.getSelectedMarket(market)
    val price = CurrentCalculator.tradePriceCalculator(orderBook.price, marketState)
    val rate = Calculator.orderBookRateCalculator(preClosingPrice, orderBook.price)
    val rateResult = rate.secondDecimal().plus("%")
    val orderBookSize = orderBook.size.thirdDecimal()
    val orderBookBlock = if(round(orderBook.size / maxOrderBookSize * 100).isNaN()) {
        0
    } else {
        round(orderBook.size / maxOrderBookSize * 100)
    }

    val orderBookTextColor = when {
        rate > 0.0 -> {
            increase_color
        }
        rate < 0.0 -> {
            decrease_color
        }
        else -> {
            Color.Black
        }
    }

    val orderBokBlockColor = if (orderBook.state == 0) {
        decrease_order_book_block_color
    } else {
        increase_order_book_block_color
    }

    val borderModifier = if (currentTradePrice == orderBook.price) {
        Modifier.border(1.dp, color = Color.Black)
    } else {
        Modifier.border(0.5.dp, color = Color.White)
    }

    val orderBookBackground = if (orderBook.state == 0) {
        decrease_order_book_color
    } else {
        increase_order_book_color
    }

    Row(
        modifier = borderModifier
            .fillMaxWidth()
            .height(40.dp)
            .background(orderBookBackground)
    ) {
        Column(modifier = Modifier.weight(5f)) {
            AutoSizeText(
                text = price,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .wrapContentHeight(),
                textStyle = TextStyle(fontSize = DpToSp(15.dp),
                    textAlign = TextAlign.Center),
                color = orderBookTextColor
            )
            Text(
                text = rateResult,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .wrapContentHeight(),
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = DpToSp(13.dp), color = orderBookTextColor)
            )
        }
        Box(
            modifier = Modifier
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
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight()
                    .height(20.dp)
                    .fillMaxWidth(1f * orderBookBlock.toFloat() / 100)
                    .background(orderBokBlockColor)
            )

            AutoSizeText(
                text = orderBookSize, textStyle = MaterialTheme.typography.body1,
                modifier = Modifier
                    .padding(2.dp, 0.dp, 0.5.dp, 0.dp)
                    .fillMaxHeight()
                    .wrapContentHeight()
            )
        }
    }
}

/**
 * 빈화면
 */
@Composable
fun EmptyAskingPriceLazyColumnItem(
    orderBookState: Int,
) {
    val rateResult = ""
    val orderBokBlockColor = if (orderBookState == 0) {
        decrease_order_book_color
    } else {
        increase_order_book_color
    }

    Row(
        modifier = Modifier
            .border(0.5.dp, color = Color.White)
            .fillMaxWidth()
            .height(40.dp)
            .background(orderBokBlockColor)
    ) {
        Column(modifier = Modifier.weight(5f)) {
            Text(
                text = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .wrapContentHeight(),
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = DpToSp(15.dp))
            )
            Text(
                text = rateResult,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .wrapContentHeight(),
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = DpToSp(13.dp))
            )
        }
        Box(
            modifier = Modifier
                .weight(3f)
                .fillMaxHeight()
                .drawUnderLine(lineColor = Color.White, strokeWidth = 10f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight()
                    .height(20.dp)
            )
            Spacer(
                modifier = Modifier
                    .padding(2.dp, 0.dp, 0.5.dp, 0.dp)
                    .fillMaxHeight()
                    .wrapContentHeight()
            )
        }
    }
}