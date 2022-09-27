package org.jeonfeel.moeuibit2.ui.coindetail

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.skydoves.landscapist.glide.GlideImage
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.CoinDetailViewModel
import org.jeonfeel.moeuibit2.constant.coinImageUrl
import org.jeonfeel.moeuibit2.util.EtcUtils
import org.jeonfeel.moeuibit2.util.calculator.Calculator
import org.jeonfeel.moeuibit2.util.calculator.CurrentCalculator

@Composable
fun CoinDetailMain(
    currentPrice: Double = 100.0,
    symbol: String,
    coinDetailViewModel: CoinDetailViewModel = viewModel(),
) {
    val marketState = EtcUtils.getSelectedMarket(coinDetailViewModel.market)
    val curTradePrice = CurrentCalculator.tradePriceCalculator(currentPrice,marketState)
    val curChangeRate =
        Calculator.signedChangeRateCalculator(coinDetailViewModel.coinDetailModel.signedChangeRate)
    val curChangePrice = Calculator.changePriceCalculator(coinDetailViewModel.coinDetailModel.signed_change_price)
    val textColor = getTextColor(curChangeRate)
    val navController = rememberNavController()

    Column(modifier = Modifier
        .padding(0.dp, 10.dp, 0.dp, 0.dp)
        .fillMaxWidth()
        .wrapContentHeight()) {
        Row(modifier = Modifier
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
                    Text(text = stringResource(id = R.string.netChange), modifier = Modifier
                        .wrapContentWidth()
                        .padding(0.dp, 0.dp, 10.dp, 0.dp))
                    Text(text = curChangeRate.plus("%"),
                        modifier = Modifier.weight(1f),
                        style = TextStyle(color = textColor),
                        maxLines = 1)
                    Text(text = curChangePrice,
                        modifier = Modifier.weight(1f),
                        style = TextStyle(color = textColor),
                        maxLines = 1)
                }
            }
            GlideImage(imageModel = coinImageUrl.plus("$symbol.png"),
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 10.dp)
                    .weight(1f),
                contentScale = ContentScale.FillHeight,
            placeHolder = ImageBitmap.imageResource(R.drawable.img_glide_placeholder)
            )
        }
        CoinDetailMainTabRow(navController = navController)
        TabRowMainNavigation(navController,coinDetailViewModel)
    }
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