package org.jeonfeel.moeuibit2.ui.coindetail

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.glide.GlideImage
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailTickerModel
import org.jeonfeel.moeuibit2.util.Calculator

@Composable
fun CoinDetailMain(
    currentPrice: Double = 100.0,
    symbol: String,
    coinDetailTickerModel: CoinDetailTickerModel,
) {
    val curTradePrice = getCurTradePriceTextFormat(currentPrice)
    val curChangeRate = Calculator.signedChangeRateCalculator(coinDetailTickerModel.signedChangeRate)
    val curChangePrice = Calculator.changePriceCalculator(coinDetailTickerModel.signed_change_price)
    val textColor = getTextColor(curChangeRate)
    Row(modifier = Modifier
        .padding(0.dp, 10.dp, 0.dp, 0.dp)
        .fillMaxWidth()
        .height(70.dp)) {
        Column(modifier = Modifier
            .weight(2f)
            .fillMaxHeight()) {
            Text(text = curTradePrice,
                modifier = Modifier.padding(20.dp, 0.dp, 0.dp, 0.dp),
                style = TextStyle(color = textColor, fontSize = 27.sp))

            Row(modifier = Modifier
                .padding(20.dp, 0.dp, 0.dp, 10.dp)
                .fillMaxHeight()
                .wrapContentHeight(Alignment.Bottom)
            ) {
                Text(text = "전일대비", modifier = Modifier.weight(1f))
                Text(text = curChangeRate.plus("%"), modifier = Modifier.weight(1f), style = TextStyle(color = textColor))
                Text(text = curChangePrice, modifier = Modifier.weight(1f), style = TextStyle(color = textColor))
            }
        }
        GlideImage(imageModel = "https://raw.githubusercontent.com/Hi-Im-SeungPil/moeuibitImg/main/coinlogo2/$symbol.png",
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 10.dp)
                .weight(1f),
            contentScale = ContentScale.FillHeight)
    }
}

@Preview(showBackground = true)
@Composable
fun CoinDetailMainPreview() {
//    CoinDetailMain()
}

fun getTextColor(
    curChangeRate: String,
): Color {
    val curSignedChangeRate = curChangeRate.toDouble()
    return when {
        curSignedChangeRate > 0.0 -> {
            Color.Red
        }
        curSignedChangeRate < 0.0 -> {
            Color.Blue
        }
        else -> {
            Color.Black
        }
    }
}

private fun getCurTradePriceTextFormat(curTradePrice: Double): String {
    return if (curTradePrice >= 100.0) {
        Calculator.getDecimalFormat().format(curTradePrice.toInt())
    } else {
        Calculator.tradePriceCalculator(curTradePrice)
    }
}