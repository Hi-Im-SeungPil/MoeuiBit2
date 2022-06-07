package org.jeonfeel.moeuibit2.ui.coindetail.order

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.SOCKET_IS_CONNECTED
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitCoinDetailWebSocket
import org.jeonfeel.moeuibit2.ui.custom.AskBidDialog
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.ui.custom.OrderScreenQuantityTextField
import org.jeonfeel.moeuibit2.util.Calculator
import org.jeonfeel.moeuibit2.util.OneTimeNetworkCheck
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.CoinDetailViewModel
import kotlin.math.round

@Composable
fun OrderScreenAskBid(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        OrderScreenTabs(coinDetailViewModel)
        Box(
            modifier = Modifier
                .padding(10.dp, 0.dp)
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                OrderScreenUserSeedMoney(coinDetailViewModel)
                OrderScreenQuantity(coinDetailViewModel)
                OrderScreenPrice(coinDetailViewModel)
                OrderScreenTotalPrice(coinDetailViewModel)
                OrderScreenButtons(coinDetailViewModel)
                OrderScreenNotice()
            }
        }
    }
}

@Composable
fun OrderScreenTabs(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    val selectedTab = coinDetailViewModel.askBidSelectedTab
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        Modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        Box(modifier = Modifier
            .background(getTabBackGround(selectedTab.value, 1))
            .weight(1f)
            .fillMaxHeight()
            .wrapContentHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { selectedTab.value = 1 }) {
            Text(
                text = "매수",
                modifier = Modifier.fillMaxWidth(),
                style = getTabTextStyle(selectedTab.value, 1)
            )
        }
        Box(modifier = Modifier
            .background(getTabBackGround(selectedTab.value, 2))
            .weight(1f)
            .fillMaxHeight()
            .wrapContentHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { selectedTab.value = 2 }
        ) {
            Text(
                text = "매도",
                modifier = Modifier.fillMaxWidth(),
                style = getTabTextStyle(selectedTab.value, 2)
            )
        }
        Box(modifier = Modifier
            .background(getTabBackGround(selectedTab.value, 3))
            .weight(1f)
            .fillMaxHeight()
            .wrapContentHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { selectedTab.value = 3 }
        ) {
            Text(
                text = "거래내역",
                modifier = Modifier.fillMaxWidth(),
                style = getTabTextStyle(selectedTab.value, 3)
            )
        }
    }
}

@Composable
fun OrderScreenUserSeedMoney(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    val textStyleBody1 = MaterialTheme.typography.body1.copy(
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.End,
        fontSize = 15.sp
    )
    val textStyle = remember { mutableStateOf(textStyleBody1) }
    var krwOrSymbol = ""
    var currentUserCoinValue = ""
    val userSeedMoneyOrCoin = if (coinDetailViewModel.askBidSelectedTab.value == 1) {
        krwOrSymbol = "KRW"
        Calculator.getDecimalFormat().format(coinDetailViewModel.userSeedMoney.value)
    } else {
        val userCoin = coinDetailViewModel.userCoinQuantity.value
        currentUserCoinValue = Calculator.getDecimalFormat()
            .format(round(coinDetailViewModel.currentTradePriceState.value * userCoin))
        krwOrSymbol = coinDetailViewModel.market.substring(4)
        Calculator.getDecimalDecimalFormat().format(userCoin)
    }
    Column(
        modifier = Modifier
            .padding(0.dp, 10.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
        ) {
            Text(text = "주문가능", modifier = Modifier.wrapContentWidth(), fontSize = 13.sp)
            AutoSizeText(
                text = userSeedMoneyOrCoin,
                textStyle = textStyle.value,
                modifier = Modifier.weight(1f, true)
            )
            Text(
                text = "  ".plus(krwOrSymbol),
                modifier = Modifier.wrapContentWidth(),
                fontWeight = FontWeight.Bold, fontSize = 15.sp,
                textAlign = TextAlign.End
            )
        }
        if (coinDetailViewModel.askBidSelectedTab.value == 2) {
            Text(
                text = "= $currentUserCoinValue  KRW",
                modifier = Modifier
                    .padding(0.dp, 2.dp)
                    .align(Alignment.End),
                style = TextStyle(color = Color.DarkGray, fontSize = 12.sp)
            )
        }
    }
}

