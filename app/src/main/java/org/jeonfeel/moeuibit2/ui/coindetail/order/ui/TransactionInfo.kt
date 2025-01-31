package org.jeonfeel.moeuibit2.ui.coindetail.order.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.BID
import org.jeonfeel.moeuibit2.constants.SELECTED_BTC_MARKET
import org.jeonfeel.moeuibit2.constants.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.data.local.room.entity.TransactionInfo
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.decreaseColor
import org.jeonfeel.moeuibit2.ui.theme.increaseColor
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator
import org.jeonfeel.moeuibit2.utils.eighthDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionInfoLazyColumn(
    isKrw: Boolean,
    transactionInfoList: List<TransactionInfo>,
) {
    val dateFormat = remember {
        SimpleDateFormat("yyyy-MM-dd kk:mm", Locale("ko", "KR"))
    }
    val marketState = if (isKrw) {
        SELECTED_KRW_MARKET
    } else {
        SELECTED_BTC_MARKET
    }

    if (transactionInfoList.isEmpty()) {
        Text(
            text = stringResource(id = R.string.empty_transaction_info),
            modifier = Modifier
                .padding(0.dp, 15.dp, 0.dp, 0.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = DpToSp(18.dp),
            fontWeight = FontWeight.Bold,
            style = TextStyle(color = MaterialTheme.colorScheme.onBackground)
        )
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(transactionInfoList) { _, item ->
                val askBidText = if (item.transactionStatus == BID) {
                    stringResource(id = R.string.bid)
                } else {
                    stringResource(id = R.string.ask)
                }
                val totalPrice =
                    CurrentCalculator.tradePriceCalculator(item.quantity * item.price, marketState)
                val priceText = CurrentCalculator.tradePriceCalculator(item.price, marketState)
                val time = dateFormat.format(Date(item.transactionTime))
                TransactionInfoLazyColumnItem(
                    askBidText = askBidText,
                    market = item.market,
                    time = time,
                    price = priceText,
                    quantity = item.quantity.eighthDecimal(),
                    totalPrice = totalPrice
                )
            }
        }
    }
}

@Composable
fun TransactionInfoLazyColumnItem(
    askBidText: String,
    market: String,
    time: String,
    price: String,
    quantity: String,
    totalPrice: String,
) {
    val textColor = if (askBidText == "매수") {
        increaseColor()
    } else {
        decreaseColor()
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(0.dp, 8.dp)
                .fillMaxWidth()
        )
        {
            Text(
                text = askBidText, modifier = Modifier
                    .weight(1f, true)
                    .fillMaxHeight()
                    .wrapContentHeight(),
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = textColor,
                    fontSize = DpToSp(18.dp),
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = market, modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight(),
                style = TextStyle(
                    fontSize = DpToSp(18.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth(), color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        ItemRow(title = stringResource(id = R.string.time), content = time)
        ItemRow(title = stringResource(id = R.string.price), content = price)
        ItemRow(title = stringResource(id = R.string.quantity), content = quantity)
        ItemRow(title = stringResource(id = R.string.total), content = totalPrice)
        Divider(
            modifier = Modifier
                .fillMaxWidth(), color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun ItemRow(title: String, content: String) {
    Row(
        modifier = Modifier
            .padding(0.dp, 3.dp)
            .fillMaxSize()
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = DpToSp(dp = 13.dp)
        )
        Text(
            text = content,
            modifier = Modifier.weight(1f, true),
            textAlign = TextAlign.End,
            fontSize = DpToSp(dp = 13.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}