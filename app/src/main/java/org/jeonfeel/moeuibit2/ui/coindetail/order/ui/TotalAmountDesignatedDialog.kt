package org.jeonfeel.moeuibit2.ui.coindetail.order.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.ui.custom.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.decrease_color
import org.jeonfeel.moeuibit2.ui.theme.increase_color
import org.jeonfeel.moeuibit2.utils.OneTimeNetworkCheck
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.eighthDecimal
import org.jeonfeel.moeuibit2.utils.showToast
import java.text.NumberFormat
import java.util.*
import kotlin.math.round
import kotlin.math.roundToLong
import kotlin.reflect.KFunction4
import kotlin.reflect.KFunction5

@Composable
fun TotalAmountDesignatedDialog(
    askBidDialogState: MutableState<Boolean>,
    userSeedMoney: MutableState<Long>,
    btcQuantity: MutableState<Double>,
    currentBTCPrice: MutableState<Double>,
    userCoinQuantity: MutableState<Double>,
    currentTradePriceState: MutableState<Double>,
    askBidSelectedTab: MutableState<Int>,
    totalPriceDesignated: MutableState<String>,
    marketState: Int,
    getCommission: (String) -> Float,
    askRequest: KFunction4<Double, Long, Double, Double, Job>,
    bidRequest: KFunction5<Double, Double, Long, Double, Double, Job>,
) {
    val context = LocalContext.current
    if (askBidDialogState.value) {
        val interactionSource = remember {
            MutableInteractionSource()
        }
        val userSeedMoneyFormatting = if (marketState == SELECTED_KRW_MARKET) {
            Calculator.getDecimalFormat()
                .format(userSeedMoney.value)
        } else {
            val btcPrice =
                (btcQuantity.value * currentBTCPrice.value).toLong()
            Calculator.getDecimalFormat()
                .format(btcPrice)
        }

        val userCoinValuable = if (marketState == SELECTED_KRW_MARKET) {
            Calculator.getDecimalFormat()
                .format(round(userCoinQuantity.value * currentTradePriceState.value))
        } else {
            Calculator.getDecimalFormat()
                .format(round(userCoinQuantity.value * currentTradePriceState.value * currentBTCPrice.value))
        }
        val krwFeeState = remember {
            mutableStateOf(0.0)
        }


        Dialog(onDismissRequest = { askBidDialogState.value = false }) {
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
                        text = if (askBidSelectedTab.value == ASK_BID_SCREEN_BID_TAB) "총액 지정 매수" else "총액 지정 매도",
                        modifier = Modifier
                            .padding(0.dp, 20.dp)
                            .fillMaxWidth(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = DpToSp(25.dp),
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
                            style = TextStyle(fontSize = DpToSp(18.dp))
                        )
                        AutoSizeText(
                            modifier = Modifier.weight(1f, true),
                            text = if (askBidSelectedTab.value == ASK_BID_SCREEN_BID_TAB) userSeedMoneyFormatting else userCoinValuable,
                            textStyle = TextStyle(
                                fontSize = DpToSp(18.dp),
                                textAlign = TextAlign.End
                            )
                        )
                        Text(
                            text = " $SYMBOL_KRW",
                            style = TextStyle(
                                fontSize = DpToSp(18.dp),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(10.dp, 10.dp, 10.dp, 10.dp)
                            .fillMaxWidth()
                    ) {
                        TextField(
                            value = totalPriceDesignated.value, onValueChange = {
                                if (it == "") {
                                    totalPriceDesignated.value = ""
                                    krwFeeState.value = 0.0
                                } else {
                                    if (it.toLongOrNull() != null) {
                                        totalPriceDesignated.value = it
                                        val bidFee = if (marketState == SELECTED_KRW_MARKET) {
                                            getCommission(PREF_KEY_KRW_BID_COMMISSION)
                                        } else {
                                            getCommission(PREF_KEY_BTC_BID_COMMISSION)
                                        }
                                        krwFeeState.value =
                                            totalPriceDesignated.value.toDouble() + (totalPriceDesignated.value.toFloat() * (bidFee * 0.01))
                                    } else {
                                        totalPriceDesignated.value = ""
                                        krwFeeState.value = 0.0
                                        context.showToast(context.getString(R.string.onlyNumberMessage))
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f, true)
                                .align(Alignment.CenterVertically),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(
                                fontSize = DpToSp(18.dp),
                                textAlign = TextAlign.End
                            ),
                            visualTransformation = NumbersCommaTransformation(),
                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                backgroundColor = colorResource(id = R.color.design_default_color_background),
                            )
                        )
                        Text(
                            text = " $SYMBOL_KRW",
                            style = TextStyle(
                                fontSize = DpToSp(18.dp),
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }

                    if (askBidSelectedTab.value == ASK_BID_SCREEN_BID_TAB) {
                        Text(
                            text = "지정금액 + 수수료 = ${
                                Calculator.getDecimalFormat()
                                    .format(round(krwFeeState.value))
                            } ".plus(SYMBOL_KRW),
                            style = TextStyle(color = Color.Gray, fontSize = DpToSp(15.dp)),
                            modifier = Modifier.padding(10.dp, 10.dp, 10.dp, 20.dp)
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
                                        if (totalPriceDesignated.value.isEmpty()) {
                                            totalPriceDesignated.value =
                                                valueArray[i].toString()
                                        } else {
                                            totalPriceDesignated.value =
                                                (totalPriceDesignated.value.toLong() + valueArray[i]).toString()
                                        }
                                        val bidFee = if (marketState == SELECTED_KRW_MARKET) {
                                            getCommission(PREF_KEY_KRW_BID_COMMISSION)
                                        } else {
                                            getCommission(PREF_KEY_BTC_BID_COMMISSION)
                                        }
                                        krwFeeState.value =
                                            totalPriceDesignated.value.toDouble() + (totalPriceDesignated.value.toFloat() * (bidFee * 0.01))
                                    },
                                textAlign = TextAlign.Center
                            )
                            if (i != textArray.lastIndex) {
                                Text(
                                    text = "",
                                    style = TextStyle(
                                        color = Color.LightGray,
                                        fontSize = DpToSp(18.dp)
                                    ),
                                    modifier = Modifier
                                        .width(1.dp)
                                        .border(1.dp, color = Color.LightGray)
                                )
                            }
                        }
                    }
                    Text(
                        text = stringResource(id = R.string.reset),
                        modifier = Modifier
                            .padding(10.dp, 0.dp, 10.dp, 25.dp)
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .padding(10.dp, 10.dp, 10.dp, 10.dp)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                totalPriceDesignated.value = ""
                                krwFeeState.value = 0.0
                            },
                        style = TextStyle(color = Color.White),
                        textAlign = TextAlign.Center
                    )

                    Divider(modifier = Modifier.fillMaxWidth(), Color.LightGray, 1.dp)
                    Row {
                        Text(
                            text = stringResource(id = R.string.commonCancel), modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    askBidDialogState.value = false
                                }
                                .padding(0.dp, 10.dp),
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = DpToSp(18.dp),
                                textAlign = TextAlign.Center
                            )
                        )
                        Text(
                            text = "", modifier = Modifier
                                .width(1.dp)
                                .border(0.5.dp, Color.LightGray)
                                .padding(0.dp, 10.dp), fontSize = DpToSp(18.dp)
                        )
                        Text(text = if (askBidSelectedTab.value == ASK_BID_SCREEN_BID_TAB)
                            stringResource(id = R.string.bid)
                        else
                            stringResource(id = R.string.ask),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (totalPriceDesignated.value.isEmpty()) {
                                        context.showToast("금액을 입력해 주세요.")
                                    } else {
                                        val currentPrice =
                                            currentTradePriceState.value
                                        val selectedTab =
                                            askBidSelectedTab.value
                                        val userCoin = userCoinQuantity.value

                                        if (marketState == SELECTED_KRW_MARKET) {
                                            val localUserSeedMoney =
                                                userSeedMoney.value
                                            val totalPrice =
                                                totalPriceDesignated.value.toLong()
                                            val quantity =
                                                (totalPrice / currentPrice)
                                                    .eighthDecimal()
                                                    .toDouble()
                                            val bidCommission = getCommission(PREF_KEY_KRW_BID_COMMISSION)
                                            when {
                                                currentPrice == 0.0 -> {
                                                    context.showToast(context.getString(R.string.NETWORK_ERROR))
                                                }
                                                OneTimeNetworkCheck.networkCheck(context) == null -> {
                                                    context.showToast(context.getString(R.string.NO_INTERNET_CONNECTION))
                                                }
                                                UpBitTickerWebSocket.currentSocketState != SOCKET_IS_CONNECTED -> {
                                                    context.showToast(context.getString(R.string.NETWORK_ERROR))
                                                }
                                                totalPrice < 5000 -> {
                                                    context.showToast(context.getString(R.string.notMinimumOrderMessage))
                                                }
                                                selectedTab == ASK_BID_SCREEN_BID_TAB && localUserSeedMoney < totalPrice + round(
                                                    totalPrice * (bidCommission * 0.01)
                                                ).toLong() -> {
                                                    context.showToast(context.getString(R.string.youHaveNoMoneyMessage))
                                                }
                                                selectedTab == ASK_BID_SCREEN_ASK_TAB && totalPriceDesignated.value.toLong() > (userCoin * currentPrice).roundToLong() -> {
                                                    context.showToast(context.getString(R.string.youHaveNoCoinMessage))
                                                }
                                                else -> {
                                                    if (selectedTab == ASK_BID_SCREEN_BID_TAB) {
                                                        CoroutineScope(mainDispatcher).launch {
                                                            bidRequest(
                                                                currentPrice,
                                                                quantity,
                                                                totalPrice,
                                                                0.0,
                                                                0.0
                                                            )
                                                                .join()
                                                            context.showToast(context.getString(R.string.completeBidMessage))
                                                            askBidDialogState.value = false
                                                        }
                                                    } else {
                                                        CoroutineScope(mainDispatcher).launch {
                                                            askRequest(
                                                                quantity,
                                                                totalPrice,
                                                                currentPrice,
                                                                0.0
                                                            )
                                                                .join()
                                                            context.showToast(context.getString(R.string.completeAskMessage))
                                                            askBidDialogState.value = false
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            val bidCommission =
                                                getCommission(PREF_KEY_BTC_BID_COMMISSION)
                                            val currentBtcPrice =
                                                currentBTCPrice.value
                                            val localUserSeedMoney =
                                                (btcQuantity.value * currentBtcPrice).roundToLong()
                                            val totalPrice =
                                                (totalPriceDesignated.value.toLong() / currentBtcPrice)
                                                    .eighthDecimal()
                                                    .toDouble()
                                            val quantity =
                                                (totalPrice / currentPrice)
                                                    .eighthDecimal()
                                                    .toDouble()
                                            when {
                                                currentPrice == 0.0 -> {
                                                    context.showToast(context.getString(R.string.NETWORK_ERROR))
                                                }
                                                currentBtcPrice <= 0.0 -> {
                                                    context.showToast(context.getString(R.string.NETWORK_ERROR))
                                                }
                                                OneTimeNetworkCheck.networkCheck(context) == null -> {
                                                    context.showToast(context.getString(R.string.NO_INTERNET_CONNECTION))
                                                }
                                                UpBitTickerWebSocket.currentSocketState != SOCKET_IS_CONNECTED -> {
                                                    context.showToast(context.getString(R.string.NETWORK_ERROR))
                                                }
                                                totalPrice < 0.0005 -> {
                                                    context.showToast(context.getString(R.string.notMinimumOrderBtcMessage))
                                                }
                                                selectedTab == ASK_BID_SCREEN_BID_TAB && localUserSeedMoney < ((totalPrice + (totalPrice * (bidCommission * 0.01))) * currentBtcPrice).toLong() -> {
                                                    context.showToast(context.getString(R.string.youHaveNoMoneyMessage))
                                                }
                                                selectedTab == ASK_BID_SCREEN_ASK_TAB && totalPriceDesignated.value.toLong() >
                                                        (userCoin * currentPrice * currentBtcPrice).roundToLong() -> {
                                                    context.showToast(context.getString(R.string.youHaveNoCoinMessage))
                                                }
                                                else -> {
                                                    if (selectedTab == ASK_BID_SCREEN_BID_TAB) {
                                                        CoroutineScope(mainDispatcher).launch {
                                                            bidRequest(
                                                                currentPrice,
                                                                quantity,
                                                                0L,
                                                                totalPrice,
                                                                currentBtcPrice
                                                            )
                                                                .join()
                                                            context.showToast(context.getString(R.string.completeBidMessage))
                                                            askBidDialogState.value = false
                                                        }
                                                    } else {
                                                        CoroutineScope(mainDispatcher).launch {
                                                            askRequest(
                                                                quantity,
                                                                totalPrice.toLong(),
                                                                currentPrice,
                                                                totalPrice
                                                            )
                                                                .join()
                                                            context.showToast(context.getString(R.string.completeAskMessage))
                                                            askBidDialogState.value = false
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                .background(if (askBidSelectedTab.value == 1) increase_color else decrease_color)
                                .padding(0.dp, 10.dp),
                            style = TextStyle(
                                color = Color.White,
                                fontSize = DpToSp(18.dp),
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
        }
    }
}

class NumbersCommaTransformation : VisualTransformation {
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
