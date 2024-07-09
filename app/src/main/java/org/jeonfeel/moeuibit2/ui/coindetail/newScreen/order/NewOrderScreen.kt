package org.jeonfeel.moeuibit2.ui.coindetail.newScreen.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.OrderBookModel
import org.jeonfeel.moeuibit2.data.usecase.OrderBookKind
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedString
import org.jeonfeel.moeuibit2.utils.thirdDecimal
import java.math.BigDecimal

@Composable
fun OrderScreen(
    initCoinOrder: (String) -> Unit,
    coinOrderScreenOnPause: () -> Unit,
    market: String,
    preClosedPrice: Double,
    orderBookList: List<OrderBookModel>,
    maxOrderBookSize: Double,
    coinPrice: BigDecimal,
//    quantityState: Int
) {
    val state = rememberCoinOrderStateHolder(
        preClosedPrice = preClosedPrice,
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

        }
    )

    Row(
        modifier = Modifier
            .background(color = androidx.compose.material3.MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        OrderBookSection(
            orderBookList = orderBookList,
            getOrderBookItemFluctuateRate = state::getOrderBookItemFluctuateRate,
            getOrderBookItemBackground = state::getOrderBookItemBackground,
            getOrderBookItemTextColor = state::getOrderBookItemTextColor,
            getOrderBookBlockColor = state::getOrderBookBlockColor,
            getOrderBookBlockSize = state::getOrderBookBlockSize
        )
        Box(
            modifier = Modifier
                .weight(7f)
                .fillMaxHeight()
                .background(color = androidx.compose.material3.MaterialTheme.colorScheme.background)
        )
    }
}

@Composable
fun RowScope.OrderBookSection(
    orderBookList: List<OrderBookModel>,
    getOrderBookItemFluctuateRate: (Double) -> String,
    getOrderBookItemBackground: (OrderBookKind) -> Color,
    getOrderBookItemTextColor: (Double) -> Color,
    getOrderBookBlockColor: (OrderBookKind) -> Color,
//    getOrderBookText: (Double, Double) -> String,
    getOrderBookBlockSize: (Double) -> Float
) {
    LazyColumn(modifier = Modifier.weight(3f)) {
        items(orderBookList) { orderBookModel ->
            OrderBookView(
                price = orderBookModel.price.formattedString(),
                fluctuateRate = getOrderBookItemFluctuateRate(orderBookModel.price.toDouble()),
                itemBackground = getOrderBookItemBackground(orderBookModel.kind),
                itemTextColor = getOrderBookItemTextColor(orderBookModel.price.toDouble()),
                orderBookText = orderBookModel.size.thirdDecimal(),
                orderBokBlockColor = getOrderBookBlockColor(orderBookModel.kind),
                orderBookBlockSize = getOrderBookBlockSize(orderBookModel.price.toDouble())
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
    fluctuateRate: String,
    orderBookBlockSize: Float,
    orderBookText: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(itemBackground)
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
}