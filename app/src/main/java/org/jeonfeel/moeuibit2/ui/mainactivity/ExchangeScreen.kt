package org.jeonfeel.moeuibit2.ui.mainactivity

import android.annotation.SuppressLint
import android.inputmethodservice.Keyboard
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.KrwExchangeModel
import org.jeonfeel.moeuibit2.util.Calculator
import org.jeonfeel.moeuibit2.viewmodel.ExchangeViewModel

@Composable
fun ExchangeScreen(exchangeViewModel: ExchangeViewModel = viewModel()) {
    Column(Modifier
        .fillMaxSize()) {
        SearchTextField()
        SortButtons()
        ExchangeList(exchangeViewModel.krwExchangeModelMutableStateList)
    }
}

@Composable
fun SearchTextField() {
    var text by remember { mutableStateOf("") }

    TextField(value = text,
        onValueChange = { text = it },
        maxLines = 1,
        placeholder = { Text(text = "코인명/심볼 검색") },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp))
}

@Composable
fun SortButtons() {

    val selectedButtonState = remember { mutableStateOf(-1) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
    ) {
        Text(modifier = Modifier.weight(1f), text = "")

        Button(onClick = {
            when {
                selectedButtonState.value != 0 && selectedButtonState.value != 1 -> {
                    selectedButtonState.value = 0
                }
                selectedButtonState.value == 0 -> {
                    selectedButtonState.value = 1
                }
                else -> {
                    selectedButtonState.value = -1
                }
            }
        }, modifier = Modifier.weight(1f)) {
            when (selectedButtonState.value) {
                0 -> {
                    Text(text = "현재가↓", fontSize = 10.sp)
                }
                1 -> {
                    Text(text = "현재가↑", fontSize = 10.sp)
                }
                else -> {
                    Text(text = "현재가↓↑", fontSize = 10.sp)
                }
            }
        }

        Button(onClick = {
            when {
                selectedButtonState.value != 2 && selectedButtonState.value != 3 -> {
                    selectedButtonState.value = 2
                }
                selectedButtonState.value == 2 -> {
                    selectedButtonState.value = 3
                }
                else -> {
                    selectedButtonState.value = -1
                }
            }
        }, modifier = Modifier.weight(1f)) {
            when (selectedButtonState.value) {
                2 -> {
                    Text(text = "전일대비↓", fontSize = 10.sp, maxLines = 1)
                }
                3 -> {
                    Text(text = "전일대비↑", fontSize = 10.sp, maxLines = 1)
                }
                else -> {
                    Text(text = "전일대비↓↑", fontSize = 10.sp, maxLines = 1)
                }
            }
        }

        Button(onClick = {
            when {
                selectedButtonState.value != 4 && selectedButtonState.value != 5 -> {
                    selectedButtonState.value = 4
                }
                selectedButtonState.value == 4 -> {
                    selectedButtonState.value = 5
                }
                else -> {
                    selectedButtonState.value = -1
                }
            }
        }, modifier = Modifier.weight(1f)) {
            when (selectedButtonState.value) {
                4 -> {
                    Text(text = "거래량↓", fontSize = 10.sp)
                }
                5 -> {
                    Text(text = "거래량↑", fontSize = 10.sp)
                }
                else -> {
                    Text(text = "거래량↓↑", fontSize = 10.sp)
                }
            }
        }
    }
}


@Composable
fun ExchangeScreenItem(
    krwExchangeModel: KrwExchangeModel,
    preTradePrice: Double,
) {
    val signedChangeRate =
        Calculator.signedChangeRateCalculator(krwExchangeModel.signedChangeRate)
    val curTradePrice = Calculator.tradePriceCalculator(krwExchangeModel.tradePrice)
    val accTradePrice24h =
        Calculator.accTradePrice24hCalculator(krwExchangeModel.accTradePrice24h)
    val koreanName = krwExchangeModel.koreanName
    val market = krwExchangeModel.market

    Row(Modifier
        .fillMaxWidth()
        .height(50.dp)
        .border(BorderStroke(0.5.dp, color = Color.LightGray))) {
        Column(Modifier
            .weight(1f)
            .align(Alignment.Bottom)) {
            Text(
                text = koreanName,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Bottom),
                style = TextStyle(textAlign = TextAlign.Center)
            )
            Text(text = market,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                style = TextStyle(textAlign = TextAlign.Center))
        }
        Text(text = getCurTradePriceTextFormat(curTradePrice),
            modifier = getTradePriceTextModifier(preTradePrice,
                curTradePrice).weight(1f),
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
        Text(text = signedChangeRate
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
        Text(text = accTradePrice24h
            .plus("백만"),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight(),
            style = TextStyle(textAlign = TextAlign.Center))
    }
}

@Composable
fun ExchangeList(krwItem: SnapshotStateList<KrwExchangeModel>) {
    val preItemArray = remember {
        ArrayList<KrwExchangeModel>()
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(items = krwItem
        ) { index, krwCoinListElement ->
            if (preItemArray.size - 1 >= index) {
                ExchangeScreenItem(krwCoinListElement, preItemArray[index].tradePrice)
                preItemArray[index] = krwCoinListElement
            } else if (preItemArray.size - 1 < index) {
                ExchangeScreenItem(krwCoinListElement, krwCoinListElement.tradePrice)
                preItemArray.add(krwCoinListElement)
            }
        }
    }
}

@SuppressLint("ModifierFactoryExtensionFunction")
fun getTradePriceTextModifier(
    preTradePrice: Double,
    curTradePrice: String,
): Modifier {
    val tempPreTradePrice = Calculator.tradePriceCalculator(preTradePrice)
    when {
        tempPreTradePrice < curTradePrice -> {
            return Modifier
                .fillMaxHeight()
                .wrapContentHeight()
                .border(1.dp, Color.Red)
        }
        tempPreTradePrice > curTradePrice -> {
            return Modifier
                .fillMaxHeight()
                .wrapContentHeight()
                .border(1.dp, Color.Blue)
        }
        else -> {
            return Modifier
                .fillMaxHeight()
                .wrapContentHeight()
                .border(0.dp, Color.White)
        }
    }
}

fun getCurTradePriceTextFormat(curTradePrice: String): String {
    val tempCurTradePrice = curTradePrice.toDouble()
    return if (tempCurTradePrice >= 100.0) {
        Calculator.getDecimalFormat().format(tempCurTradePrice.toInt())
    } else {
        curTradePrice
    }
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