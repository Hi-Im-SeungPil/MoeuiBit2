package org.jeonfeel.moeuibit2.ui.coindetail.order.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.ui.coindetail.CoinDetailViewModel
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.CoinDetailOrderBookModel
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.*
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
    val initPosition = remember {
        coinDetailViewModel.getOrderBookInitPosition()
    }
    val blockItemCount = remember {
        coinDetailViewModel.getBlockItemCount()
    }
    val scrollState = rememberLazyListState(initialFirstVisibleItemIndex = initPosition)
    val preClosingPrice = coinDetailViewModel.preClosingPrice // 지난 가격
    val currentTradePriceStateForOrderBook =
        coinDetailViewModel.coinOrder.state.currentTradePriceStateForOrderBook // 현재가격
    val maxOrderBookSize = coinDetailViewModel.coinOrder.state.maxOrderBookSize.value // 벽돌
    val market = coinDetailViewModel.market // krw 인지 btc 인지

    LazyColumn(
        modifier = modifier.background(color = androidx.compose.material3.MaterialTheme.colorScheme.background),
        state = scrollState
    ) {
        if (coinDetailViewModel.coinOrder.state.orderBookMutableStateList.size >= 30) {
            items(items = coinDetailViewModel.coinOrder.state.orderBookMutableStateList) { item ->
                AskingPriceLazyColumnItem(
                    orderBook = item,
                    preClosingPrice = preClosingPrice,
                    currentTradePrice = currentTradePriceStateForOrderBook.value,
                    maxOrderBookSize = maxOrderBookSize,
                    market = market
                )
            }
        } else { // 로딩 되지 않았을 때 빈 화면
            items(blockItemCount) {
                EmptyAskingPriceLazyColumnItem(0)
            }
            items(blockItemCount) {
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
    val orderBookBlock = if (round(orderBook.size / maxOrderBookSize * 100).isNaN()) {
        0
    } else {
        round(orderBook.size / maxOrderBookSize * 100)
    }

    val orderBookTextColor = when {
        rate > 0.0 -> {
            increaseColor()
        }
        rate < 0.0 -> {
            decreaseColor()
        }
        else -> {
            androidx.compose.material3.MaterialTheme.colorScheme.onBackground
        }
    }

    val orderBokBlockColor = if (orderBook.state == 0) {
        decreaseOrderBookBlockColor()
    } else {
        increaseOrderBookBlockColor()
    }

    val borderModifier = if (currentTradePrice == orderBook.price) {
        Modifier.border(
            1.dp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
        )
    } else {
        Modifier.border(
            1.dp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.background
        )
    }

    val orderBookBackground = if (orderBook.state == 0) {
        decreaseOrderBookColor()
    } else {
        increaseOrderBookColor()
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
                textStyle = TextStyle(
                    fontSize = DpToSp(15.dp),
                    textAlign = TextAlign.Center
                ),
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
        decreaseOrderBookColor()
    } else {
        increaseOrderBookColor()
    }

    Row(
        modifier = Modifier
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