package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.KrwExchangeModel
import org.jeonfeel.moeuibit2.util.Calculator

@Composable
fun ExchangeScreenLazyColumnItem(
    krwExchangeModel: KrwExchangeModel,
    preTradePrice: Double,
) {
    val signedChangeRate =
        Calculator.signedChangeRateCalculator(krwExchangeModel.signedChangeRate)
    val curTradePrice = Calculator.tradePriceCalculator(krwExchangeModel.tradePrice)
    val accTradePrice24h =
        Calculator.accTradePrice24hCalculator(krwExchangeModel.accTradePrice24h)
    val koreanName = krwExchangeModel.koreanName

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
            Text(text = "${krwExchangeModel.symbol}/KRW",
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                style = TextStyle(textAlign = TextAlign.Center))
        }

        Box(modifier = getTradePriceTextModifier(preTradePrice,
            curTradePrice).weight(1f)) {
            Text(text = getCurTradePriceTextFormat(curTradePrice),
                modifier = Modifier
                    .fillMaxWidth()
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
        }

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
fun ExchangeScreenLazyColumn(
    KrwExchangeModelList: SnapshotStateList<KrwExchangeModel>,
    searchTextFieldValue: MutableState<String>,
    KrwExchangeModelListPosition: HashMap<String, Int>,
    preItemArray: ArrayList<KrwExchangeModel>
) {

    var filteredKrwExchangeList: SnapshotStateList<KrwExchangeModel>
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        val searchedText = searchTextFieldValue.value
        filteredKrwExchangeList = if (searchedText.isEmpty()) {
            KrwExchangeModelList
        } else {
            val resultList = SnapshotStateList<KrwExchangeModel>()
            for (element in KrwExchangeModelList) {
                if (element.koreanName.contains(searchedText) || element.EnglishName.uppercase()
                        .contains(searchedText.uppercase()) || element.symbol.uppercase().contains(
                        searchedText.uppercase())
                ) {
                    resultList.add(element)
                }
            }
            resultList
        }
        itemsIndexed(items = filteredKrwExchangeList
        ) { _, krwCoinListElement ->
            ExchangeScreenLazyColumnItem(krwCoinListElement,
                preItemArray[KrwExchangeModelListPosition[krwCoinListElement.market]
                    ?: 0].tradePrice)
            preItemArray[KrwExchangeModelListPosition[krwCoinListElement.market] ?: 0] =
                krwCoinListElement
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
                .padding(0.dp, 4.dp)
                .fillMaxHeight()
                .border(1.dp, Color.Red)
        }
        tempPreTradePrice > curTradePrice -> {
            return Modifier
                .padding(0.dp, 4.dp)
                .fillMaxHeight()
                .border(1.dp, Color.Blue)
        }
        else -> {
            return Modifier
                .padding(0.dp, 4.dp)
                .fillMaxHeight()
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