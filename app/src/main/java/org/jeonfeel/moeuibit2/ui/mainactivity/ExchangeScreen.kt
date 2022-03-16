package org.jeonfeel.moeuibit2.ui.mainactivity

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.RealExchangeModel
import org.jeonfeel.moeuibit2.util.Calculator
import org.jeonfeel.moeuibit2.viewmodel.ExchangeViewModel
import java.lang.Exception

@Composable
fun ExchangeScreenItem(
    realExchangeModel: RealExchangeModel,
    tradePrice: Double,
) {
    val signedChangeRate = Calculator.signedChangeRateCalculator(realExchangeModel.signedChangeRate)
    val preTradePrice = Calculator.tradePriceCalculator(tradePrice)
    val curTradePrice = Calculator.tradePriceCalculator(realExchangeModel.tradePrice)
    var priceHL = -1
    if (preTradePrice < curTradePrice) {
        priceHL = 1
    } else if (preTradePrice > curTradePrice) {
        priceHL = 2
    } else {
        priceHL = 0
    }
    Log.d("aaaaaaaaa", priceHL.toString())

    Row(Modifier
        .fillMaxWidth()
        .height(50.dp)
        .border(BorderStroke(0.5.dp, color = Color.LightGray))) {
        Column(Modifier
            .weight(1f)
            .align(Alignment.Bottom)) {
            Text(
                text = realExchangeModel.koreanName,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Bottom),
                style = TextStyle(textAlign = TextAlign.Center)
            )
            Text(text = realExchangeModel.market,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                style = TextStyle(textAlign = TextAlign.Center))
        }

        lateinit var modifier1: Modifier

        when (priceHL) {
            1 -> {
                modifier1 = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .wrapContentHeight()
                    .border(1.dp, Color.Red)
            }
            2 -> {
                modifier1 = Modifier.weight(1f)
                    .fillMaxHeight()
                    .wrapContentHeight()
                    .border(1.dp,Color.Blue)
            }
            else -> {
                modifier1 = Modifier.weight(1f)
                    .fillMaxHeight()
                    .wrapContentHeight()
                    .border(0.dp,Color.White)
            }
        }

        Text(text = Calculator.tradePriceCalculator(realExchangeModel.tradePrice),
            modifier = modifier1,
            style = when {
                signedChangeRate.toFloat() > 0 -> {
                    TextStyle(textAlign = TextAlign.Center, color = Color.Red)
                }
                signedChangeRate.toFloat() < 0 -> {
                    TextStyle(textAlign = TextAlign.Center, color = Color.Blue)
                }
                else -> {
                    TextStyle(textAlign = TextAlign.Center, color = Color.Black)
                }
            }
        )
        Text(text = Calculator.signedChangeRateCalculator(realExchangeModel.signedChangeRate)
            .plus("%"),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight(),
            style = when {
                signedChangeRate.toFloat() > 0 -> {
                    TextStyle(textAlign = TextAlign.Center, color = Color.Red)
                }
                signedChangeRate.toFloat() < 0 -> {
                    TextStyle(textAlign = TextAlign.Center, color = Color.Blue)
                }
                else -> {
                    TextStyle(textAlign = TextAlign.Center, color = Color.Black)
                }
            }
        )
        Text(text = Calculator.accTradePrice24hCalculator(realExchangeModel.accTradePrice24h)
            .plus("백만"),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight(),
            style = TextStyle(textAlign = TextAlign.Center))
    }
}

@Composable
fun ExchangeList(realItem: SnapshotStateList<RealExchangeModel>) {

    val rr = remember {
        ArrayList<RealExchangeModel>()
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(items = realItem
        ) { index, krwCoinListElement ->
            if (rr.size - 1 >= index) {
                ExchangeScreenItem(krwCoinListElement, rr[index].tradePrice)
                rr[index] = krwCoinListElement
            } else if (rr.size - 1 < index) {
                ExchangeScreenItem(krwCoinListElement, krwCoinListElement.tradePrice)
                rr.add(krwCoinListElement)
            }
        }
    }
}

@Composable
fun ExchangeScreen(exchangeViewModel: ExchangeViewModel = viewModel()) {
    ExchangeList(exchangeViewModel.listst)
}

@Preview(showBackground = true)
@Composable
fun ExchangeScreenItemPreview() {
//    ExchangeScreenItem()
}

@Preview(showBackground = true)
@Composable
fun ExchangeListPreview() {
//    ExchangeList()
}