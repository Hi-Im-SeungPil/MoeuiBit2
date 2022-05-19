package org.jeonfeel.moeuibit2.ui.coindetail.order

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
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
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.ui.custom.OrderScreenQuantityTextField
import org.jeonfeel.moeuibit2.util.Calculator
import org.jeonfeel.moeuibit2.util.OneTimeNetworkCheck
import org.jeonfeel.moeuibit2.viewmodel.coindetail.CoinDetailViewModel
import kotlin.math.round

@Composable
fun OrderScreenAskBid(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        OrderScreenTabs(coinDetailViewModel)
        Box(
            modifier = Modifier
                .padding(10.dp, 0.dp)
                .weight(1f)
        ) {
            Column() {
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

    Row(
        modifier = Modifier
            .padding(0.dp, 10.dp)
            .fillMaxWidth()
            .height(30.dp)
            .wrapContentHeight()
    ) {
        Text(text = "주문가능", modifier = Modifier.wrapContentWidth(), fontSize = 13.sp)
        AutoSizeText(
            text = Calculator.getDecimalFormat().format(coinDetailViewModel.userSeedMoney.value),
            textStyle = textStyle.value,
            modifier = Modifier.weight(1f, true)
        )
        Text(
            text = "  KRW",
            modifier = Modifier.wrapContentWidth(),
            fontWeight = FontWeight.Bold, fontSize = 15.sp
        )
    }
}

@Composable
fun OrderScreenQuantity(coinDetailViewModel: CoinDetailViewModel = viewModel()) {

    val text = remember { mutableStateOf("") }

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
                textFieldValue = text,
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
    coinDetailViewModel: CoinDetailViewModel = viewModel()
) {
    val buttonText = remember { mutableStateOf("가능") }
    val expanded = remember { mutableStateOf(false) }
    val suggestions = remember {
        listOf("최대", "50%", "25%", "10%")
    }
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
            Text(buttonText.value, style = TextStyle(color = Color.Black))
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
                    buttonText.value = label
                    expanded.value = false
                    val quantity = Calculator.orderScreenSpinnerValueCalculator(
                        label,
                        coinDetailViewModel.userSeedMoney.value,
                        coinDetailViewModel.currentTradePriceState.value
                    )
                    if (quantity.toDouble() != 0.0) {
                        if (coinDetailViewModel.askBidSelectedTab.value == 1) {
                            coinDetailViewModel.bidQuantity.value = quantity
                        } else {
                            coinDetailViewModel.askQuantity.value = quantity
                        }
                    }
                    //do something ...
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
                onClick = { }, modifier = Modifier
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
                    when {
                        totalPrice.toLong() < 5000 -> {
                            Toast.makeText(context, "5000원 이상만 주문 가능합니다.", Toast.LENGTH_SHORT).show()
                        }
                        totalPrice.toLong() > coinDetailViewModel.userSeedMoney.value -> {
                            Toast.makeText(context, "주문 가능 금액이 부족합니다.", Toast.LENGTH_SHORT).show()
                        }
                        OneTimeNetworkCheck.networkCheck(context) == null -> {
                            Toast.makeText(context, "네트워크 상태를 확인해 주세요.", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(context, "주문 ㄱ", Toast.LENGTH_SHORT).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                if(coinDetailViewModel.localRepository.getMyCoinDao().isInsert(coinDetailViewModel.market) == null) {
//                                    coinDetailViewModel.localRepository.getMyCoinDao().insert(MyCoin(coinDetailViewModel.market,currentPrice.value,coinDetailViewModel.koreanName,coinDetailViewModel.market.substring(4)))
                                } else {
                                    coinDetailViewModel.localRepository.getMyCoinDao().updatePurchasePriceLong(coinDetailViewModel.market,totalPrice.toLong())
                                    coinDetailViewModel.localRepository.getMyCoinDao().updatePlusQuantity(coinDetailViewModel.market,quantity.toDouble())
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
            onClick = { }, modifier = Modifier
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