package org.jeonfeel.moeuibit2.ui.coindetail.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.viewmodels.CoinDetailViewModel
import org.jeonfeel.moeuibit2.constants.BID
import org.jeonfeel.moeuibit2.ui.custom.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.decrease_color
import org.jeonfeel.moeuibit2.ui.theme.increase_color
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator
import org.jeonfeel.moeuibit2.utils.eighthDecimal
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionInfoLazyColumn(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd kk:mm", Locale("ko", "KR"))
    val marketState = Utils.getSelectedMarket(coinDetailViewModel.market)
    val transactionInfoList = coinDetailViewModel.transactionInfoList
    if (transactionInfoList.isEmpty()) {
        Text(text = "거래내역이 없습니다.",
            modifier = Modifier
                .padding(0.dp, 15.dp, 0.dp, 0.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = DpToSp(18.dp),
            fontWeight = FontWeight.Bold)
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(transactionInfoList) { _, item ->
                val askBidText = if (item.transactionStatus == BID) {
                    "매수"
                } else {
                    "매도"
                }
                val totalPrice =
                    CurrentCalculator.tradePriceCalculator(item.quantity * item.price, marketState)
                val priceText = CurrentCalculator.tradePriceCalculator(item.price, marketState)
                val time = dateFormat.format(Date(item.transactionTime))
                TransactionInfoLazyColumnItem(askBidText = askBidText,
                    market = item.market,
                    time = time,
                    price = priceText,
                    quantity = item.quantity.eighthDecimal(),
                    totalPrice = totalPrice)
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
        increase_color
    } else {
        decrease_color
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
                style = TextStyle(textAlign = TextAlign.Center,
                    color = textColor,
                    fontSize = DpToSp(18.dp),
                    fontWeight = FontWeight.Bold)
            )
            Text(
                text = market, modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight(),
                style = TextStyle(fontSize = DpToSp(18.dp),
                    fontWeight = FontWeight.Bold)
            )
        }
        Divider(modifier = Modifier
            .fillMaxWidth(), color = colorResource(id = R.color.C0F0F5C))
        ItemRow(title = "시간", content = time)
        ItemRow(title = "가격", content = price)
        ItemRow(title = "수량", content = quantity)
        ItemRow(title = "총액", content = totalPrice)
        Divider(modifier = Modifier
            .fillMaxWidth(), color = colorResource(id = R.color.C0F0F5C))
    }
}

@Composable
private fun ItemRow(title: String, content: String) {
    Row(modifier = Modifier
        .padding(0.dp, 3.dp)
        .fillMaxSize()) {
        Text(text = title)
        Text(text = content, modifier = Modifier.weight(1f, true), textAlign = TextAlign.End)
    }
}