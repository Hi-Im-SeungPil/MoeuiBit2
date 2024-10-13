package org.jeonfeel.moeuibit2.ui.coindetail.newScreen.order

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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.coindetail.order.ui.formatWithComma
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

@Composable
fun OrderSection(
    orderTabState: MutableState<OrderTabState>,
    userSeedMoney: Double,
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
    getUserCoinQuantity: () -> Double,
    dropdownLabelList: List<String>,
    askSelectedText: String,
    bidSelectedText: String
) {
    Column(modifier = Modifier.fillMaxSize()) {
        OrderTabSection(orderTabState = orderTabState)
        Box(modifier = Modifier.padding(10.dp)) {
            when (orderTabState.value) {
                OrderTabState.BID -> BidSection(
                    userSeedMoney = userSeedMoney,
                    isKrw = isKrw,
                    symbol = symbol,
                    currentPrice = currentPrice,
                    updateBidCoinQuantity = updateBidCoinQuantity,
                    bidQuantity = bidQuantity,
                    quantityOnValueChanged = quantityOnValueChanged,
                    getBidTotalPrice = getBidTotalPrice,
                    requestBid = requestBid,
                    dropdownLabelList = dropdownLabelList,
                    selectedText = bidSelectedText
                )

                OrderTabState.ASK -> AskSection(
                    isKrw = isKrw,
                    symbol = symbol,
                    currentPrice = currentPrice,
                    updateAskCoinQuantity = updateAskCoinQuantity,
                    askQuantity = askQuantity,
                    quantityOnValueChanged = quantityOnValueChanged,
                    getAskTotalPrice = getAskTotalPrice,
                    getUserCoinQuantity = getUserCoinQuantity,
                    dropdownLabelList = dropdownLabelList,
                    selectedText = askSelectedText,
                    requestAsk = requestAsk
                )

                OrderTabState.TRANSACTION_INFO -> {}
            }
        }
    }
}

@Composable
fun BidSection(
    userSeedMoney: Double,
    isKrw: Boolean,
    symbol: String,
    currentPrice: BigDecimal?,
    updateBidCoinQuantity: (Int) -> Unit,
    bidQuantity: String,
    quantityOnValueChanged: (String, Boolean) -> Unit,
    getBidTotalPrice: () -> String,
    requestBid: () -> Unit,
    dropdownLabelList: List<String>,
    selectedText: String
) {

    Column(modifier = Modifier) {
        OrderTabUserSeedMoneySection(userSeedMoney = userSeedMoney, isKrw = isKrw, symbol = symbol)
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
        OrderSectionButtonGroup(orderTabState = OrderTabState.BID, bidAskAction = requestBid)
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
    getUserCoinQuantity: () -> Double,
    dropdownLabelList: List<String>,
    selectedText: String,
    requestAsk: () -> Unit
) {
    Column(modifier = Modifier) {
        OrderTabUserSeedMoneySection(
            userHoldingQuantity = getUserCoinQuantity(),
            isKrw = isKrw,
            symbol = symbol
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
        OrderSectionButtonGroup(orderTabState = OrderTabState.ASK, bidAskAction = requestAsk)
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
    userSeedMoney: Double? = null,
    userHoldingQuantity: Double? = null,
    isKrw: Boolean,
    symbol: String
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
            text = if (isKrw) userSeedMoney?.toLong()
                .formatWithComma() else userSeedMoney?.eighthDecimal()
                ?: userHoldingQuantity?.newBigDecimal(scale = 8)
                    ?.formattedStringForQuantity() ?: "0",
            textStyle = TextStyle(fontSize = DpToSp(dp = 14.dp), textAlign = TextAlign.Center)
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
            quantityOnValueChanged(it, isBid)
        }, singleLine = true,
            textStyle = TextStyle(
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                fontSize = DpToSp(13.dp), textAlign = TextAlign.End
            ),
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .weight(1f)
                .clearFocusOnKeyboardDismiss(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (quantity.isEmpty()) {
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
        Row() {
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