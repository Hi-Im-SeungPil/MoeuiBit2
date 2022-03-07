package org.jeonfeel.moeuibit2.ui.mainactivity

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ExchangeScreenItem(
    coinName: String,
    marketName: String,
    coinPrice: Double,
    coinRate: Double,
    coinAmount: Double,
) {
    Row(Modifier
        .fillMaxWidth()
        .height(50.dp)) {
        Column(Modifier
            .weight(1f)
            .align(Alignment.Bottom)) {
            Text(
                text = coinName,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Bottom),
                style = TextStyle(textAlign = TextAlign.Center)
            )
            Text(text = marketName,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                style = TextStyle(textAlign = TextAlign.Center))
        }
        Text(text = coinPrice.toString(),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight(),
            style = TextStyle(textAlign = TextAlign.Center))
        Text(text = coinRate.toString(),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight(),
            style = TextStyle(textAlign = TextAlign.Center))
        Text(text = coinAmount.toString(),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight(),
            style = TextStyle(textAlign = TextAlign.Center))
    }
}

@Composable
fun ExchangeList() {
    LazyColumn {
        item {
            ExchangeScreenItem("에이다", "KRW-ADA", 1100.0, 30.8, 4223.0)
        }
        items(30) {
            ExchangeScreenItem("비트코인", "KRW-BTC", 542001.0, 10.8, 5800.0)
        }
    }
}

@Composable
fun ExchangeScreen() {
    ExchangeList()
}

@Preview(showBackground = true)
@Composable
fun ExchangeScreenItemPreview() {
    ExchangeScreenItem("비트코인", "KRW-BTC", 542001.0, 10.8, 5800.0)
}

@Preview(showBackground = true)
@Composable
fun ExchangeListPreview() {
    ExchangeList()
}