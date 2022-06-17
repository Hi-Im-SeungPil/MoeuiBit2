package org.jeonfeel.moeuibit2.ui.coindetail.order

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.CoinDetailViewModel
import org.jeonfeel.moeuibit2.constant.SOCKET_IS_CONNECTED
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitCoinDetailWebSocket
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.ui.mainactivity.exchange.clearFocusOnKeyboardDismiss
import org.jeonfeel.moeuibit2.util.Calculator
import org.jeonfeel.moeuibit2.util.OneTimeNetworkCheck
import org.jeonfeel.moeuibit2.util.showToast
import java.text.NumberFormat
import java.util.*
import kotlin.math.round

@Composable
fun TotalAmountDesignatedDialog(
    coinDetailViewModel: CoinDetailViewModel,
    currentPrice: Double,
) {
    val context = LocalContext.current
    if (coinDetailViewModel.askBidDialogState) {
        val userSeedMoney = Calculator.getDecimalFormat()
            .format(coinDetailViewModel.userSeedMoney - round(coinDetailViewModel.userSeedMoney * 0.0005).toLong())
        val userCoinValuable = Calculator.getDecimalFormat()
            .format(round(coinDetailViewModel.userCoinQuantity * coinDetailViewModel.currentTradePriceState))

        Dialog(onDismissRequest = { coinDetailViewModel.askBidDialogState = false }) {
            Card(
                modifier = Modifier
                    .padding(20.dp, 0.dp)
                    .wrapContentSize()
            ) {
                Column(
                    Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                ) {
                    Text(
                        text = if (coinDetailViewModel.askBidSelectedTab.value == 1) "총액 지정 매수" else "총액 지정 매도",
                        modifier = Modifier
                            .padding(0.dp, 20.dp)
                            .fillMaxWidth(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Row(
                        modifier = Modifier
                            .padding(10.dp, 20.dp, 10.dp, 20.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "거래 가능",
                            style = TextStyle(fontSize = 18.sp)
                        )
                        AutoSizeText(
                            text = if (coinDetailViewModel.askBidSelectedTab.value == 1) userSeedMoney else userCoinValuable,
                            modifier = Modifier.weight(1f, true),
                            textStyle = TextStyle(fontSize = 18.sp, textAlign = TextAlign.End)
                        )
                        Text(
                            text = " KRW",
                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(10.dp, 10.dp, 10.dp, 20.dp)
                            .fillMaxWidth()
                    ) {
                        TextField(
                            value = coinDetailViewModel.totalPriceDesignated, onValueChange = {
                                if (it == "") {
                                    coinDetailViewModel.totalPriceDesignated = ""
                                } else {
                                    if (it.toLongOrNull() != null) {
                                        coinDetailViewModel.totalPriceDesignated = it
                                    } else {
                                        coinDetailViewModel.totalPriceDesignated = ""
                                        context.showToast("숫자만 입력 가능합니다.")
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f, true),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(fontSize = 18.sp, textAlign = TextAlign.End),
                            visualTransformation = NumberCommaTransformation(),
                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                backgroundColor = colorResource(id = R.color.design_default_color_background),
                            )
                        )
                        Text(
                            text = " KRW",
                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(10.dp, 10.dp, 10.dp, 20.dp)
                            .fillMaxWidth()
                    ) {
                        val textArray = arrayOf("+10만", "+100만", "+500만", "+1000만")
                        val valueArray = arrayOf(100_000, 1_000_000, 5_000_000, 10_000_000)
                        for (i in textArray.indices) {
                            Text(
                                text = textArray[i], modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        if (coinDetailViewModel.totalPriceDesignated.isEmpty()) {
                                            coinDetailViewModel.totalPriceDesignated =
                                                valueArray[i].toString()
                                        } else {
                                            coinDetailViewModel.totalPriceDesignated =
                                                (coinDetailViewModel.totalPriceDesignated.toLong() + valueArray[i]).toString()
                                        }
                                    },
                                textAlign = TextAlign.Center
                            )
                            if (i != textArray.lastIndex) {
                                Text(
                                    text = "",
                                    style = TextStyle(color = Color.LightGray, fontSize = 18.sp),
                                    modifier = Modifier
                                        .width(1.dp)
                                        .border(1.dp, color = Color.LightGray)
                                )
                            }
                        }
                    }
                    Text(
                        text = "초기화",
                        modifier = Modifier
                            .padding(10.dp, 0.dp, 10.dp, 25.dp)
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .padding(10.dp, 10.dp, 10.dp, 10.dp)
                            .clickable { coinDetailViewModel.totalPriceDesignated = "" },
                        style = TextStyle(color = Color.White),
                        textAlign = TextAlign.Center
                    )

                    Divider(modifier = Modifier.fillMaxWidth(), Color.LightGray, 0.5.dp)
                    Row {
                        Text(
                            text = stringResource(id = R.string.commonCancel), modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    coinDetailViewModel.askBidDialogState = false
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
                        Text(text = if (coinDetailViewModel.askBidSelectedTab.value == 1) "매수" else "매도",
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    val totalPrice = coinDetailViewModel.totalPriceDesignated.toLong()
                                    val localUserSeedMoney = coinDetailViewModel.userSeedMoney
                                    val quantity = String.format("%.8f",totalPrice / currentPrice).toDouble()
                                    Log.d("totalPriceDesignated","=> $totalPrice")
                                    Log.d("localCurrentPrice","=> $currentPrice")
                                    Log.d("quantity","=> $quantity")
                                    when {
                                        totalPrice < 5000 -> {
                                            context.showToast("주문 가능한 최소금액은 5,000KRW 입니다.")
                                        }
                                        OneTimeNetworkCheck.networkCheck(context) == null -> {
                                            context.showToast("네트워크 상태를 확인해 주세요.")
                                        }
                                        UpBitCoinDetailWebSocket.currentSocketState != SOCKET_IS_CONNECTED -> {
                                            context.showToast("네트워크 오류 입니다.")
                                        }
                                        coinDetailViewModel.askBidSelectedTab.value == 1 && localUserSeedMoney < totalPrice + round(totalPrice * 0.0005) -> {
                                            context.showToast("주문 가능 금액이 부족합니다.")
                                        }
                                        coinDetailViewModel.askBidSelectedTab.value == 2 && coinDetailViewModel.userCoinQuantity < quantity -> {
                                            context.showToast("매도 가능 수량이 부족합니다.")
                                        }
                                        else -> {
                                            CoroutineScope(Dispatchers.Main).launch {
                                                if(coinDetailViewModel.askBidSelectedTab.value == 1) {
                                                    coinDetailViewModel.bidRequest(currentPrice,quantity,totalPrice).join()
                                                    context.showToast("매수가 완료 되었습니다.")
                                                } else if(coinDetailViewModel.askBidSelectedTab.value == 2) {
                                                    coinDetailViewModel.askRequest(quantity,totalPrice,currentPrice).join()
                                                    context.showToast("매도가 완료 되었습니다.")
                                                }
                                            }
                                        }
                                    }

                                    coinDetailViewModel.askBidDialogState = false
                                }
                                .background(if (coinDetailViewModel.askBidSelectedTab.value == 1) Color.Red else Color.Blue)
                                .padding(0.dp, 10.dp),
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
        }
    }
}

class NumberCommaTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        var result = AnnotatedString(text.text.toLongOrNull().formatWithComma())
        if (result.text == "입력") {
            result = AnnotatedString(
                text.text.toLongOrNull().formatWithComma(),
                spanStyle = SpanStyle(Color.LightGray)
            )
        }
        return TransformedText(
            text = result,
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return text.text.toLongOrNull().formatWithComma().length
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return text.length
                }
            }
        )
    }
}

fun Long?.formatWithComma(): String {
    return if (this == null) {
        "입력"
    } else {
        NumberFormat.getNumberInstance(Locale.KOREA).format(this)
    }
}
