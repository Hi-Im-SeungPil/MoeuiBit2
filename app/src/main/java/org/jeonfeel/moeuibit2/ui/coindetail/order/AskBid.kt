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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import org.jeonfeel.moeuibit2.constant.*
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitCoinDetailWebSocket
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.ui.custom.OrderScreenQuantityTextField
import org.jeonfeel.moeuibit2.util.*
import org.jeonfeel.moeuibit2.util.calculator.Calculator
import org.jeonfeel.moeuibit2.util.calculator.CurrentCalculator
import kotlin.math.floor
import kotlin.math.round

@Composable
fun OrderScreenAskBid(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val marketState = EtcUtils.getSelectedMarket(coinDetailViewModel.market)
    AdjustFeeDialog(dialogState = coinDetailViewModel.isShowAdjustFeeDialog,
        coinDetailViewModel.feeStateList,coinDetailViewModel)
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
            if (coinDetailViewModel.askBidSelectedTab.value != ASK_BID_SCREEN_TRANSACTION_TAB) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    OrderScreenUserSeedMoney(coinDetailViewModel, marketState)
                    OrderScreenQuantity(coinDetailViewModel, marketState)
                    OrderScreenPrice(coinDetailViewModel, marketState)
                    OrderScreenTotalPrice(coinDetailViewModel, marketState)
                    OrderScreenButtons(coinDetailViewModel, marketState)
                    OrderScreenNotice(context, lifecycleOwner, marketState, coinDetailViewModel)
                }
            } else {
                TransactionInfoLazyColumn(coinDetailViewModel)
            }
        }
    }
}

/**
 * 매수, 매도, 거래내역 버튼들
 */
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
            .background(getTabBackGround(selectedTab.value, ASK_BID_SCREEN_BID_TAB))
            .weight(1f)
            .fillMaxHeight()
            .wrapContentHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { selectedTab.value = ASK_BID_SCREEN_BID_TAB }) {
            Text(
                text = stringResource(id = R.string.bid),
                modifier = Modifier.fillMaxWidth(),
                style = getTabTextStyle(selectedTab.value, ASK_BID_SCREEN_BID_TAB)
            )
        }
        Box(modifier = Modifier
            .background(getTabBackGround(selectedTab.value, ASK_BID_SCREEN_ASK_TAB))
            .weight(1f)
            .fillMaxHeight()
            .wrapContentHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { selectedTab.value = ASK_BID_SCREEN_ASK_TAB }
        ) {
            Text(
                text = stringResource(id = R.string.ask),
                modifier = Modifier.fillMaxWidth(),
                style = getTabTextStyle(selectedTab.value, ASK_BID_SCREEN_ASK_TAB)
            )
        }
        Box(modifier = Modifier
            .background(getTabBackGround(selectedTab.value, ASK_BID_SCREEN_TRANSACTION_TAB))
            .weight(1f)
            .fillMaxHeight()
            .wrapContentHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                CoroutineScope(mainDispatcher).launch {
                    coinDetailViewModel.getTransactionInfoList()
                    selectedTab.value = 3
                }
            }
        ) {
            Text(
                text = stringResource(id = R.string.tradHistory),
                modifier = Modifier.fillMaxWidth(),
                style = getTabTextStyle(selectedTab.value, ASK_BID_SCREEN_TRANSACTION_TAB)
            )
        }
    }
}

/**
 * 유저가 할 수 있는 매수, 매도 수량
 */
