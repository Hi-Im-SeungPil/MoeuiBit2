package org.jeonfeel.moeuibit2.ui.coindetail.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.CoinDetailViewModel

@Composable
fun TransactionInfoLazyColumn(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
//        itemsIndexed()
    }
}

@Composable
fun TransactionInfoLazyColumnItem(askBidText: String, market: String, time: String, price: String, quantity: String, totalPrice: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        )
        {
            Text(
                text = askBidText, modifier = Modifier
                    .weight(1f, true)
                    .fillMaxHeight()
                    .wrapContentHeight(), style = TextStyle(textAlign = TextAlign.Center)
            )
            Text(
                text = market, modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight()
            )
        }
        ItemRow(title = "시간", content = time)
        ItemRow(title = "가격", content = price)
        ItemRow(title = "수량", content = quantity)
        ItemRow(title = "총액", content = totalPrice)
    }
}

@Composable
private fun ItemRow(title: String, content: String) {
    Row(modifier = Modifier.fillMaxSize()) {
        Text(text = title)
        Text(text = content, modifier = Modifier.weight(1f,true), textAlign = TextAlign.End)
    }
}