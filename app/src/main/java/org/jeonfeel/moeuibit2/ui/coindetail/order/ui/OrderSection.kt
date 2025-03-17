package org.jeonfeel.moeuibit2.ui.coindetail.order.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.local.room.entity.TransactionInfo
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.clearFocusOnKeyboardDismiss
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.theme.newtheme.coindetail.boxTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.coindetail.unSelectedOrderTabColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.coindetail.unselectedOrderTabTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonDividerColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonFallColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonHintTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonRiseColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonUnSelectedColor
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedString
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringForQuantity
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator
import org.jeonfeel.moeuibit2.utils.commaFormat
import org.jeonfeel.moeuibit2.utils.eighthDecimal
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun OrderSection(
    orderTabState: MutableState<OrderTabState>,
    userSeedMoney: State<Double>,
    userBTC: State<MyCoin>,
    isKrw: Boolean,
    symbol: String,
    currentPrice: BigDecimal?,
    updateBidCoinQuantity: (Int) -> Unit,
    updateAskCoinQuantity: (Int) -> Unit,
    bidQuantity: String,
    askQuantity: String,
    quantityOnValueChanged: (String, Boolean) -> Unit,
    getBidTotalPrice: () -> String,
    getAskTotalPrice: () -> String,
    requestBid: () -> Unit,
    requestAsk: () -> Unit,
    dropdownLabelList: List<String>,
    askSelectedText: String,
    bidSelectedText: String,
    userCoin: State<MyCoin>,
    transactionInfoList: List<TransactionInfo>,
    getTransactionInfoList: (String) -> Unit,
    market: String,
    totalBidDialogState: MutableState<Boolean>,
    totalAskDialogState: MutableState<Boolean>,
    btcPrice: State<BigDecimal>,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        OrderTabSection(orderTabState = orderTabState)
        Box(modifier = Modifier.padding(10.dp)) {
            when (orderTabState.value) {
                OrderTabState.BID -> BidSection(
                    userSeedMoney = userSeedMoney,
                    userBTC = userBTC,
                    isKrw = isKrw,
                    symbol = symbol,
                    currentPrice = currentPrice,
                    updateBidCoinQuantity = updateBidCoinQuantity,
                    bidQuantity = bidQuantity,
                    quantityOnValueChanged = quantityOnValueChanged,
                    getBidTotalPrice = getBidTotalPrice,
                    requestBid = requestBid,
                    dropdownLabelList = dropdownLabelList,
                    selectedText = bidSelectedText,
                    totalBidDialogState = totalBidDialogState
                )

                OrderTabState.ASK -> AskSection(
                    isKrw = isKrw,
                    symbol = symbol,
                    currentPrice = currentPrice,
                    updateAskCoinQuantity = updateAskCoinQuantity,
                    askQuantity = askQuantity,
                    quantityOnValueChanged = quantityOnValueChanged,
                    getAskTotalPrice = getAskTotalPrice,
                    userCoin = userCoin,
                    dropdownLabelList = dropdownLabelList,
                    selectedText = askSelectedText,
                    requestAsk = requestAsk,
                    totalAskDialogState = totalAskDialogState,
                    btcPrice = btcPrice
                )

                OrderTabState.TRANSACTION_INFO -> {
                    LaunchedEffect(true) {
                        getTransactionInfoList(market)
                    }
                    TransactionInfoLazyColumn(
                        isKrw = isKrw,
                        transactionInfoList = transactionInfoList
                    )
                }
            }
        }
    }
}