@Composable
fun OrderScreenUserSeedMoney(
    coinDetailViewModel: CoinDetailViewModel = viewModel(),
    marketState: Int,
) {
    val askBidSelectedTab = coinDetailViewModel.askBidSelectedTab // 매수인지 매도인지
    val symbol = coinDetailViewModel.market.substring(4) // 심볼
    val krwOrSymbol: String // 환산 심볼인지 krw인지
    val userCoin = coinDetailViewModel.userCoinQuantity // 유저코인 수량
    val currentTradePriceState = coinDetailViewModel.currentTradePriceState // krw일 때 코인 krw가격
    val currentBTCPrice = coinDetailViewModel.currentBTCPrice // 현재 btc krw 가격
    val currentBTCCoinPrice =
        CurrentCalculator.tradePriceCalculator(currentBTCPrice.value * coinDetailViewModel.btcQuantity.value, // btc 가격 * 수량
            SELECTED_KRW_MARKET)
    var currentUserCoinValue = "" // 유저 보유 코인 총가격

    val userSeedMoneyOrCoin =
        if (askBidSelectedTab.value == ASK_BID_SCREEN_BID_TAB && marketState == SELECTED_KRW_MARKET) { // KRW 매수
            krwOrSymbol = SYMBOL_KRW
            coinDetailViewModel.userSeedMoney.commaFormat()
        } else if (askBidSelectedTab.value == ASK_BID_SCREEN_ASK_TAB && marketState == SELECTED_KRW_MARKET) { // KRW 매도
            currentUserCoinValue = if (userCoin == 0.0) {
                "0"
            } else {
                round(currentTradePriceState * userCoin).commaFormat()
            }
            krwOrSymbol = symbol
            if (userCoin == 0.0) {
                "0"
            } else {
                userCoin.eighthDecimal()
            }

        } else if (askBidSelectedTab.value == ASK_BID_SCREEN_BID_TAB && marketState == SELECTED_BTC_MARKET) { // BTC 매수
            krwOrSymbol = SYMBOL_BTC
            if (coinDetailViewModel.btcQuantity.value == 0.0 || coinDetailViewModel.btcQuantity.value < 0.00000001) {
                "0"
            } else {
                coinDetailViewModel.btcQuantity.value.eighthDecimal()
            }

        } else { // BTC 매도
            currentUserCoinValue = if (userCoin == 0.0) {
                "0"
            } else {
                round(currentTradePriceState * userCoin * currentBTCPrice.value).commaFormat()
            }
            krwOrSymbol = symbol

            if (userCoin == 0.0) {
                "0"
            } else {
                userCoin.eighthDecimal()
            }
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
                modifier = Modifier.weight(1f, true),
                text = userSeedMoneyOrCoin,
                textStyle = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    fontSize = 15.sp
                )
            )
            Text(
                text = "  ".plus(krwOrSymbol),
                modifier = Modifier.wrapContentWidth(),
                fontWeight = FontWeight.Bold, fontSize = 15.sp,
                textAlign = TextAlign.End
            )
        }

        // 코인 현재 KRW 가격
        if (askBidSelectedTab.value == ASK_BID_SCREEN_ASK_TAB || marketState == SELECTED_BTC_MARKET && askBidSelectedTab.value == ASK_BID_SCREEN_ASK_TAB) {
            Text(
                text = "= $currentUserCoinValue  $SYMBOL_KRW",
                modifier = Modifier
                    .padding(0.dp, 2.dp)
                    .align(Alignment.End),
                style = TextStyle(color = Color.DarkGray, fontSize = 12.sp)
            )
        } else if (askBidSelectedTab.value == ASK_BID_SCREEN_BID_TAB && marketState == SELECTED_BTC_MARKET) {
            Text(
                text = "= $currentBTCCoinPrice  $SYMBOL_KRW",
                modifier = Modifier
                    .padding(0.dp, 2.dp)
                    .align(Alignment.End),
                style = TextStyle(color = Color.DarkGray, fontSize = 12.sp)
            )
        }
    }
}

@Composable
fun OrderScreenQuantity(coinDetailViewModel: CoinDetailViewModel = viewModel(), marketState: Int) {
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
                text = stringResource(id = R.string.quantity),
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
        OrderScreenQuantityDropDown(Modifier.weight(1f), coinDetailViewModel, marketState)
    }
}

