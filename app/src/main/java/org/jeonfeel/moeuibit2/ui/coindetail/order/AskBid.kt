package org.jeonfeel.moeuibit2.ui.coindetail.order

import android.content.Context
import android.view.Gravity
import android.widget.ImageView
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.showAlignTop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.CoinDetailViewModel
import org.jeonfeel.moeuibit2.constant.SOCKET_IS_CONNECTED
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitCoinDetailWebSocket
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.ui.custom.OrderScreenQuantityTextField
import org.jeonfeel.moeuibit2.util.Calculator
import org.jeonfeel.moeuibit2.util.OneTimeNetworkCheck
import org.jeonfeel.moeuibit2.util.showToast
import kotlin.math.round

@Composable
fun OrderScreenAskBid(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
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
            if (coinDetailViewModel.askBidSelectedTab.value != 3) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    OrderScreenUserSeedMoney(coinDetailViewModel)
                    OrderScreenQuantity(coinDetailViewModel)
                    OrderScreenPrice(coinDetailViewModel)
                    OrderScreenTotalPrice(coinDetailViewModel)
                    OrderScreenButtons(coinDetailViewModel)
                    OrderScreenNotice(context, lifecycleOwner)
                }
            } else {
                TransactionInfoLazyColumn(coinDetailViewModel)
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
            ) {
                coinDetailViewModel.getTransactionInfoList()
                selectedTab.value = 3
            }
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
    val krwOrSymbol: String
    var currentUserCoinValue = ""
    val userSeedMoneyOrCoin = if (coinDetailViewModel.askBidSelectedTab.value == 1) {
        krwOrSymbol = "KRW"
        Calculator.getDecimalFormat().format(coinDetailViewModel.userSeedMoney)
    } else {
        val userCoin = coinDetailViewModel.userCoinQuantity
        currentUserCoinValue = Calculator.getDecimalFormat()
            .format(round(coinDetailViewModel.currentTradePriceState * userCoin))
        krwOrSymbol = coinDetailViewModel.market.substring(4)
        Calculator.getDecimalDecimalFormat().format(userCoin)
    }
    Column(
        modifier = Modifier
            .padding(0.dp, 10.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row {
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
                    if (coinDetailViewModel.askBidSelectedTab.value == 1 && coinDetailViewModel.currentTradePriceState != 0.0) {
                        bidButtonText.value = label
                            val quantity = Calculator.orderScreenSpinnerBidValueCalculator(
                                bidButtonText.value,
                                coinDetailViewModel.userSeedMoney,
                                coinDetailViewModel.currentTradePriceState
                            )
                        val quantityResult = quantity.toDoubleOrNull()
                            if (quantityResult != 0.0 && quantityResult != null) {
                                coinDetailViewModel.bidQuantity.value = quantity
                            }
                    } else if (coinDetailViewModel.askBidSelectedTab.value == 2 && coinDetailViewModel.currentTradePriceState != 0.0) {
                        askButtonText.value = label
                        val quantity = Calculator.orderScreenSpinnerAskValueCalculator(
                            askButtonText.value,
                            coinDetailViewModel.userCoinQuantity
                        )
                        val quantityResult = quantity.toDoubleOrNull()
                        if (quantityResult != 0.0 && quantityResult != null) {
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
            text = Calculator.tradePriceCalculatorForChart(coinDetailViewModel.currentTradePriceState),
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
                        coinDetailViewModel.currentTradePriceState
                    )
                }
            } else {
                if (coinDetailViewModel.askQuantity.value.isEmpty()) {
                    "0"
                } else {
                    Calculator.orderScreenTotalPriceCalculator(
                        coinDetailViewModel.askQuantity.value.toDouble(),
                        coinDetailViewModel.currentTradePriceState
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
                    if (coinDetailViewModel.askBidSelectedTab.value == 1) {
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
                    currentPrice.value = coinDetailViewModel.currentTradePriceState
                    val quantity = if (coinDetailViewModel.askBidSelectedTab.value == 1) {
                        coinDetailViewModel.bidQuantity.value.ifEmpty { "0" }
                    } else {
                        coinDetailViewModel.askQuantity.value.ifEmpty { "0" }
                    }
                    val totalPrice = Calculator.orderScreenBidTotalPriceCalculator(
                        quantity.toDouble(),
                        currentPrice.value
                    )
                    val userSeedMoney = coinDetailViewModel.userSeedMoney
                    val userCoin = coinDetailViewModel.userCoinQuantity
                    when {
                        totalPrice.toLong() < 5000 -> {
                            context.showToast("주문 가능한 최소금액은 5,000KRW 입니다.")
                        }
                        currentPrice.value == 0.0 -> {
                            context.showToast("네트워크 통신 오류입니다.")
                        }
                        OneTimeNetworkCheck.networkCheck(context) == null -> {
                            context.showToast("네트워크 상태를 확인해 주세요.")
                        }
                        UpBitCoinDetailWebSocket.currentSocketState != SOCKET_IS_CONNECTED -> {
                            context.showToast("네트워크 오류 입니다.")
                        }
                        coinDetailViewModel.askBidSelectedTab.value == 1 && userSeedMoney < totalPrice + round(
                            totalPrice * 0.0005
                        ) -> {
                            context.showToast("주문 가능 금액이 부족합니다.")
                        }
                        coinDetailViewModel.askBidSelectedTab.value == 2 && userCoin < coinDetailViewModel.askQuantity.value.toDouble() -> {
                            context.showToast("매도 가능 수량이 부족합니다.")
                        }
                        else -> {
                            if (coinDetailViewModel.askBidSelectedTab.value == 1) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    coinDetailViewModel.bidRequest(
                                        currentPrice.value,
                                        quantity.toDouble(),
                                        totalPrice.toLong()
                                    ).join()
                                    context.showToast("매수주문이 완료 되었습니다.")
                                }
                            } else {
                                CoroutineScope(Dispatchers.Main).launch {
                                    coinDetailViewModel.askRequest(
                                        quantity.toDouble(), totalPrice.toLong(), currentPrice.value
                                    ).join()
                                    context.showToast("매도주문이 완료 되었습니다.")
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
                coinDetailViewModel.totalPriceDesignated = ""
                coinDetailViewModel.askBidDialogState = true
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
        TotalAmountDesignatedDialog(coinDetailViewModel, coinDetailViewModel.currentTradePriceState)
    }
}

@Composable
fun OrderScreenNotice(context: Context, lifecycleOwner: LifecycleOwner) {
    val balloon = remember {
        Balloon.Builder(context)
            .setWidthRatio(1.0f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setText("· 현재가격으로만 거래가 가능합니다.\n\n· 상장폐지, 리브랜딩되는 코인들은 상태 변경 후 거래가 불가능합니다. 그전에 매도 하시는것을 권장합니다. \n\n· 네트워크 요청이 많을 시 잠시동안 연결이 되지않습니다. 이 문제는 잠시 기다리시거나 앱을 완전히 종료후 다시시작하시면 해결됩니다. ")
            .setTextColorResource(R.color.white)
            .setTextGravity(Gravity.START)
            .setTextSize(15f)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowSize(10)
            .setArrowPosition(0.5f)
            .setPadding(12)
            .setCornerRadius(8f)
            .setBackgroundColorResource(R.color.C6799FF)
            .setLifecycleOwner(lifecycleOwner)
            .build()
    }
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
        AndroidView(factory = {
            ImageView(it).apply {
                val drawable = ContextCompat.getDrawable(it, R.drawable.img_info)
                setImageDrawable(drawable)
                setOnClickListener {
                    showAlignTop(balloon)
                }
            }
        }, modifier = Modifier
            .padding(0.dp, 10.dp, 10.dp, 0.dp)
            .size(20.dp)
            .align(Alignment.End))
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