@Composable
fun BidSection(
    userSeedMoney: State<Double>,
    isKrw: Boolean,
    symbol: String,
    currentPrice: BigDecimal?,
    updateBidCoinQuantity: (Int) -> Unit,
    bidQuantity: String,
    quantityOnValueChanged: (String, Boolean) -> Unit,
    getBidTotalPrice: () -> String,
    requestBid: () -> Unit,
    dropdownLabelList: List<String>,
    selectedText: String,
    userBTC: State<MyCoin>,
    totalBidDialogState: MutableState<Boolean>,
) {
    Column(modifier = Modifier) {
        OrderTabUserSeedMoneySection(
            userSeedMoney = userSeedMoney,
            isKrw = isKrw,
            symbol = symbol,
            userBTC = userBTC,
            isBid = true
        )
        OrderTabPriceSection(currentPrice = currentPrice?.formattedString() ?: "0")
        OrderTabQuantitySection(
            dropDownItemClickAction = updateBidCoinQuantity,
            quantity = bidQuantity,
            quantityOnValueChanged = quantityOnValueChanged,
            isBid = true,
            dropdownLabelList = dropdownLabelList,
            selectedText = selectedText
        )
        OrderTabTotalPriceSection(getTotalPrice = getBidTotalPrice)
        OrderSectionButtonGroup(
            orderTabState = OrderTabState.BID,
            bidAskAction = requestBid,
            totalBidAskAction = { totalBidDialogState.value = true }
        )
        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
        ) {
            Text(
                "최소주문금액",
                modifier = Modifier.weight(1f),
                style = TextStyle(fontSize = DpToSp(13.dp), color = commonHintTextColor())
            )
            Text(
                if (isKrw) "5000KRW" else "0.0005BTC",
                style = TextStyle(fontSize = DpToSp(13.dp), color = commonHintTextColor())
            )
        }
        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
        ) {
            Text(
                "수수료",
                modifier = Modifier.weight(1f),
                style = TextStyle(fontSize = DpToSp(13.dp), color = commonHintTextColor())
            )
            Text(
                text = if (isKrw) "0.05%" else "0.25%",
                style = TextStyle(fontSize = DpToSp(13.dp), color = commonHintTextColor())
            )
        }
    }
}

@Composable
fun AskSection(
    isKrw: Boolean,
    symbol: String,
    currentPrice: BigDecimal?,
    updateAskCoinQuantity: (Int) -> Unit,
    askQuantity: String,
    quantityOnValueChanged: (String, Boolean) -> Unit,
    getAskTotalPrice: () -> String,
    dropdownLabelList: List<String>,
    selectedText: String,
    requestAsk: () -> Unit,
    userCoin: State<MyCoin>,
    totalAskDialogState: MutableState<Boolean>,
    btcPrice: State<BigDecimal>,
) {
    Column(modifier = Modifier) {
        OrderTabUserSeedMoneySection(
            userCoin = userCoin,
            isKrw = isKrw,
            symbol = symbol,
            isBid = false,
            currentPrice = currentPrice,
            btcPrice = btcPrice
        )
        OrderTabPriceSection(currentPrice?.formattedString() ?: "0")
        OrderTabQuantitySection(
            dropDownItemClickAction = updateAskCoinQuantity,
            quantity = askQuantity,
            quantityOnValueChanged = quantityOnValueChanged,
            isBid = false,
            dropdownLabelList = dropdownLabelList,
            selectedText = selectedText
        )
        OrderTabTotalPriceSection(getAskTotalPrice)
        OrderSectionButtonGroup(
            orderTabState = OrderTabState.ASK,
            bidAskAction = requestAsk,
            totalBidAskAction = { totalAskDialogState.value = true }
        )
        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
        ) {
            Text(
                "최소주문금액",
                modifier = Modifier
                    .weight(1f),
                style = TextStyle(fontSize = DpToSp(13.dp), color = commonHintTextColor())
            )
            Text(
                if (isKrw) "5000KRW" else "0.0005BTC",
                style = TextStyle(fontSize = DpToSp(13.dp), color = commonHintTextColor())
            )
        }

        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
        ) {
            Text(
                "수수료",
                modifier = Modifier
                    .weight(1f),
                style = TextStyle(fontSize = DpToSp(13.dp), color = commonHintTextColor())
            )
            Text(
                text = if (isKrw) "0.05%" else "0.25%",
                style = TextStyle(fontSize = DpToSp(13.dp), color = commonHintTextColor())
            )
        }
    }
}

