package org.jeonfeel.moeuibit2.ui.main.exchange

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import org.jeonfeel.moeuibit2.MoeuiBitDataStore.isKor
import org.jeonfeel.moeuibit2.MoeuiBitDataStore.usdPrice
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.activities.CoinDetailActivity
import org.jeonfeel.moeuibit2.ui.activities.MainActivity
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.CommonExchangeModel
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.ui.theme.decrease_color
import org.jeonfeel.moeuibit2.ui.theme.increase_color
import org.jeonfeel.moeuibit2.ui.custom.DpToSp
import org.jeonfeel.moeuibit2.ui.custom.drawUnderLine
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.Utils.removeComma
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator
import kotlin.math.round


@Composable
fun ExchangeScreenLazyColumnItem(
    commonExchangeModel: CommonExchangeModel,
    preTradePrice: Double,
    isFavorite: Boolean,
    startForActivityResult: ActivityResultLauncher<Intent>,
    btcPrice: MutableState<Double>,
) {
    val context = LocalContext.current
    val selectedMarket = Utils.getSelectedMarket(commonExchangeModel.market)
    val koreanName = commonExchangeModel.koreanName
    val engName = commonExchangeModel.EnglishName
    val warning = commonExchangeModel.warning
    val symbol = commonExchangeModel.symbol
    val signedChangeRate =
        CurrentCalculator.signedChangeRateCalculator(commonExchangeModel.signedChangeRate)
    val openingPrice = commonExchangeModel.opening_price
    val curTradePrice =
        CurrentCalculator.tradePriceCalculator(commonExchangeModel.tradePrice, selectedMarket)
    val accTradePrice24h =
        CurrentCalculator.accTradePrice24hCalculator(
            commonExchangeModel.accTradePrice24h,
            selectedMarket
        )
    val formattedPreTradePrice =
        CurrentCalculator.tradePriceCalculator(preTradePrice, selectedMarket)
    val btcToKrw = if (selectedMarket == SELECTED_BTC_MARKET) {
        CurrentCalculator.tradePriceCalculator(
            commonExchangeModel.tradePrice * btcPrice.value,
            SELECTED_KRW_MARKET
        )
    } else {
        ""
    }

    val market = if (commonExchangeModel.market.startsWith(SYMBOL_KRW)) {
        "/$SYMBOL_KRW"
    } else if (commonExchangeModel.market.startsWith(SYMBOL_BTC)) {
        "/$SYMBOL_BTC"
    } else {
        ""
    }

    val rateTextColor = when {
        signedChangeRate.toFloat() > 0 -> {
            increase_color
        }
        signedChangeRate.toFloat() < 0 -> {
            decrease_color
        }
        else -> {
            Color.Black
        }
    }

    Row(
        Modifier
            .fillMaxWidth()
            .height(50.dp)
            .drawUnderLine()
            .clickable {
                val intent = Intent(context, CoinDetailActivity::class.java).apply {
                    putExtra(INTENT_KOREAN_NAME, koreanName)
                    putExtra(INTENT_ENG_NAME, engName)
                    putExtra(INTENT_COIN_SYMBOL, symbol)
                    putExtra(INTENT_OPENING_PRICE, openingPrice)
                    putExtra(INTENT_IS_FAVORITE, isFavorite)
                    putExtra(INTENT_MARKET_STATE, selectedMarket)
                    putExtra(INTENT_WARNING, warning)
                }
                startForActivityResult.launch(intent)
                (context as MainActivity).overridePendingTransition(
                    R.anim.lazy_column_item_slide_left,
                    R.anim.none
                )
            }
    ) {

        // 코인명 심볼
        Column(
            Modifier
                .weight(1f)
                .align(Alignment.Bottom)
        ) {
            Text(
                text = buildAnnotatedString {
                    if (warning == CAUTION) {
                        withStyle(
                            style = SpanStyle(
                                color = Color.Magenta,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(context.getString(R.string.exchangeCaution))
                        }
                    }
                    if (isKor) append(koreanName) else append(engName)
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
                text = symbol.plus(market),
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                style = TextStyle(textAlign = TextAlign.Center, color = Color.Gray),
                overflow = TextOverflow.Ellipsis
            )
        }
        // 코인가격
        Box(
            modifier = Modifier
                .padding(0.dp, 4.dp)
                .fillMaxHeight()
                .border(
                    1.dp, color = when {
                        formattedPreTradePrice < curTradePrice -> {
                            increase_color
                        }
                        formattedPreTradePrice > curTradePrice -> {
                            decrease_color
                        }
                        else -> {
                            Color.Transparent
                        }
                    }
                )
                .weight(1f)
        ) {
            TradePrice(
                tradePrice = curTradePrice,
                textColor = rateTextColor,
                btcToKrw = btcToKrw,
                doubleTradePrice = commonExchangeModel.tradePrice,
                selectedMarket = selectedMarket
            )
        }
        // 코인 변동률
        Text(
            text = signedChangeRate
                .plus("%"),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight(),
            style = TextStyle(
                textAlign = TextAlign.Center, color = rateTextColor
            )
        )
        // 거래대금
        volume(accTradePrice24h, commonExchangeModel.accTradePrice24h, commonExchangeModel.market)
    }
}

@Composable
fun TradePrice(
    tradePrice: String,
    textColor: Color,
    btcToKrw: String = "",
    doubleTradePrice: Double,
    selectedMarket: Int
) {
    if (btcToKrw.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = tradePrice,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .wrapContentHeight(),
                style = TextStyle(
                    textAlign = TextAlign.Center, color = textColor
                )
            )
            if (!isKor) {
                AutoSizeText(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    text = " $SYMBOL_USD ${CurrentCalculator.krwToUsd(doubleTradePrice, usdPrice)}",
                    TextStyle(fontSize = DpToSp(13), textAlign = TextAlign.Start),
                    color = Color.Gray
                )
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = tradePrice,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .wrapContentHeight(),
                style = TextStyle(
                    textAlign = TextAlign.Center, color = textColor
                )
            )
            if (btcToKrw == "0.0000") {
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .fillMaxHeight()
                )
            } else {
                if (isKor) {
                    if (selectedMarket == SELECTED_BTC_MARKET) {
                        AutoSizeText(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .fillMaxHeight(), text = btcToKrw.plus(" $SYMBOL_KRW"),
                            TextStyle(fontSize = DpToSp(13), textAlign = TextAlign.End),
                            color = Color.Gray
                        )
                    }
                } else {
                    AutoSizeText(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .fillMaxHeight(), text = "\$ ${
                            CurrentCalculator.krwToUsd(removeComma(btcToKrw).toDouble(), usdPrice)
                        }",
                        TextStyle(fontSize = DpToSp(13), textAlign = TextAlign.Start),
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.volume(volume: String, doubleVolume: Double, market: String) {
    val tempDoubleVolume = round(doubleVolume * 0.000001)
    val unit = if (market.startsWith(SYMBOL_KRW)) {
        stringResource(id = R.string.million)
    } else {
        ""
    }
    Column(
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = volume.plus(unit),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .fillMaxHeight()
                .wrapContentHeight(),
            style = TextStyle(textAlign = TextAlign.Center)
        )
        if (!isKor && market.startsWith(SYMBOL_KRW)) {
            AutoSizeText(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                text = " $SYMBOL_USD ${CurrentCalculator.krwToUsd(tempDoubleVolume, usdPrice)}M",
                TextStyle(fontSize = DpToSp(13), textAlign = TextAlign.Start),
                color = Color.Gray
            )
        }
    }
}