package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.sp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.coindetail.CoinDetailActivity
import org.jeonfeel.moeuibit2.activity.main.MainActivity
import org.jeonfeel.moeuibit2.constant.*
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.exchange.CommonExchangeModel
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.ui.util.drawUnderLine
import org.jeonfeel.moeuibit2.util.EtcUtils
import org.jeonfeel.moeuibit2.util.calculator.CurrentCalculator


@Composable
fun ExchangeScreenLazyColumnItem(
    commonExchangeModel: CommonExchangeModel,
    preTradePrice: Double,
    isFavorite: Boolean,
    startForActivityResult: ActivityResultLauncher<Intent>,
    btcPrice: Double = 0.0,
) {
    val context = LocalContext.current
    val marketState = EtcUtils.getSelectedMarket(commonExchangeModel.market)
    val koreanName = commonExchangeModel.koreanName
    val warning = commonExchangeModel.warning
    val symbol = commonExchangeModel.symbol
    val signedChangeRate =
        CurrentCalculator.signedChangeRateCalculator(commonExchangeModel.signedChangeRate)
    val openingPrice = commonExchangeModel.opening_price
    val curTradePrice =
        CurrentCalculator.tradePriceCalculator(commonExchangeModel.tradePrice, marketState)
    val accTradePrice24h =
        CurrentCalculator.accTradePrice24hCalculator(commonExchangeModel.accTradePrice24h,
            marketState)
    val formattedPreTradePrice = CurrentCalculator.tradePriceCalculator(preTradePrice, marketState)
    val btcToKrw = if (marketState == SELECTED_BTC_MARKET) {
//        Log.e("btc",btcPrice.toString())
        CurrentCalculator.tradePriceCalculator(commonExchangeModel.tradePrice * btcPrice, SELECTED_KRW_MARKET)
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

    val unit = if (commonExchangeModel.market.startsWith(SYMBOL_KRW)) {
        stringResource(id = R.string.million)
    } else {
        ""
    }

    val rateTextColor = when {
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

    Row(
        Modifier
            .fillMaxWidth()
            .height(50.dp)
            .drawUnderLine()
            .clickable {
                val intent = Intent(context, CoinDetailActivity::class.java).apply {
                    putExtra(INTENT_KOREAN_NAME, koreanName)
                    putExtra(INTENT_COIN_SYMBOL, symbol)
                    putExtra(INTENT_OPENING_PRICE, openingPrice)
                    putExtra(INTENT_IS_FAVORITE, isFavorite)
                    putExtra(INTENT_MARKET_STATE, marketState)
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
            TradePrice(tradePrice = curTradePrice, textColor = rateTextColor, btcToKrw)
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
        Text(
            text = accTradePrice24h.plus(unit),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight(),
            style = TextStyle(textAlign = TextAlign.Center)
        )
    }
}

@Composable
fun TradePrice(tradePrice: String, textColor: Color, btcToKrw: String = "") {
    if (btcToKrw.isEmpty()) {
        Text(
            text = tradePrice,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .wrapContentHeight(),
            style = TextStyle(
                textAlign = TextAlign.Center, color = textColor
            )
        )
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
                Spacer(modifier =  Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight())
            } else {
                AutoSizeText(modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight(),text = btcToKrw.plus(" $SYMBOL_KRW"),
                    TextStyle(fontSize = 13.sp, textAlign = TextAlign.End),
                    color = Color.Gray
                )
            }
        }
    }
}