@Composable
fun OrderScreenQuantity(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(35.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .border(0.7.dp, Color.LightGray)
                .wrapContentHeight()
        ) {
            Text(
                text = "수량",
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(8.dp, 0.dp, 8.dp, 0.dp),
                style = TextStyle(fontSize = 15.sp)
            )
            OrderScreenQuantityTextField(
                modifier = Modifier.weight(1f, true),
                placeholderText = "0",
                fontSize = 15.sp,
                coinDetailViewModel = coinDetailViewModel
            )
        }
        OrderScreenQuantityDropDown(Modifier.weight(1f), coinDetailViewModel)
    }
}

@Composable
fun OrderScreenQuantityDropDown(
    modifier: Modifier,
    coinDetailViewModel: CoinDetailViewModel = viewModel(),
) {
    val bidButtonText = remember { mutableStateOf("가능") }
    val askButtonText = remember { mutableStateOf("가능") }
    val expanded = remember { mutableStateOf(false) }
    val suggestions = remember { listOf("최대", "50%", "25%", "10%") }
    val imageVector = if (expanded.value) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }
    val textButtonWidth = remember {
        mutableStateOf(0f)
    }

    Box(modifier = modifier) {
        TextButton(
            onClick = { expanded.value = !expanded.value },
            modifier = Modifier.onGloballyPositioned { coordinates ->
                textButtonWidth.value = coordinates.size.toSize().width
            }
        ) {
            val buttonText = when (coinDetailViewModel.askBidSelectedTab.value) {
                1 -> {
                    bidButtonText.value
                }
                2 -> {
                    askButtonText.value
                }
                else -> {
                    ""
                }
            }

            Text(buttonText, style = TextStyle(color = Color.Black))
            Icon(
                imageVector = imageVector,
                contentDescription = null,
            )
        }
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier.width(with(LocalDensity.current) { textButtonWidth.value.toDp() })
        ) {
            suggestions.forEach { label ->
                DropdownMenuItem(onClick = {
                    expanded.value = false
                    if (coinDetailViewModel.askBidSelectedTab.value == 1) {
                        bidButtonText.value = label
                        val quantity = Calculator.orderScreenSpinnerBidValueCalculator(
                            bidButtonText.value,
                            coinDetailViewModel.userSeedMoney.value,
                            coinDetailViewModel.currentTradePriceState.value
                        )
                        if (quantity.toDouble() != 0.0) {
                            coinDetailViewModel.bidQuantity.value = quantity
                        }
                    } else if (coinDetailViewModel.askBidSelectedTab.value == 2) {
                        askButtonText.value = label
                        val quantity = Calculator.orderScreenSpinnerAskValueCalculator(
                            askButtonText.value,
                            coinDetailViewModel.userCoinQuantity.value
                        )
                        if (quantity.toDouble() != 0.0) {
                            coinDetailViewModel.askQuantity.value = quantity
                        }
                    }
                }) {
                    Text(text = label)
                }
            }
        }
    }
}