@Composable
fun OrderTabSection(
    orderTabState: MutableState<OrderTabState>,
) {
    val tabText = stringArrayResource(id = R.array.order_tab_text_array)
    val entries = remember {
        OrderTabState.entries
    }

    Row {
        repeat(3) { index ->
            Text(
                text = tabText[index], modifier = Modifier
                    .weight(1f)
                    .background(color = if (entries[index] == orderTabState.value) commonBackground() else unSelectedOrderTabColor())
                    .padding(10.dp)
                    .noRippleClickable {
                        orderTabState.value = entries[index]
                    }, style = TextStyle(
                    textAlign = TextAlign.Center, fontSize = DpToSp(
                        15.dp
                    ), fontWeight = FontWeight.W500,
                    color = if (entries[index] == orderTabState.value) {
                        when (orderTabState.value) {
                            OrderTabState.BID -> {
                                commonRiseColor()
                            }

                            OrderTabState.ASK -> {
                                commonFallColor()
                            }

                            else -> {
                                commonTextColor()
                            }
                        }
                    } else {
                        unselectedOrderTabTextColor()
                    }
                )
            )
        }
    }
}

@Composable
fun OrderTabUserSeedMoneySection(
    userSeedMoney: State<Double>? = null,
    userCoin: State<MyCoin>? = null,
    isKrw: Boolean,
    symbol: String,
    userBTC: State<MyCoin>? = null,
    isBid: Boolean,
    currentPrice: BigDecimal? = BigDecimal.ZERO,
    btcPrice: State<BigDecimal>? = null,
) {
    val tempSymbol = remember {
        if (userSeedMoney != null) {
            if (isKrw) {
                "  KRW"
            } else {
                "  BTC"
            }
        } else {
            "  $symbol"
        }
    }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (userSeedMoney != null) stringResource(id = R.string.orderable) else stringResource(
                    id = R.string.holdingQuantity
                ),
                style = TextStyle(
                    fontWeight = FontWeight.W500,
                    fontSize = DpToSp(dp = 14.dp),
                    color = commonTextColor()
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Row {
                AutoSizeText(
                    text = if (isBid) {
                        if (isKrw) {
                            userSeedMoney?.value?.commaFormat() ?: "0"
                        } else {
                            userBTC?.value?.quantity?.eighthDecimal() ?: "0"
                        }
                    } else {
                        userCoin?.value?.quantity?.newBigDecimal(scale = 8)
                            ?.formattedStringForQuantity() ?: "0"
                    },
                    textStyle = TextStyle(
                        fontSize = DpToSp(dp = 14.dp),
                        textAlign = TextAlign.Center
                    ),
                    color = commonTextColor()
                )
                Text(
                    text = tempSymbol,
                    style = TextStyle(
                        fontWeight = FontWeight.W700,
                        fontSize = DpToSp(dp = 14.dp),
                        color = commonTextColor()
                    )
                )
            }
        }
        if (!isBid) {
            Row(
                modifier = Modifier
                    .padding(3.dp)
                    .align(Alignment.End)
            ) {
                AutoSizeText(
                    text = "= " + CurrentCalculator.getUserCoinValue(
                        userCoinQuantity = userCoin?.value?.quantity ?: 0.0,
                        currentPrice = currentPrice ?: BigDecimal.ZERO,
                        btcPrice = btcPrice?.value
                    ),
                    textStyle = TextStyle(
                        fontSize = DpToSp(dp = 12.dp)
                    ),
                    color = commonHintTextColor()
                )
                Text(
                    text = " KRW",
                    style = TextStyle(
                        color = commonHintTextColor(),
                        fontSize = DpToSp(dp = 12.dp)
                    )
                )
            }
        }
    }
}

