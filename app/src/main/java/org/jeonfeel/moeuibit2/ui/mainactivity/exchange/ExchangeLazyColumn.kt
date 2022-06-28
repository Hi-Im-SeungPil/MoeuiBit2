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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.coindetail.CoinDetailActivity
import org.jeonfeel.moeuibit2.activity.main.MainActivity
import org.jeonfeel.moeuibit2.activity.main.MainViewModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.KrwExchangeModel
import org.jeonfeel.moeuibit2.util.calculator.Calculator
import org.jeonfeel.moeuibit2.util.calculator.ExchangeCalculator
import org.jeonfeel.moeuibit2.util.commaFormat

@Composable
fun ExchangeScreenLazyColumnItem(
    krwExchangeModel: KrwExchangeModel,
    preTradePrice: Double,
    isFavorite: Boolean,
    startForActivityResult: ActivityResultLauncher<Intent>,
) {
    val context = LocalContext.current
    val koreanName = krwExchangeModel.koreanName
    val warning = krwExchangeModel.warning
    val symbol = krwExchangeModel.symbol
    val openingPrice = krwExchangeModel.opening_price
    val signedChangeRate =
        ExchangeCalculator.signedChangeRateCalculator(krwExchangeModel.signedChangeRate)
    val curTradePrice = ExchangeCalculator.tradePriceCalculator(krwExchangeModel.tradePrice)
    val accTradePrice24h =
        ExchangeCalculator.accTradePrice24hCalculator(krwExchangeModel.accTradePrice24h)
    val formattedPreTradePrice = ExchangeCalculator.tradePriceCalculator(preTradePrice)

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
            val intent = Intent(context, CoinDetailActivity::class.java).apply {
                putExtra("coinKoreanName", koreanName)
                putExtra("coinSymbol", symbol)
                putExtra("openingPrice", openingPrice)
                putExtra("isFavorite", isFavorite)
                putExtra("warning", warning)
            }
            startForActivityResult.launch(intent)
            (context as MainActivity).overridePendingTransition(
                R.anim.lazy_column_item_slide_left,
                R.anim.none
            )
        }
    ) {

        Column(
            Modifier
                .weight(1f)
                .align(Alignment.Bottom)
        ) {
            Text(
                text = buildAnnotatedString {
                    if (warning == "CAUTION") {
                        withStyle(
                            style = SpanStyle(
                                color = Color.Magenta,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("[유]")
                        }
                    }
                    append(koreanName)
                },
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Bottom),
                style = TextStyle(textAlign = TextAlign.Center),
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${symbol}/KRW",
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
            modifier = Modifier
                .padding(0.dp, 4.dp)
                .fillMaxHeight()
                .border(
                    1.dp, color = when {
                        formattedPreTradePrice < curTradePrice -> {
                            Color.Red
                        }
                        formattedPreTradePrice > curTradePrice -> {
                            Color.Blue
                        }
                        else -> {
                            Color.Transparent
                        }
                    }
                )
                .weight(1f)
        ) {
            Text(
                text = curTradePrice,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .wrapContentHeight(),
                style = TextStyle(
                    textAlign = TextAlign.Center, color = when {
                        signedChangeRate.toFloat() > 0 -> {
                            Color.Red
                        }
                        signedChangeRate.toFloat() < 0 -> {
                            Color.Blue
                        }
                        else -> {
                            Color.Black
                        }
                    }
                )
            )
        }

        Text(
            text = signedChangeRate
                .plus("%"),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight(),
            style = TextStyle(
                textAlign = TextAlign.Center, color = when {
                    signedChangeRate.toFloat() > 0 -> {
                        Color.Red
                    }
                    signedChangeRate.toFloat() < 0 -> {
                        Color.Blue
                    }
                    else -> {
                        Color.Black
                    }
                }
            )
        )

        Text(
            text = accTradePrice24h
                .plus(stringResource(id = R.string.million)),
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
    mainViewModel: MainViewModel = viewModel(),
    startForActivityResult: ActivityResultLauncher<Intent>
) {
    val filteredKrwExchangeList = mainViewModel.getFilteredKrwCoinList()
    when {
        filteredKrwExchangeList.isEmpty() && mainViewModel.showFavorite.value -> {
            Text(
                text = stringResource(id = R.string.noFavorite),
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        filteredKrwExchangeList.isEmpty() && !mainViewModel.showFavorite.value && mainViewModel.searchTextFieldValue.value.isNotEmpty() -> {
            Text(
                text = stringResource(id = R.string.noSearchingCoin),
                modifier = Modifier
                    .padding(0.dp, 20.dp, 0.dp, 0.dp)
                    .fillMaxSize(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        else -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(
                    items = filteredKrwExchangeList
                ) { _, krwCoinListElement ->
                    ExchangeScreenLazyColumnItem(
                        krwCoinListElement,
                        mainViewModel.preItemArray[mainViewModel.krwExchangeModelListPosition[krwCoinListElement.market]
                            ?: 0].tradePrice,
                        mainViewModel.favoriteHashMap[krwCoinListElement.market] != null,
                        startForActivityResult
                    )
                    mainViewModel.preItemArray[mainViewModel.krwExchangeModelListPosition[krwCoinListElement.market]
                        ?: 0] = krwCoinListElement
                }
            }
        }
    }
}