@Composable
fun OrderScreenPrice(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    Row(
        modifier = Modifier
            .padding(0.dp, 5.dp)
            .fillMaxWidth()
            .height(35.dp)
            .border(0.7.dp, Color.LightGray)
            .wrapContentHeight()
    ) {
        Text(
            text = "가격", modifier = Modifier
                .wrapContentWidth()
                .padding(8.dp, 0.dp, 8.dp, 0.dp), style = TextStyle(fontSize = 15.sp)
        )
        Text(
            text = Calculator.tradePriceCalculatorForChart(coinDetailViewModel.currentTradePriceState.value),
            modifier = Modifier.weight(1f, true),
            textAlign = TextAlign.End,
            style = TextStyle(fontSize = 15.sp)
        )
        Text(
            text = "  KRW",
            modifier = Modifier
                .wrapContentWidth()
                .padding(0.dp, 0.dp, 8.dp, 0.dp),
            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun OrderScreenTotalPrice(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    Row(
        modifier = Modifier
            .padding(0.dp, 0.dp, 0.dp, 5.dp)
            .fillMaxWidth()
            .height(35.dp)
            .border(0.7.dp, Color.LightGray)
            .wrapContentHeight()
    ) {
        Text(
            text = "총액", modifier = Modifier
                .wrapContentWidth()
                .padding(8.dp, 0.dp, 8.dp, 0.dp), style = TextStyle(fontSize = 15.sp)
        )
        Text(
            text = if (coinDetailViewModel.askBidSelectedTab.value == 1) {
                if (coinDetailViewModel.bidQuantity.value.isEmpty()) {
                    "0"
                } else {
                    Calculator.orderScreenTotalPriceCalculator(
                        coinDetailViewModel.bidQuantity.value.toDouble(),
                        coinDetailViewModel.currentTradePriceState.value
                    )
                }
            } else {
                if (coinDetailViewModel.askQuantity.value.isEmpty()) {
                    "0"
                } else {
                    Calculator.orderScreenTotalPriceCalculator(
                        coinDetailViewModel.askQuantity.value.toDouble(),
                        coinDetailViewModel.currentTradePriceState.value
                    )
                }
            },
            modifier = Modifier.weight(1f, true),
            textAlign = TextAlign.End,
            style = TextStyle(fontSize = 15.sp)
        )
        Text(
            text = "  KRW",
            modifier = Modifier
                .wrapContentWidth()
                .padding(0.dp, 0.dp, 8.dp, 0.dp),
            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun OrderScreenButtons(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    val buttonBackground = getButtonsBackground(coinDetailViewModel.askBidSelectedTab.value)
    val buttonText = getButtonsText(coinDetailViewModel.askBidSelectedTab.value)
    val currentPrice = remember {
        mutableStateOf(0.0)
    }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(0.dp, 5.dp)
                .fillMaxWidth()
                .height(40.dp)
        ) {
            TextButton(
                onClick = {
                          if(coinDetailViewModel.askBidSelectedTab.value == 1) {
                              coinDetailViewModel.bidQuantity.value = ""
                          } else {
                              coinDetailViewModel.askQuantity.value = ""
                          }
                }, modifier = Modifier
                    .padding(0.dp, 0.dp, 4.dp, 0.dp)
                    .weight(1f)
                    .background(color = Color.LightGray)
                    .fillMaxHeight()
            ) {
                Text(text = "초기화", style = TextStyle(color = Color.White), fontSize = 18.sp)
            }
            TextButton(
                onClick = {
                    currentPrice.value = coinDetailViewModel.currentTradePriceState.value
                    val quantity = if (coinDetailViewModel.askBidSelectedTab.value == 1) {
                        coinDetailViewModel.bidQuantity.value.ifEmpty { "0" }
                    } else {
                        coinDetailViewModel.askQuantity.value.ifEmpty { "0" }
                    }
                    val totalPrice = Calculator.orderScreenBidTotalPriceCalculator(
                        quantity.toDouble(),
                        currentPrice.value
                    )
                    val userSeedMoney = coinDetailViewModel.userSeedMoney.value
                    val userCoin = coinDetailViewModel.userCoinQuantity.value
                    when {
                        totalPrice.toLong() < 5000 -> {
                            Toast.makeText(
                                context,
                                "주문 가능한 최소금액은 5,000KRW 입니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        OneTimeNetworkCheck.networkCheck(context) == null -> {
                            Toast.makeText(context, "네트워크 상태를 확인해 주세요.", Toast.LENGTH_SHORT).show()
                        }
                        UpBitCoinDetailWebSocket.currentSocketState != SOCKET_IS_CONNECTED -> {
                            Toast.makeText(context, "네트워크 오류 입니다.", Toast.LENGTH_SHORT).show()
                        }
                        coinDetailViewModel.askBidSelectedTab.value == 1 && userSeedMoney < totalPrice + round(totalPrice * 0.0005) -> {
                            Toast.makeText(context, "주문 가능 금액이 부족합니다.", Toast.LENGTH_SHORT).show()
                        }
                        coinDetailViewModel.askBidSelectedTab.value == 2 && userCoin < coinDetailViewModel.askQuantity.value.toDouble() -> {
                            Toast.makeText(context, "매도 가능 수량이 부족합니다.", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            if (coinDetailViewModel.askBidSelectedTab.value == 1) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    coinDetailViewModel.bidRequest(
                                        currentPrice.value,
                                        quantity.toDouble(),
                                        totalPrice.toLong()
                                    ).join()
                                    Toast.makeText(context, "매수주문이 완료 되었습니다.", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            } else {
                                CoroutineScope(Dispatchers.Main).launch {
                                    coinDetailViewModel.askRequest(
                                        quantity.toDouble(), totalPrice.toLong()
                                    ).join()
                                    Toast.makeText(context, "매도주문이 완료 되었습니다.", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    }
                }, modifier = Modifier
                    .padding(4.dp, 0.dp, 0.dp, 0.dp)
                    .weight(1f)
                    .background(buttonBackground)
                    .fillMaxHeight()
            ) {
                Text(text = buttonText, style = TextStyle(color = Color.White), fontSize = 18.sp)
            }
        }
        TextButton(
            onClick = {
                coinDetailViewModel.askBidDialogState.value = true
            }, modifier = Modifier
                .padding(0.dp, 5.dp)
                .fillMaxWidth()
                .background(buttonBackground)
                .height(40.dp)
        ) {
            Text(
                text = "총액 지정하여 ".plus(buttonText),
                style = TextStyle(color = Color.White),
                fontSize = 18.sp
            )
        }
        AskBidDialog(visible = coinDetailViewModel.askBidDialogState.value, coinDetailViewModel = coinDetailViewModel)
    }
}

@Composable
fun OrderScreenNotice() {
    Column(
        modifier = Modifier
            .padding(0.dp, 10.dp, 0.dp, 0.dp)
            .fillMaxWidth()
    ) {
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = "최소 주문 금액",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
                style = TextStyle(color = Color.Gray)
            )
            Text(
                text = "5,000 KRW",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                style = TextStyle(color = Color.Gray)
            )
        }
        Row(
            Modifier
                .padding(0.dp, 10.dp, 0.dp, 0.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "수수료",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
                style = TextStyle(color = Color.Gray)
            )
            Text(
                text = "0.05%",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                style = TextStyle(color = Color.Gray)
            )
        }
    }
}

@Composable
fun getTabTextStyle(selectedTab: Int, tabNum: Int): TextStyle {
    return if (selectedTab == tabNum) {
        TextStyle(
            color = colorResource(id = R.color.C0F0F5C),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
    } else {
        TextStyle(color = Color.Gray, fontSize = 15.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun getTabBackGround(selectedTab: Int, tabNum: Int): Color {
    return if (selectedTab == tabNum) {
        colorResource(id = R.color.design_default_color_background)
    } else {
        colorResource(id = R.color.CF6F6F6)
    }
}

@Composable
fun getButtonsBackground(selectedTab: Int): Color {
    return if (selectedTab == 1) {
        colorResource(id = R.color.CEA3030)
    } else {
        colorResource(id = R.color.C3048EA)
    }
}

@Composable
fun getButtonsText(selectedTab: Int): String {
    return if (selectedTab == 1) {
        "매수"
    } else {
        "매도"
    }
}