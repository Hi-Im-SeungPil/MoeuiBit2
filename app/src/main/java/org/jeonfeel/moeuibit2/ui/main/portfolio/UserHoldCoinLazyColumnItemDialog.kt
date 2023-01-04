package org.jeonfeel.moeuibit2.ui.main.portfolio

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.MoeuiBitDataStore.isKor
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.activity.CoinDetailActivity
import org.jeonfeel.moeuibit2.ui.activity.MainActivity
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.ui.theme.decrease_color
import org.jeonfeel.moeuibit2.ui.theme.increase_color
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator
import org.jeonfeel.moeuibit2.utils.secondDecimal
import org.jeonfeel.moeuibit2.utils.showToast

/**
 * 포트폴리오 아이템 눌렀을 때 나오는 다이얼로그
 */
@Composable
fun UserHoldCoinLazyColumnItemDialog(
    dialogState: MutableState<Boolean>,
    koreanName: String,
    engName: String,
    currentPrice: Double,
    symbol: String,
    openingPrice: Double,
    isFavorite: Int?,
    warning: String,
    marketState:Int,
    startForActivityResult: ActivityResultLauncher<Intent>,
) {
    val context = LocalContext.current
    val textColor = if (openingPrice < currentPrice) {
        increase_color
    } else if (openingPrice > currentPrice) {
        decrease_color
    } else {
        Color.Black
    }
    var name = if (isKor) koreanName else engName
    if(name.startsWith("[BTC]")) {
        name = name.substring(5)
    }
    val tradePrice = CurrentCalculator.tradePriceCalculator(currentPrice,marketState)

    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier
                .padding(40.dp, 0.dp)
                .wrapContentSize()
        ) {
            Column(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {
                Text(
                    text = name.plus(stringResource(id = R.string.order)),
                    modifier = Modifier
                        .padding(0.dp, 20.dp)
                        .fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Row {
                    Text(
                        text = stringResource(id = R.string.currentPrice),
                        modifier = Modifier
                            .padding(20.dp, 20.dp, 0.dp, 20.dp)
                            .wrapContentWidth(),
                        style = TextStyle(fontSize = 18.sp)
                    )
                    Text(
                        text = tradePrice,
                        modifier = Modifier
                            .padding(0.dp, 20.dp)
                            .weight(1f, true),
                        style = TextStyle(
                            color = textColor,
                            fontSize = 18.sp,
                            textAlign = TextAlign.End
                        )
                    )
                    Text(
                        text = if(marketState == SELECTED_KRW_MARKET)"  $SYMBOL_KRW" else "  $SYMBOL_BTC" ,
                        modifier = Modifier
                            .padding(0.dp, 20.dp, 20.dp, 20.dp)
                            .wrapContentWidth(),
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                if (!isKor && marketState == SELECTED_KRW_MARKET) {
                    Text(
                        text = "= \$ ${
                            CurrentCalculator.krwToUsd(Utils.removeComma(tradePrice).toDouble(),
                                MoeuiBitDataStore.usdPrice)
                        }",
                        modifier = Modifier
                            .padding(0.dp, 3.dp, 20.dp, 20.dp)
                            .align(Alignment.End),
                        style = TextStyle(
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    )
                }

                Row {
                    Text(
                        text = stringResource(id = R.string.netChange),
                        modifier = Modifier
                            .padding(20.dp, 20.dp, 0.dp, 20.dp)
                            .wrapContentWidth(),
                        style = TextStyle(fontSize = 18.sp)
                    )
                    Text(
                        text =
                        Calculator.orderBookRateCalculator(openingPrice, currentPrice)
                            .secondDecimal().plus("%"),
                        modifier = Modifier
                            .padding(0.dp, 20.dp, 20.dp, 40.dp)
                            .weight(1f, true),
                        style = TextStyle(
                            color = textColor,
                            fontSize = 18.sp,
                            textAlign = TextAlign.End
                        )
                    )
                }
                Divider(modifier = Modifier.fillMaxWidth(), Color.LightGray, 0.5.dp)
                Row {
                    Text(
                        text = stringResource(id = R.string.commonCancel), modifier = Modifier
                            .weight(1f)
                            .clickable {
                                dialogState.value = false
                            }
                            .padding(0.dp, 10.dp),
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        text = "", modifier = Modifier
                            .width(0.5.dp)
                            .border(0.5.dp, Color.LightGray)
                            .padding(0.dp, 10.dp), fontSize = 18.sp
                    )
                    Text(text = stringResource(id = R.string.commonMove),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (!koreanName.isNullOrEmpty()) {
                                    val intent = Intent(context, CoinDetailActivity::class.java)
                                    intent.putExtra(INTENT_KOREAN_NAME, name)
                                    intent.putExtra(INTENT_ENG_NAME,name)
                                    intent.putExtra(INTENT_COIN_SYMBOL, symbol)
                                    intent.putExtra(INTENT_OPENING_PRICE, openingPrice)
                                    intent.putExtra(INTENT_IS_FAVORITE, isFavorite != null)
                                    intent.putExtra(INTENT_WARNING, warning)
                                    intent.putExtra(INTENT_MARKET_STATE, marketState)
                                    startForActivityResult.launch(intent)
                                    (context as MainActivity).overridePendingTransition(
                                        R.anim.lazy_column_item_slide_left,
                                        R.anim.none
                                    )
                                } else {
                                    context.showToast(context.getString(R.string.doNotTradeMessage))
                                }
                                dialogState.value = false
                            }
                            .padding(0.dp, 10.dp),
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}