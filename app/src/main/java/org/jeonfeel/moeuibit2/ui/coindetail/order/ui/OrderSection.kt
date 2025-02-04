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
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedString
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringForQuantity
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import org.jeonfeel.moeuibit2.utils.commaFormat
import org.jeonfeel.moeuibit2.utils.eighthDecimal
import java.math.BigDecimal
import java.text.DecimalFormat

@Composable
fun OrderSection(
    orderTabState: MutableState<OrderTabState>,
    userSeedMoney: State<Long>,
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
    totalAskDialogState: MutableState<Boolean>
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
                    totalAskDialogState = totalAskDialogState
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
    userSeedMoney: State<Long>,
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
    totalBidDialogState: MutableState<Boolean>
) {
    Column(modifier = Modifier) {
        OrderTabUserSeedMoneySection(
            userSeedMoney = userSeedMoney,
            userBTC = userBTC,
            isKrw = isKrw,
            symbol = symbol,
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
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("최소주문금액", modifier = Modifier.weight(1f))
            Text("5000 KRW")
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("수수료", modifier = Modifier.weight(1f))
            Text("0.05%")
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
    totalAskDialogState: MutableState<Boolean>
) {
    Column(modifier = Modifier) {
        OrderTabUserSeedMoneySection(
            userCoin = userCoin,
            isKrw = isKrw,
            symbol = symbol,
            isBid = false
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
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("최소주문금액", modifier = Modifier.weight(1f))
            Text("5000 KRW")
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("수수료", modifier = Modifier.weight(1f))
            Text("0.05%")
        }
    }
}

@Composable
fun OrderTabSection(
    orderTabState: MutableState<OrderTabState>
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
                    .background(color = if (entries[index] == orderTabState.value) Color.White else Color.LightGray)
                    .padding(10.dp)
                    .noRippleClickable {
                        orderTabState.value = entries[index]
                    }, style = TextStyle(
                    textAlign = TextAlign.Center, fontSize = DpToSp(
                        15.dp
                    ), fontWeight = FontWeight.W500,
                    color = if (entries[index] == orderTabState.value) Color.Black else Color.Gray
                )
            )
        }
    }
}

@Composable
fun OrderTabUserSeedMoneySection(
    userSeedMoney: State<Long>? = null,
    userCoin: State<MyCoin>? = null,
    isKrw: Boolean,
    symbol: String,
    userBTC: State<MyCoin>? = null,
    isBid: Boolean
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

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = if (userSeedMoney != null) stringResource(id = R.string.orderable) else stringResource(
                id = R.string.holdingQuantity
            ),
            style = TextStyle(fontWeight = FontWeight.W500, fontSize = DpToSp(dp = 14.dp))
        )
        Spacer(modifier = Modifier.weight(1f))
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
            )
        )
        Text(
            text = tempSymbol,
            style = TextStyle(fontWeight = FontWeight.W700, fontSize = DpToSp(dp = 14.dp))
        )
    }
}

@Composable
fun OrderTabPriceSection(currentPrice: String) {
    Row(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, RoundedCornerShape(5.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "가격",
            modifier = Modifier,
            fontSize = DpToSp(13.dp),
            color = Color.DarkGray
        )
        Text(
            text = currentPrice,
            modifier = Modifier.weight(1f),
            style = TextStyle(
                textAlign = TextAlign.End,
                fontSize = DpToSp(14.dp),
                fontWeight = FontWeight.W500
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
    selectedText: String
) {
    val focusRequester = remember { FocusRequester() }
    val focusState = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, RoundedCornerShape(5.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "수량",
            modifier = Modifier
                .padding(vertical = 10.dp)
                .padding(start = 10.dp),
            fontSize = DpToSp(13.dp),
            color = Color.DarkGray
        )

        BasicTextField(value = quantity, onValueChange = {
            val rawValue = it.replace(",", "")
            if (rawValue.matches(Regex("^[0-9]*\\.?[0-9]{0,8}$"))) {
                quantityOnValueChanged(rawValue, isBid)
            }
        }, singleLine = true,
            textStyle = TextStyle(
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
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
            visualTransformation = NumberCommaTransformation(),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (quantity.isEmpty() && !focusState.value) {
                        Text(
                            "0",
                            style = TextStyle(
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
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
    selectedText: String
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
                        Color.LightGray,
                        shape = RoundedCornerShape(topEnd = 5.dp, bottomEnd = 5.dp)
                    )
                    .padding(10.dp),
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = DpToSp(13.dp),
                ),
                maxLines = 1
            )
            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                labelList.forEachIndexed { index, label ->
                    androidx.compose.material.DropdownMenuItem(onClick = {
                        expanded.value = false
                        itemClickAction(index)
                    }) {
                        Text(text = label)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderTabTotalPriceSection(
    getTotalPrice: () -> String
) {
    Row(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, RoundedCornerShape(5.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "총액",
            modifier = Modifier,
            fontSize = DpToSp(13.dp),
            color = Color.DarkGray
        )
        Text(
            text = getTotalPrice(),
            modifier = Modifier.weight(1f),
            style = TextStyle(
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
    val buttonColor = remember {
        if (orderTabState == OrderTabState.BID) Color.Red else Color.Blue
    }
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

class NumberCommaTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text

        // 입력 값에서 정수와 소수점 부분 분리
        val parts = originalText.split(".")
        val integerPart = parts.getOrNull(0)?.replace(",", "") ?: ""
        val decimalPart = parts.getOrNull(1)?.take(8) ?: "" // 소수점 이하 8자리까지만 허용

        // 정수 부분에 쉼표 추가
        val formattedIntegerPart = if (integerPart.isNotEmpty()) {
            DecimalFormat("#,###").format(integerPart.toLongOrNull() ?: 0)
        } else {
            ""
        }

        // 소수점 있는 경우 합치기
        val formattedText = if (decimalPart.isNotEmpty()) {
            "$formattedIntegerPart.$decimalPart"
        } else if (originalText.contains(".")) {
            "$formattedIntegerPart."
        } else {
            formattedIntegerPart
        }

        // OffsetMapping 설정
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var transformedOffset = 0
                var commasAdded = 0
                var dotFound = false

                for (i in 0 until offset) {
                    if (i < integerPart.length) {
                        // 정수 부분에 쉼표 추가에 따라 인덱스 보정
                        if ((integerPart.length - i) % 3 == 0 && i != 0) commasAdded++
                    } else if (!dotFound && originalText[i] == '.') {
                        // 소수점 처리
                        dotFound = true
                    }
                    transformedOffset++
                }

                return transformedOffset + commasAdded
            }

            override fun transformedToOriginal(offset: Int): Int {
                var originalOffset = 0
                var commasAdded = 0
                var dotFound = false

                for (i in 0 until offset) {
                    if (originalOffset < integerPart.length) {
                        // 쉼표가 추가된 부분은 건너뛰기
                        if ((integerPart.length - originalOffset) % 3 == 0 && originalOffset != 0) commasAdded++
                    } else if (!dotFound && formattedText[i] == '.') {
                        // 소수점 처리
                        dotFound = true
                    }
                    originalOffset++
                }

                return originalOffset - commasAdded
            }
        }

        return TransformedText(
            text = AnnotatedString(formattedText),
            offsetMapping = offsetMapping
        )
    }
}