@Composable
fun OrderTabPriceSection(currentPrice: String) {
    Row(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth()
            .border(1.dp, commonUnSelectedColor(), RoundedCornerShape(5.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "가격",
            modifier = Modifier,
            fontSize = DpToSp(13.dp),
            color = boxTextColor()
        )
        Text(
            text = currentPrice,
            modifier = Modifier.weight(1f),
            style = TextStyle(
                textAlign = TextAlign.End,
                fontSize = DpToSp(14.dp),
                fontWeight = FontWeight.W500,
                color = commonTextColor()
            )
        )
    }
}

@Composable
fun OrderTabQuantitySection(
    dropDownItemClickAction: (Int) -> Unit,
    quantity: String,
    quantityOnValueChanged: (String, Boolean) -> Unit,
    isBid: Boolean,
    dropdownLabelList: List<String>,
    selectedText: String,
) {
    val focusRequester = remember { FocusRequester() }
    val focusState = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth()
            .border(1.dp, commonUnSelectedColor(), RoundedCornerShape(5.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "수량",
            modifier = Modifier
                .padding(vertical = 10.dp)
                .padding(start = 10.dp),
            fontSize = DpToSp(13.dp),
            color = boxTextColor()
        )

        BasicTextField(value = quantity, onValueChange = {
            val rawValue = it.replace(",", "")

            if (rawValue.matches(Regex("^[0-9]*\\.?[0-9]{0,8}$"))) {
                quantityOnValueChanged(rawValue, isBid)
            }
        }, singleLine = true,
            textStyle = TextStyle(
                color = commonTextColor(),
                fontSize = DpToSp(13.dp), textAlign = TextAlign.End
            ),
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .weight(1f)
                .clearFocusOnKeyboardDismiss()
                .focusRequester(focusRequester)
                .onFocusChanged { state ->
                    focusState.value = state.isFocused
                },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
            visualTransformation = NumberCommaTransformation2(),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (quantity.isEmpty() && !focusState.value) {
                        Text(
                            "0",
                            style = TextStyle(
                                color = commonTextColor(),
                                fontSize = DpToSp(dp = 13.dp),
                                textAlign = TextAlign.End
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    innerTextField()
                }
            })
        PercentageDropdown(
            itemClickAction = dropDownItemClickAction,
            labelList = dropdownLabelList,
            selectedText = selectedText
        )
    }
}

@Composable
fun PercentageDropdown(
    itemClickAction: ((Int) -> Unit),
    labelList: List<String>,
    selectedText: String,
) {
    val expanded = remember { mutableStateOf(false) }

    Box(modifier = Modifier, contentAlignment = Alignment.CenterEnd) {
        Column {
            Text(
                text = selectedText,
                modifier = Modifier
                    .width(60.dp)
                    .noRippleClickable { expanded.value = true }
                    .background(
                        commonUnSelectedColor(),
                        shape = RoundedCornerShape(topEnd = 5.dp, bottomEnd = 5.dp)
                    )
                    .padding(10.dp),
                style = TextStyle(
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = DpToSp(13.dp),
                ),
                maxLines = 1
            )
            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },
                modifier = Modifier
                    .background(commonBackground())
                    .border(width = 1.dp, color = commonDividerColor())
            ) {
                labelList.forEachIndexed { index, label ->
                    androidx.compose.material.DropdownMenuItem(onClick = {
                        expanded.value = false
                        itemClickAction(index)
                    }) {
                        Text(
                            text = label,
                            style = TextStyle(fontSize = DpToSp(13.dp), color = commonTextColor())
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderTabTotalPriceSection(
    getTotalPrice: () -> String,
) {
    Row(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth()
            .border(1.dp, commonUnSelectedColor(), RoundedCornerShape(5.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "총액",
            modifier = Modifier,
            fontSize = DpToSp(13.dp),
            color = boxTextColor()
        )
        Text(
            text = getTotalPrice(),
            modifier = Modifier.weight(1f),
            style = TextStyle(
                color = commonTextColor(),
                textAlign = TextAlign.End,
                fontSize = DpToSp(14.dp),
                fontWeight = FontWeight.W500
            )
        )
    }
}

@Composable
fun OrderSectionButtonGroup(
    orderTabState: OrderTabState,
    resetAction: () -> Unit = {},
    bidAskAction: () -> Unit = {},
    totalBidAskAction: () -> Unit = {},
) {
    val buttonColor =
        if (orderTabState == OrderTabState.BID) commonRiseColor() else commonFallColor()

    val buttonText = remember {
        if (orderTabState == OrderTabState.BID) "매수" else "매도"
    }
    val totalButtonText = remember {
        if (orderTabState == OrderTabState.BID) "총액 지정하여 매수" else "총액 지정하여 매도"
    }

    Column(modifier = Modifier.padding(top = 15.dp)) {
        Row {
            Text(
                text = "초기화", modifier = Modifier
                    .padding(end = 5.dp)
                    .weight(2f)
                    .background(Color.LightGray, shape = RoundedCornerShape(5.dp))
                    .padding(15.dp)
                    .noRippleClickable { resetAction() },
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W500,
                    fontSize = DpToSp(dp = 15.dp)
                )
            )
            Text(
                text = buttonText,
                modifier = Modifier
                    .weight(3f)
                    .background(buttonColor, shape = RoundedCornerShape(5.dp))
                    .padding(15.dp)
                    .noRippleClickable { bidAskAction() },
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontWeight = FontWeight.W500,
                    fontSize = DpToSp(dp = 15.dp)
                )
            )
        }
        Row(modifier = Modifier.padding(top = 15.dp)) {
            Text(
                text = totalButtonText, modifier = Modifier
                    .weight(1f)
                    .background(buttonColor, shape = RoundedCornerShape(5.dp))
                    .padding(15.dp)
                    .noRippleClickable { totalBidAskAction() },
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontWeight = FontWeight.W500,
                    fontSize = DpToSp(dp = 15.dp)
                )
            )
        }
    }
}

class NumberCommaTransformation2 : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(AnnotatedString(""), OffsetMapping.Identity)
        }

        try {
            // 소숫점 분리
            val parts = originalText.split(".")
            val integerPart = parts[0].filter { it.isDigit() }
            val decimalPart = if (parts.size > 1) {
                parts[1].filter { it.isDigit() } // 소숫점 자리수 제한 제거
            } else {
                ""
            }

            // BigDecimal로 정수 부분 처리
            val bigDecimal = BigDecimal(integerPart)
            val formatter = DecimalFormat("#,###", DecimalFormatSymbols(Locale.getDefault()))
            val formattedInteger = formatter.format(bigDecimal)

            // 최종 결과 조합
            val result = if (decimalPart.isNotEmpty()) {
                "$formattedInteger.$decimalPart"
            } else {
                formattedInteger
            }

            // OffsetMapping 구현
            val offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    if (offset <= 0) return offset

                    val originalLength = originalText.length
                    val formattedLength = result.length

                    if (offset >= originalLength) return formattedLength

                    // 원본에서 변환된 위치 계산
                    var commaCount = 0
                    val digitsBefore = originalText.take(offset).count { it.isDigit() }
                    var digitCount = 0

                    for (i in result.indices) {
                        if (result[i].isDigit()) {
                            digitCount++
                            if (digitCount == digitsBefore) {
                                return i + 1
                            }
                        } else if (result[i] == ',') {
                            commaCount++
                        }
                    }
                    return offset + commaCount
                }

                override fun transformedToOriginal(offset: Int): Int {
                    if (offset <= 0) return offset

                    val formattedLength = result.length
                    if (offset >= formattedLength) return originalText.length

                    var digitCount = 0
                    for (i in 0 until offset) {
                        if (result[i].isDigit()) {
                            digitCount++
                        }
                    }
                    return digitCount
                }
            }

            return TransformedText(
                AnnotatedString(result),
                offsetMapping
            )
        } catch (e: Exception) {
            return TransformedText(text, OffsetMapping.Identity)
        }
    }
}