@Composable
fun OrderScreenQuantityDropDown(
    modifier: Modifier,
    coinDetailViewModel: CoinDetailViewModel = viewModel(),
    marketState: Int,
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
                ASK_BID_SCREEN_BID_TAB -> {
                    bidButtonText.value
                }
                ASK_BID_SCREEN_ASK_TAB -> {
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
                    if (coinDetailViewModel.askBidSelectedTab.value == ASK_BID_SCREEN_BID_TAB && coinDetailViewModel.currentTradePriceState != 0.0) {
                        bidButtonText.value = label
                        val bidFee = coinDetailViewModel.getFee(PREF_KEY_KRW_BID_FEE)
                        val quantity = if (marketState == SELECTED_KRW_MARKET) {
                            Calculator.orderScreenSpinnerBidValueCalculator(
                                bidButtonText.value,
                                coinDetailViewModel.userSeedMoney,
                                coinDetailViewModel.currentTradePriceState,
                                bidFee
                            )
                        } else {
                            val bidFee2 = coinDetailViewModel.getFee(PREF_KEY_BTC_BID_FEE)
                            Calculator.orderScreenSpinnerBidValueCalculatorForBTC(
                                bidButtonText.value,
                                coinDetailViewModel.btcQuantity.value,
                                coinDetailViewModel.currentTradePriceState,
                                bidFee2
                            )
                        }
                        val quantityResult = quantity.toDoubleOrNull()
                        if (quantityResult != 0.0 && quantityResult != null) {
                            coinDetailViewModel.bidQuantity.value = quantity
                        }
                    } else if (coinDetailViewModel.askBidSelectedTab.value == ASK_BID_SCREEN_ASK_TAB && coinDetailViewModel.currentTradePriceState != 0.0) {
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
fun OrderScreenPrice(coinDetailViewModel: CoinDetailViewModel = viewModel(), marketState: Int) {
    Row(
        modifier = Modifier
            .padding(0.dp, 5.dp)
            .fillMaxWidth()
            .height(35.dp)
            .border(0.7.dp, Color.LightGray)
            .wrapContentHeight()
    ) {
        Text(
            text = stringResource(id = R.string.price), modifier = Modifier
                .wrapContentWidth()
                .padding(8.dp, 0.dp, 8.dp, 0.dp), style = TextStyle(fontSize = 15.sp)
        )
        Text(
            text = CurrentCalculator.tradePriceCalculator(coinDetailViewModel.currentTradePriceState,
                marketState),
            modifier = Modifier.weight(1f, true),
            textAlign = TextAlign.End,
            style = TextStyle(fontSize = 15.sp)
        )
        Text(
            text = "  ".plus(EtcUtils.getUnit(marketState)),
            modifier = Modifier
                .wrapContentWidth()
                .padding(0.dp, 0.dp, 8.dp, 0.dp),
            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun OrderScreenTotalPrice(
    coinDetailViewModel: CoinDetailViewModel = viewModel(),
    marketState: Int,
) {
    val selectedMarket = EtcUtils.getSelectedMarket(coinDetailViewModel.market)
    val totalPrice = if (coinDetailViewModel.askBidSelectedTab.value == ASK_BID_SCREEN_BID_TAB) {
        if (coinDetailViewModel.bidQuantity.value.isEmpty()) {
            "0"
        } else {
            Calculator.orderScreenTotalPriceCalculator(
                coinDetailViewModel.bidQuantity.value.toDouble(),
                coinDetailViewModel.currentTradePriceState,
                marketState
            )
        }
    } else {
        if (coinDetailViewModel.askQuantity.value.isEmpty()) {
            "0"
        } else {
            Calculator.orderScreenTotalPriceCalculator(
                coinDetailViewModel.askQuantity.value.toDouble(),
                coinDetailViewModel.currentTradePriceState,
                marketState
            )
        }
    }
    val totalPriceBtcToKrw = if (marketState == SELECTED_BTC_MARKET) {
        CurrentCalculator.tradePriceCalculator(coinDetailViewModel.currentBTCPrice.value * totalPrice.toDouble(),
            SELECTED_KRW_MARKET)
    } else {
        "0"
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()) {
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
                text = totalPrice,
                modifier = Modifier.weight(1f, true),
                textAlign = TextAlign.End,
                style = TextStyle(fontSize = 15.sp)
            )
            Text(
                text = "  ".plus(EtcUtils.getUnit(marketState)),
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(0.dp, 0.dp, 8.dp, 0.dp),
                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold)
            )
        }
        if (selectedMarket == SELECTED_BTC_MARKET) {
            Text(
                text = "= $totalPriceBtcToKrw  $SYMBOL_KRW",
                modifier = Modifier
                    .padding(5.dp, 2.dp)
                    .align(Alignment.End),
                style = TextStyle(color = Color.DarkGray, fontSize = 12.sp)
            )
        }
    }
}

@Composable
fun OrderScreenButtons(coinDetailViewModel: CoinDetailViewModel = viewModel(), marketState: Int) {
    val buttonBackground = getButtonsBackground(coinDetailViewModel.askBidSelectedTab.value)
    val buttonText = getButtonsText(coinDetailViewModel.askBidSelectedTab.value)
    val currentPrice = remember {
        mutableStateOf(0.0)
    }
    val context = LocalContext.current
    TotalAmountDesignatedDialog(coinDetailViewModel)
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
            // 초기화 버튼
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
                Text(text = stringResource(id = R.string.reset),
                    style = TextStyle(color = Color.White),
                    fontSize = 18.sp)
            }

            // 매수 or 매도버튼
            TextButton(
                onClick = {
                    currentPrice.value = coinDetailViewModel.currentTradePriceState // 현재 코인 가격
                    val quantity = if (coinDetailViewModel.askBidSelectedTab.value == 1) {
                        coinDetailViewModel.bidQuantity.value.ifEmpty { "0" }
                    } else {
                        coinDetailViewModel.askQuantity.value.ifEmpty { "0" }
                    }
                    val totalPrice = Calculator.orderScreenBidTotalPriceCalculator(
                        quantity.toDouble(),
                        currentPrice.value,
                        marketState
                    )
                    val userSeedMoney = coinDetailViewModel.userSeedMoney
                    val userCoin = coinDetailViewModel.userCoinQuantity
                    val selectedTab = coinDetailViewModel.askBidSelectedTab.value
                    val userBtcCoin = coinDetailViewModel.btcQuantity
                    if (marketState == SELECTED_KRW_MARKET) {
                        val fee = coinDetailViewModel.getFee(PREF_KEY_KRW_BID_FEE)
                        when {
                            currentPrice.value == 0.0 -> {
                                context.showToast(context.getString(R.string.NETWORK_ERROR))
                            }
                            OneTimeNetworkCheck.networkCheck(context) == null -> {
                                context.showToast(context.getString(R.string.NO_INTERNET_CONNECTION))
                            }
                            UpBitCoinDetailWebSocket.currentSocketState != SOCKET_IS_CONNECTED -> {
                                context.showToast(context.getString(R.string.NETWORK_ERROR))
                            }
                            totalPrice.toLong() < 5000 -> {
                                context.showToast(context.getString(R.string.notMinimumOrderMessage))
                            }
                            selectedTab == ASK_BID_SCREEN_BID_TAB && userSeedMoney < (totalPrice + floor(
                                totalPrice * (fee * 0.01))) -> {
                                context.showToast(context.getString(R.string.youHaveNoMoneyMessage))
                            }
                            selectedTab == ASK_BID_SCREEN_ASK_TAB && userCoin.eighthDecimal()
                                .toDouble() < coinDetailViewModel.askQuantity.value.toDouble() -> {
                                context.showToast(context.getString(R.string.youHaveNoCoinMessage))
                            }
                            else -> {
                                if (selectedTab == ASK_BID_SCREEN_BID_TAB) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        coinDetailViewModel.bidRequest(
                                            currentPrice.value,
                                            quantity.toDouble(),
                                            totalPrice.toLong(),
                                        ).join()
                                        context.showToast(context.getString(R.string.completeBidMessage))
                                    }
                                } else {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        coinDetailViewModel.askRequest(
                                            quantity.toDouble(),
                                            totalPrice.toLong(),
                                            currentPrice.value
                                        ).join()
                                        context.showToast(context.getString(R.string.completeAskMessage))
                                    }
                                }
                            }
                        }
                    } else {
                        val currentBtcPrice = coinDetailViewModel.currentBTCPrice.value
                        val fee = coinDetailViewModel.getFee(PREF_KEY_BTC_BID_FEE)
                        when {
                            currentPrice.value == 0.0 -> {
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
                            selectedTab == ASK_BID_SCREEN_BID_TAB && userBtcCoin.value.eighthDecimal()
                                .toDouble() < totalPrice + (floor(totalPrice * (fee * 0.01) * 100000000) * 0.00000001) -> {
                                context.showToast(context.getString(R.string.youHaveNoMoneyMessage))
                            }
                            selectedTab == ASK_BID_SCREEN_ASK_TAB && userCoin.eighthDecimal()
                                .toDouble() < coinDetailViewModel.askQuantity.value.toDouble() -> {
                                context.showToast(context.getString(R.string.youHaveNoCoinMessage))
                            }
                            else -> {
                                if (selectedTab == ASK_BID_SCREEN_BID_TAB) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        coinDetailViewModel.bidRequest(
                                            currentPrice.value,
                                            quantity.toDouble(),
                                            btcTotalPrice = totalPrice,
                                            currentBtcPrice = currentBtcPrice
                                        ).join()
                                        context.showToast(context.getString(R.string.completeBidMessage))
                                    }
                                } else {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        coinDetailViewModel.askRequest(
                                            quantity.toDouble(),
                                            totalPrice.toLong(),
                                            currentPrice.value,
                                            btcTotalPrice = totalPrice
                                        ).join()
                                        context.showToast(context.getString(R.string.completeAskMessage))
                                    }
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
    }
}

@Composable
fun OrderScreenNotice(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    marketState: Int,
    coinDetailViewModel: CoinDetailViewModel
) {
    val texts = EtcUtils.getCoinDetailScreenInfo(marketState,coinDetailViewModel.askBidSelectedTab.value)
    val fee = coinDetailViewModel.feeStateList[texts[1].toInt()].value.toString()
    val balloon = remember {
        Balloon.Builder(context)
            .setWidthRatio(1.0f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setText(context.getString(R.string.askBidBalloonMessage))
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
                text = stringResource(id = R.string.minimumOrderAmount),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
                style = TextStyle(color = Color.Gray)
            )
            Text(
                text = texts[0],
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                style = TextStyle(color = Color.Gray)
            )
        }
        Row(
            Modifier
                .padding(0.dp, 10.dp, 0.dp, 0.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text = stringResource(id = R.string.fee),
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                textAlign = TextAlign.Start,
                style = TextStyle(color = Color.Gray)
            )
            Text(
                text = fee.plus("%"),
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                textAlign = TextAlign.End,
                style = TextStyle(color = Color.Gray)
            )
            Image(painter = painterResource(id = R.drawable.ic_baseline_tune_24),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 3.dp)
                    .size(25.dp)
                    .align(Alignment.CenterVertically)
                    .clickable {
                        coinDetailViewModel.isShowAdjustFeeDialog.value = true
                    }
            )
        }
        AndroidView(
            factory = {
                ImageView(it).apply {
                    val drawable = ContextCompat.getDrawable(it, R.drawable.img_info)
                    setImageDrawable(drawable)
                    setOnClickListener {
                        showAlignTop(balloon)
                    }
                }
            }, modifier = Modifier
                .padding(0.dp, 10.dp, 0.dp, 0.dp)
                .size(25.dp)
                .align(Alignment.End)
        )
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
        stringResource(id = R.string.bid)
    } else {
        stringResource(id = R.string.ask)
    }
}