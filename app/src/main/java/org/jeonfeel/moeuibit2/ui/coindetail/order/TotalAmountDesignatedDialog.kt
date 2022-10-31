package org.jeonfeel.moeuibit2.ui.coindetail.order

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.CoinDetailViewModel
import org.jeonfeel.moeuibit2.constant.*
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitCoinDetailWebSocket
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.util.EtcUtils
import org.jeonfeel.moeuibit2.util.OneTimeNetworkCheck
import org.jeonfeel.moeuibit2.util.calculator.Calculator
import org.jeonfeel.moeuibit2.util.eighthDecimal
import org.jeonfeel.moeuibit2.util.showToast
import java.text.NumberFormat
import java.util.*
import kotlin.math.round
import kotlin.math.roundToLong

@Composable
fun TotalAmountDesignatedDialog(
    coinDetailViewModel: CoinDetailViewModel,
) {
    val context = LocalContext.current
    if (coinDetailViewModel.askBidDialogState) {
        val interactionSource = remember {
            MutableInteractionSource()
        }
        val marketState = EtcUtils.getSelectedMarket(coinDetailViewModel.market)
        val userSeedMoney = if (marketState == SELECTED_KRW_MARKET) {
            Calculator.getDecimalFormat()
                .format(coinDetailViewModel.userSeedMoney - round(coinDetailViewModel.userSeedMoney * 0.0005).toLong())
        } else {
            val btcPrice =
                (coinDetailViewModel.btcQuantity.value * coinDetailViewModel.currentBTCPrice.value).toLong()
            Calculator.getDecimalFormat()
                .format(btcPrice - round(btcPrice * 0.00025).toLong())
        }

        val userCoinValuable = if (marketState == SELECTED_KRW_MARKET) {
            Calculator.getDecimalFormat()
                .format(round(coinDetailViewModel.userCoinQuantity * coinDetailViewModel.currentTradePriceState))
        } else {
            Calculator.getDecimalFormat()
                .format(round(coinDetailViewModel.userCoinQuantity * coinDetailViewModel.currentTradePriceState * coinDetailViewModel.currentBTCPrice.value))
        }


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
                        text = if (coinDetailViewModel.askBidSelectedTab.value == ASK_BID_SCREEN_BID_TAB) "총액 지정 매수" else "총액 지정 매도",
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
                            modifier = Modifier.weight(1f, true),
                            text = if (coinDetailViewModel.askBidSelectedTab.value == ASK_BID_SCREEN_BID_TAB) userSeedMoney else userCoinValuable,
                            textStyle = TextStyle(fontSize = 18.sp, textAlign = TextAlign.End)
                        )
                        Text(
                            text = " $SYMBOL_KRW",
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
                        text = stringResource(id = R.string.reset),
                        modifier = Modifier
                            .padding(10.dp, 0.dp, 10.dp, 25.dp)
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .padding(10.dp, 10.dp, 10.dp, 10.dp)
                            .clickable(interactionSource = interactionSource,
                                indication = null) {
                                coinDetailViewModel.totalPriceDesignated = ""
                            },
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
                        Text(text = if (coinDetailViewModel.askBidSelectedTab.value == ASK_BID_SCREEN_BID_TAB)
                            stringResource(id = R.string.bid)
                        else
                            stringResource(id = R.string.ask),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if(coinDetailViewModel.totalPriceDesignated.isEmpty()) {
                                        context.showToast("금액을 입력해 주세요.")
                                    } else {
                                        val currentPrice = coinDetailViewModel.currentTradePriceState
                                        val selectedTab = coinDetailViewModel.askBidSelectedTab.value
                                        val userCoin = coinDetailViewModel.userCoinQuantity

                                        if (marketState == SELECTED_KRW_MARKET) {
                                            val localUserSeedMoney = coinDetailViewModel.userSeedMoney
                                            val totalPrice =
                                                coinDetailViewModel.totalPriceDesignated.toLong()
                                            val quantity =
                                                (totalPrice / currentPrice)
                                                    .eighthDecimal()
                                                    .toDouble()
                                            when {
                                                currentPrice == 0.0 -> {
                                                    context.showToast(context.getString(R.string.NETWORK_ERROR))
                                                }
                                                OneTimeNetworkCheck.networkCheck(context) == null -> {
                                                    context.showToast(context.getString(R.string.NO_INTERNET_CONNECTION))
                                                }
                                                UpBitCoinDetailWebSocket.currentSocketState != SOCKET_IS_CONNECTED -> {
                                                    context.showToast(context.getString(R.string.NETWORK_ERROR))
                                                }
                                                totalPrice < 5000 -> {
                                                    context.showToast(context.getString(R.string.notMinimumOrderMessage))
                                                }
                                                selectedTab == ASK_BID_SCREEN_BID_TAB && localUserSeedMoney < totalPrice + round(
                                                    totalPrice * 0.0005).toLong() -> {
                                                    context.showToast(context.getString(R.string.youHaveNoMoneyMessage))
                                                }
                                                selectedTab == ASK_BID_SCREEN_ASK_TAB && coinDetailViewModel.totalPriceDesignated.toLong() > (userCoin * currentPrice).roundToLong() -> {
                                                    context.showToast(context.getString(R.string.youHaveNoCoinMessage))
                                                }
                                                else -> {
                                                    if (selectedTab == ASK_BID_SCREEN_BID_TAB) {
                                                        CoroutineScope(mainDispatcher).launch {
                                                            coinDetailViewModel
                                                                .bidRequest(
                                                                    currentPrice,
                                                                    quantity,
                                                                    totalPrice
                                                                )
                                                                .join()
                                                            context.showToast(context.getString(R.string.completeBidMessage))
                                                        }
                                                    } else {
                                                        CoroutineScope(mainDispatcher).launch {
                                                            coinDetailViewModel
                                                                .askRequest(
                                                                    quantity,
                                                                    totalPrice,
                                                                    currentPrice
                                                                )
                                                                .join()
                                                            context.showToast(context.getString(R.string.completeAskMessage))
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            val localUserSeedMoney =
                                                (coinDetailViewModel.btcQuantity.value * coinDetailViewModel.currentBTCPrice.value).roundToLong()
                                            val currentBtcPrice = coinDetailViewModel.currentBTCPrice.value
                                            val totalPrice =
                                                (coinDetailViewModel.totalPriceDesignated.toLong() / currentBtcPrice)
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
                                                currentBtcPrice <= 0.0  -> {
                                                    context.showToast(context.getString(R.string.NETWORK_ERROR))
                                                }
                                                OneTimeNetworkCheck.networkCheck(context) == null -> {
                                                    context.showToast(context.getString(R.string.NO_INTERNET_CONNECTION))
                                                }
                                                UpBitCoinDetailWebSocket.currentSocketState != SOCKET_IS_CONNECTED -> {
                                                    context.showToast(context.getString(R.string.NETWORK_ERROR))
                                                }
                                                totalPrice < 0.0005 -> {
                                                    context.showToast(context.getString(R.string.notMinimumOrderBtcMessage))
                                                }
                                                selectedTab == ASK_BID_SCREEN_BID_TAB && localUserSeedMoney < totalPrice + (totalPrice * 0.0025).toLong() -> {
                                                    context.showToast(context.getString(R.string.youHaveNoMoneyMessage))
                                                }
                                                selectedTab == ASK_BID_SCREEN_ASK_TAB && coinDetailViewModel.totalPriceDesignated.toLong() >
                                                        (userCoin * currentPrice * currentBtcPrice).roundToLong() -> {
                                                    context.showToast(context.getString(R.string.youHaveNoCoinMessage))
                                                }
                                                else -> {
                                                    if (selectedTab == ASK_BID_SCREEN_BID_TAB) {
                                                        CoroutineScope(mainDispatcher).launch {
                                                            coinDetailViewModel
                                                                .bidRequest(
                                                                    currentPrice,
                                                                    quantity,
                                                                    btcTotalPrice = totalPrice,
                                                                    currentBtcPrice = currentBtcPrice
                                                                )
                                                                .join()
                                                            context.showToast(context.getString(R.string.completeBidMessage))
                                                        }
                                                    } else {
                                                        CoroutineScope(mainDispatcher).launch {
                                                            coinDetailViewModel
                                                                .askRequest(
                                                                    quantity,
                                                                    totalPrice.toLong(),
                                                                    currentPrice,
                                                                    btcTotalPrice = totalPrice
                                                                ).join()
                                                            context.showToast(context.getString(R.string.completeAskMessage))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        coinDetailViewModel.askBidDialogState = false
                                    }
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
