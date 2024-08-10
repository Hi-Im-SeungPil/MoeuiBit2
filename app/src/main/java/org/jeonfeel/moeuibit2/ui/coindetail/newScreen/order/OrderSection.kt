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
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.coindetail.order.ui.formatWithComma
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedString
import java.math.BigDecimal

@Composable
fun OrderSection(
    orderTabState: MutableState<OrderTabState>,
    userSeedMoney: Long,
    isKrw: Boolean,
    symbol: String,
    currentPrice: BigDecimal?
) {
    Column(modifier = Modifier.fillMaxSize()) {
        OrderTabSection(orderTabState = orderTabState)
        Box(modifier = Modifier.padding(10.dp)) {
            when (orderTabState.value) {
                OrderTabState.BID -> BidSection(
                    userSeedMoney = userSeedMoney,
                    isKrw = isKrw,
                    symbol = symbol,
                    currentPrice = currentPrice
                )

                OrderTabState.ASK -> AskSection(
                    isKrw = isKrw,
                    symbol = symbol,
                    currentPrice = currentPrice
                )

                OrderTabState.TRANSACTION_INFO -> {}
            }
        }
    }
}

@Composable
fun BidSection(
    userSeedMoney: Long,
    isKrw: Boolean,
    symbol: String,
    currentPrice: BigDecimal?
) {
    Column(modifier = Modifier) {
        OrderTabUserSeedMoneySection(userSeedMoney = userSeedMoney, isKrw = isKrw, symbol = symbol)
        OrderTabPriceSection(currentPrice = currentPrice?.formattedString() ?: "0")
        OrderTabQuantitySection()
        OrderTabTotalPriceSection(currentPrice = currentPrice)
        OrderSectionButtonGroup(orderTabState = OrderTabState.BID)
    }
}

@Composable
fun AskSection(isKrw: Boolean, symbol: String, currentPrice: BigDecimal?) {
    Column(modifier = Modifier) {
        OrderTabUserSeedMoneySection(
            userHoldingQuantity = 10023845.89372662,
            isKrw = isKrw,
            symbol = symbol
        )
        OrderTabPriceSection(currentPrice?.formattedString() ?: "0")
        OrderTabQuantitySection()
        OrderTabTotalPriceSection(currentPrice)
        OrderSectionButtonGroup(orderTabState = OrderTabState.ASK)
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
    userSeedMoney: Long? = null,
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
            text = userSeedMoney?.formatWithComma() ?: userHoldingQuantity.toString(),
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
fun OrderTabQuantitySection() {
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
        AutoSizeText(
            text = "10025.89372662",
            modifier = Modifier
                .padding(vertical = 10.dp)
                .weight(1f)
                .padding(end = 5.dp),
            textStyle = TextStyle(
                textAlign = TextAlign.End,
                fontSize = DpToSp(14.dp),
                fontWeight = FontWeight.W500
            )
        )
        PercentageDropdown()
//        Text(
//            text = "비율 ▼",
//            modifier = Modifier
//                .width(50.dp)
//                .background(
//                    Color.LightGray,
//                    shape = RoundedCornerShape(topEnd = 5.dp, bottomEnd = 5.dp)
//                )
//                .padding(10.dp),
//            style = TextStyle(
//                textAlign = TextAlign.End,
//                fontSize = DpToSp(13.dp),
//            )
//        )
    }
}

@Composable
fun PercentageDropdown() {
    val expanded = remember { mutableStateOf(false) }
    val items = remember { listOf("최대", "75%", "50%", "25%", "10%") }
    val percentage = remember { listOf(100, 75, 50, 25, 10) }
    val defaultText = remember { "비율" }
    val selectedItem = remember { mutableStateOf(defaultText) }

    Box(modifier = Modifier, contentAlignment = Alignment.CenterEnd) {
        Column {
            Text(
                text = selectedItem.value + " ▼",
                modifier = Modifier
                    .width(60.dp)
                    .noRippleClickable { expanded.value = true }
                    .background(
                        Color.LightGray,
                        shape = RoundedCornerShape(topEnd = 5.dp, bottomEnd = 5.dp)
                    ).padding(10.dp),
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
                items.forEach { label ->
                    androidx.compose.material.DropdownMenuItem(onClick = {
                        selectedItem.value = label
                        expanded.value = false
                    }) {
                        Text(text = label)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderTabTotalPriceSection(currentPrice: BigDecimal?) {
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
            text = "10,000,000",
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
    totalBidAskAction: () -> Unit = {}
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