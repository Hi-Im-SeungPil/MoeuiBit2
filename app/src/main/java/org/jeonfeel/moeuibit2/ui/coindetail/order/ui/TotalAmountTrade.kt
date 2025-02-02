package org.jeonfeel.moeuibit2.ui.coindetail.order.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jeonfeel.moeuibit2.constants.BTC_COMMISSION_FEE
import org.jeonfeel.moeuibit2.constants.KRW_COMMISSION_FEE
import org.jeonfeel.moeuibit2.constants.UPBIT_BTC_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.constants.UPBIT_KRW_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.clearFocusOnKeyboardDismiss
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.main.coinsite.item.BYBIT_COLOR
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringForBtc
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringForKRW
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringForQuantity
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator
import org.jeonfeel.moeuibit2.utils.commaFormat
import org.jeonfeel.moeuibit2.utils.eighthDecimal
import org.jeonfeel.moeuibit2.utils.ext.showToast
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun TotalBidTradeDialog(
    dialogState: MutableState<Boolean>,
    userSeedMoney: State<Long>,
    userBTC: State<MyCoin>,
    isKrw: Boolean,
    requestBid: (String, Double, BigDecimal, Double) -> Unit,
    commonExchangeModelState: State<CommonExchangeModel?>,
) {
    val textFieldValue = remember {
        mutableStateOf("")
    }

    val buyingQuantity = if (textFieldValue.value.isNotEmpty()) {
        CurrentCalculator.total(
            currentPrice = commonExchangeModelState.value?.tradePrice ?: BigDecimal.ONE,
            designatePrice = textFieldValue.value.replace(",", "").toDouble(),
            isKrw = isKrw
        )
    } else {
        "0".toBigDecimal()
    }

    val context = LocalContext.current

    if (dialogState.value) {
        Dialog(
            onDismissRequest = {
                textFieldValue.value = ""
                dialogState.value = false
            }
        ) {
            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.White, shape = RoundedCornerShape(15.dp))
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.background(Color.White)) {
                    Text(
                        text = "총액 지정하여 매수",
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .align(alignment = Alignment.CenterHorizontally),
                        style = TextStyle(fontWeight = FontWeight.W600, fontSize = DpToSp(16.dp))
                    )
                    Item(
                        text = "보유",
                        value = if (isKrw) userSeedMoney.value.commaFormat() else userBTC.value.quantity.eighthDecimal(),
                        symbol = if (isKrw) "KRW" else "BTC"
                    )
                    Item(
                        text = "현재가",
                        value = if (isKrw) commonExchangeModelState.value?.tradePrice?.formattedStringForKRW()
                            ?: "0" else commonExchangeModelState.value?.tradePrice?.formattedStringForBtc()
                            ?: "0",
                        symbol = if (isKrw) "KRW" else "BTC"
                    )
                    Item(
                        text = "매수 수량",
                        value = buyingQuantity?.formattedStringForQuantity() ?: "0",
                        symbol = commonExchangeModelState.value?.symbol ?: ""
                    )

                    Divider(
                        Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .height(2.dp), color = Color(BYBIT_COLOR)
                    )

                    ButtonList(
                        textFieldValue = textFieldValue,
                        isKrw = isKrw
                    )

                    Row(modifier = Modifier.padding(top = 10.dp)) {
                        Text(
                            "총액",
                            modifier = Modifier
                                .padding(end = 15.dp)
                                .align(Alignment.CenterVertically)
                        )
                        TransparentTextField(textFieldValue)
                        Text(
                            if (isKrw) " KRW" else " BTC",
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            "취소",
                            modifier = Modifier
                                .weight(2f)
                                .background(
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(999.dp)
                                )
                                .padding(vertical = 15.dp)
                                .noRippleClickable {
                                    dialogState.value = false
                                    textFieldValue.value = ""
                                },
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                                color = Color.Black,
                                fontSize = DpToSp(14.dp)
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "매수",
                            modifier = Modifier
                                .weight(3f)
                                .background(color = Color.Red, shape = RoundedCornerShape(999.dp))
                                .padding(vertical = 15.dp)
                                .noRippleClickable {
                                    if (textFieldValue.value.isEmpty()) return@noRippleClickable

                                    if (conditionCheck(
                                            commonExchangeModelState = commonExchangeModelState,
                                            context = context,
                                            totalPrice = textFieldValue.value
                                                .replace(",", "")
                                                .toBigDecimal(),
                                            userSeedMoney = userSeedMoney,
                                            userBTC = userBTC
                                        )
                                    ) {
                                        requestBid(
                                            commonExchangeModelState.value?.market ?: "",
                                            buyingQuantity?.toDouble() ?: 0.0,
                                            commonExchangeModelState.value?.tradePrice
                                                ?: BigDecimal.ZERO,
                                            textFieldValue.value
                                                .replace(",", "")
                                                .toDouble()
                                        )
                                    }
                                },
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                                fontSize = DpToSp(14.dp),
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Item(
    text: String,
    value: String,
    symbol: String
) {
    Row(modifier = Modifier.padding(top = 10.dp)) {
        Text(
            text,
            modifier = Modifier
                .padding(end = 15.dp)
                .align(Alignment.CenterVertically),
            style = TextStyle(fontSize = DpToSp(15.dp), fontWeight = FontWeight.W600)
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        AutoSizeText(
            text = value,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Text(
            " $symbol",
            modifier = Modifier.align(Alignment.CenterVertically),
            style = TextStyle(fontWeight = FontWeight.W600)
        )
    }
}

@Composable
fun ButtonList(
    isKrw: Boolean,
    textFieldValue: MutableState<String>
) {
    val plusAmountButtonList = remember {
        arrayOf(1, 10, 100, 1_000)
    }

    val plusBTCAmountButtonList = remember {
        arrayOf(0.001, 0.01, 0.1, 1.0)
    }

    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isKrw) {
            plusAmountButtonList.forEach {
                Text(
                    text = "+${it}만",
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .weight(1f)
                        .border(
                            1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(vertical = 5.dp)
                        .noRippleClickable {
                            if (textFieldValue.value.isEmpty()) {
                                textFieldValue.value = (it * 10_000).toString()
                                return@noRippleClickable
                            }

                            textFieldValue.value = textFieldValue.value
                                .replace(",", "")
                                .toBigDecimal()
                                .plus(
                                    (it * 10_000).toBigDecimal()
                                )
                                .toString()
                        },
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        fontSize = DpToSp(10.dp),
                        color = Color.Black
                    )
                )
            }
            Text(
                text = "+1억",
                modifier = Modifier
                    .weight(1f)
                    .border(
                        1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(vertical = 5.dp)
                    .noRippleClickable {
                        if (textFieldValue.value.isEmpty()) {
                            textFieldValue.value = (100_000_000).toString()
                            return@noRippleClickable
                        }

                        textFieldValue.value = textFieldValue.value
                            .replace(",", "")
                            .toBigDecimal()
                            .plus(
                                (100_000_000).toBigDecimal()
                            )
                            .toString()
                    },
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = DpToSp(10.dp),
                    color = Color.Black
                )
            )
        } else {
            plusBTCAmountButtonList.forEach {
                Text(
                    text = "+${it.toDouble()}",
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .weight(1f)
                        .border(
                            1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(vertical = 5.dp)
                        .noRippleClickable {
                            if (textFieldValue.value.isEmpty()) {
                                textFieldValue.value = (it).toString()
                                return@noRippleClickable
                            }

                            textFieldValue.value = textFieldValue.value
                                .replace(",", "")
                                .toBigDecimal()
                                .plus(
                                    (it).toBigDecimal()
                                )
                                .toString()
                        },
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        fontSize = DpToSp(10.dp),
                        color = Color.Black
                    )
                )
            }
        }
    }
}

@Composable
fun RowScope.TransparentTextField(
    value: MutableState<String>,
    placeholder: String = "총액을 입력해 주세요"
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .background(Color.Transparent)
    ) {
        BasicTextField(
            value = value.value,
            onValueChange = { text ->
                val rawValue = text.replace(",", "")
                if (rawValue.matches(Regex("^[0-9]*\\.?[0-9]{0,8}$"))) {
                    value.value = text
                }
            },
            textStyle = TextStyle(
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                fontSize = DpToSp(13.dp), textAlign = TextAlign.End
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clearFocusOnKeyboardDismiss(),
            visualTransformation = NumberCommaTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (value.value.isEmpty()) {
                        Text(
                            text = placeholder,
                            modifier = Modifier.fillMaxWidth(),
                            style = TextStyle(color = Color.Gray, textAlign = TextAlign.End)
                        )
                    }
                }
                innerTextField()
            }
        )
    }
}

fun conditionCheck(
    commonExchangeModelState: State<CommonExchangeModel?>,
    context: Context,
    totalPrice: BigDecimal,
    userSeedMoney: State<Long>,
    userBTC: State<MyCoin>
): Boolean {
    when {
        commonExchangeModelState.value == null -> {
            context.showToast("인터넷 연결을 확인한 후 다시 시도해 주세요.")
            return false
        }

        commonExchangeModelState.value != null && commonExchangeModelState.value?.tradePrice?.toDouble() == 0.0 -> {
            context.showToast("가격이 0원 입니다. 정상화 후 다시 시도해 주세요")
            return false
        }

        !Utils.isNetworkAvailable(context) -> {
            context.showToast("인터넷 연결을 확인한 후 다시 시도해 주세요.")
            return false
        }

        else -> {}
    }

    when {
        (commonExchangeModelState.value?.market ?: "").startsWith(UPBIT_KRW_SYMBOL_PREFIX) -> {
            when {
                totalPrice > (userSeedMoney.value.toDouble().newBigDecimal()
                    .setScale(0, RoundingMode.HALF_UP)) -> {
                    context.showToast("보유하신 KRW가 부족합니다.")
                    return false
                }

                totalPrice.toDouble() < 5000 -> {
                    context.showToast("최소 매수 금액은 5000원 입니다.")
                    return false
                }

                else -> {}
            }
        }

        (commonExchangeModelState.value?.market ?: "").startsWith(UPBIT_BTC_SYMBOL_PREFIX) -> {
            when {
                totalPrice > (userBTC.value.quantity
                    .newBigDecimal(8, RoundingMode.HALF_UP)
                    .setScale(8, RoundingMode.HALF_UP)) -> {
                    context.showToast("보유하신 BTC가 부족합니다.")
                    return false
                }

                totalPrice.toDouble() < 0.00005 -> {
                    context.showToast("최소 매수 금액은 0.00005BTC 입니다.")
                    return false
                }

                else -> {}
            }
        }

        else -> {}
    }

    return true
}

//@Composable
//fun OrderTabQuantitySection(
//    quantityOnValueChanged: (String, Boolean) -> Unit,
//    isBid: Boolean,
//) {
//    val focusRequester = remember { FocusRequester() }
//    val focusState = remember { mutableStateOf(false) }
//
//    Row(
//        modifier = Modifier
//            .padding(top = 15.dp)
//            .fillMaxWidth()
//            .border(1.dp, Color.LightGray, RoundedCornerShape(5.dp)),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(
//            text = "수량",
//            modifier = Modifier
//                .padding(vertical = 10.dp)
//                .padding(start = 10.dp),
//            fontSize = DpToSp(13.dp),
//            color = Color.DarkGray
//        )
//
//        BasicTextField(value = quantity, onValueChange = {
//            val rawValue = it.replace(",", "")
//            if (rawValue.matches(Regex("^[0-9]*\\.?[0-9]{0,8}$"))) {
//                quantityOnValueChanged(rawValue, isBid)
//            }
//        }, singleLine = true,
//            textStyle = TextStyle(
//                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
//                fontSize = DpToSp(13.dp), textAlign = TextAlign.End
//            ),
//            modifier = Modifier
//                .padding(horizontal = 5.dp)
//                .weight(1f)
//                .clearFocusOnKeyboardDismiss()
//                .focusRequester(focusRequester)
//                .onFocusChanged { state ->
//                    focusState.value = state.isFocused
//                },
//            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
//            visualTransformation = NumberCommaTransformation(),
//            decorationBox = { innerTextField ->
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    if (quantity.isEmpty() && !focusState.value) {
//                        Text(
//                            "0",
//                            style = TextStyle(
//                                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
//                                fontSize = DpToSp(dp = 13.dp),
//                                textAlign = TextAlign.End
//                            ),
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                    }
//                    Spacer(modifier = Modifier.weight(1f))
//                    innerTextField()
//                }
//            })
//    }
//}

//@Composable
//@Preview()
//fun TotalAmountTradeBottomSheetPreview() {
//    TotalBidTradeDialog(
//        dialogState = remember { mutableStateOf(true) },
//        userSeedMoney = userSeedMoney,
//        userBTC = userBTC,
//        isKrw = market.isTradeCurrencyKrw(),
//        symbol = commonExchangeModelState.value?.symbol ?: "",
//        currentPrice = commonExchangeModelState.value?.tradePrice,
//        requestBid = requestBid
//    )
//}