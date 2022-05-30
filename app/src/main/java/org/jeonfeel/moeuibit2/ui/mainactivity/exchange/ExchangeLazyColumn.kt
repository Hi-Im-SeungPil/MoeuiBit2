package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.KrwExchangeModel
import org.jeonfeel.moeuibit2.util.Calculator
import org.jeonfeel.moeuibit2.view.activity.coindetail.CoinDetailActivity
import org.jeonfeel.moeuibit2.view.activity.main.MainActivity
import org.jeonfeel.moeuibit2.viewmodel.ExchangeViewModel

@Composable
fun ExchangeScreenLazyColumnItem(
    krwExchangeModel: KrwExchangeModel,
    preTradePrice: Double,
    isFavorite: Boolean,
    startForActivityResult: ActivityResultLauncher<Intent>,
) {
    val signedChangeRate =
        Calculator.signedChangeRateCalculator(krwExchangeModel.signedChangeRate)
    val curTradePrice = Calculator.tradePriceCalculator(krwExchangeModel.tradePrice)
    val accTradePrice24h =
        Calculator.accTradePrice24hCalculator(krwExchangeModel.accTradePrice24h)
    val koreanName = krwExchangeModel.koreanName
    val context = LocalContext.current

    Row(Modifier
        .fillMaxWidth()
        .height(50.dp)
        .drawWithContent {
            drawContent()
            clipRect {
                val strokeWidth = Stroke.DefaultMiter
                val y = size.height
                drawLine(
                    brush = SolidColor(Color.LightGray),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Square,
                    start = Offset.Zero.copy(y = y),
                    end = Offset(x = size.width, y = y)
                )
            }
        }
        .clickable {
            val intent = Intent(context, CoinDetailActivity::class.java)
            intent.putExtra("coinKoreanName", koreanName)
            intent.putExtra("coinSymbol", krwExchangeModel.symbol)
            intent.putExtra("openingPrice", krwExchangeModel.opening_price)
            intent.putExtra("isFavorite", isFavorite)
            startForActivityResult.launch(intent)
            (context as MainActivity).overridePendingTransition(
                R.anim.lazy_column_item_slide_left,
                R.anim.none
            )
        }) {

        Column(
            Modifier
                .weight(1f)
                .align(Alignment.Bottom)
        ) {
            Text(
                text = koreanName,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Bottom),
                style = TextStyle(textAlign = TextAlign.Center),
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${krwExchangeModel.symbol}/KRW",
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                style = TextStyle(textAlign = TextAlign.Center, color = Color.Gray),
                overflow = TextOverflow.Ellipsis
            )
        }

        Box(
            modifier = getTradePriceTextModifier(
                preTradePrice,
                curTradePrice
            ).weight(1f)
        ) {
            Text(
                text = getCurTradePriceTextFormat(curTradePrice),
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

        Text(
            text = signedChangeRate
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

        Text(
            text = accTradePrice24h
                .plus("백만"),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight(),
            style = TextStyle(textAlign = TextAlign.Center)
        )
    }
}

@Composable
fun ExchangeScreenLazyColumn(
    exchangeViewModel: ExchangeViewModel = viewModel(),
    startForActivityResult: ActivityResultLauncher<Intent>
) {
    val filteredKrwExchangeList = exchangeViewModel.getFilteredKrwCoinList()
    if (filteredKrwExchangeList.isEmpty() && exchangeViewModel.showFavorite.value) {
        Text(
            text = "등록된 관심코인이 없습니다.",
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    } else if (filteredKrwExchangeList.isEmpty() && !exchangeViewModel.showFavorite.value && exchangeViewModel.searchTextFieldValue.value.isNotEmpty()) {
        Text(
            text = "일치하는 코인이 없습니다.",
            modifier = Modifier
                .padding(0.dp,20.dp,0.dp,0.dp)
                .fillMaxSize(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(
                items = filteredKrwExchangeList
            ) { _, krwCoinListElement ->
                ExchangeScreenLazyColumnItem(
                    krwCoinListElement,
                    exchangeViewModel.preItemArray[exchangeViewModel.krwExchangeModelListPosition[krwCoinListElement.market]
                        ?: 0].tradePrice,
                    exchangeViewModel.favoriteHashMap[krwCoinListElement.market] != null,
                    startForActivityResult
                )

                exchangeViewModel.preItemArray[exchangeViewModel.krwExchangeModelListPosition[krwCoinListElement.market]
                    ?: 0] = krwCoinListElement
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

@Composable
private fun getCurTradePriceTextFormat(curTradePrice: String): String {
    val tempCurTradePrice = curTradePrice.toDouble()
    return if (tempCurTradePrice >= 100.0) {
        Calculator.getDecimalFormat().format(tempCurTradePrice.toInt())
    } else {
        curTradePrice
    }
}