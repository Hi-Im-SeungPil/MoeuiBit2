package org.jeonfeel.moeuibit2.ui.mainactivity.portfolio

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
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.coindetail.CoinDetailActivity
import org.jeonfeel.moeuibit2.activity.main.MainActivity
import org.jeonfeel.moeuibit2.constant.*
import org.jeonfeel.moeuibit2.util.calculator.Calculator
import org.jeonfeel.moeuibit2.util.calculator.CurrentCalculator
import org.jeonfeel.moeuibit2.util.secondDecimal

/**
 * 포트폴리오 아이템 눌렀을 때 나오는 다이얼로그
 */
@Composable
fun UserHoldCoinLazyColumnItemDialog(
    dialogState: MutableState<Boolean>,
    koreanName: String,
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
        Color.Red
    } else if (openingPrice > currentPrice) {
        Color.Blue
    } else {
        Color.Black
    }

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
                    text = koreanName.plus(stringResource(id = R.string.order)),
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
                        text = CurrentCalculator.tradePriceCalculator(currentPrice,marketState),
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
                        text = "  $SYMBOL_KRW",
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
                                val intent = Intent(context, CoinDetailActivity::class.java)
                                intent.putExtra(INTENT_KOREAN_NAME, koreanName)
                                intent.putExtra(INTENT_COIN_SYMBOL, symbol)
                                intent.putExtra(INTENT_OPENING_PRICE, openingPrice)
                                intent.putExtra(INTENT_IS_FAVORITE, isFavorite != null)
                                intent.putExtra(INTENT_WARNING, warning)
                                intent.putExtra(INTENT_MARKET_STATE,marketState)
                                startForActivityResult.launch(intent)
                                (context as MainActivity).overridePendingTransition(
                                    R.anim.lazy_column_item_slide_left,
                                    R.anim.none
                                )
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