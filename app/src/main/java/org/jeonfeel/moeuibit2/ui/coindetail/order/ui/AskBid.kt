package org.jeonfeel.moeuibit2.ui.coindetail.order.ui

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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.showAlignTop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.ui.coindetail.order.AdjustCommissionDialog
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.ui.custom.DpToSp
import org.jeonfeel.moeuibit2.ui.custom.OrderScreenQuantityTextField
import org.jeonfeel.moeuibit2.ui.theme.*
import org.jeonfeel.moeuibit2.ui.viewmodels.CoinDetailViewModel
import org.jeonfeel.moeuibit2.utils.*
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator
import kotlin.math.floor
import kotlin.math.round

@Composable
fun OrderScreenAskBid(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    val marketState = Utils.getSelectedMarket(coinDetailViewModel.market)
    AdjustCommissionDialog(
        dialogState = coinDetailViewModel.coinOrder.state.showAdjustFeeDialog,
        commissionStateList = coinDetailViewModel.coinOrder.state.commissionStateList,
        coinDetailViewModel = coinDetailViewModel
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        OrderScreenTabs(
            askBidSelectedTab = coinDetailViewModel.coinOrder.state.askBidSelectedTab,
            coinDetailViewModel::getTransactionInfoList
        )
        Box(
            modifier = Modifier
                .padding(10.dp, 0.dp)
                .weight(1f)
        ) {
            if (coinDetailViewModel.coinOrder.state.askBidSelectedTab.value != ASK_BID_SCREEN_TRANSACTION_TAB) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    OrderScreenUserSeedMoney(
                        askBidSelectedTab = coinDetailViewModel.coinOrder.state.askBidSelectedTab,
                        marketState = marketState,
                        symbol = coinDetailViewModel.market.substring(4),
                        userCoinQuantity = coinDetailViewModel.coinOrder.state.userCoinQuantity,
                        currentTradePriceState = coinDetailViewModel.coinOrder.state.currentTradePriceState,
                        currentBTCPrice = coinDetailViewModel.coinOrder.state.currentBTCPrice,
                        btcQuantity = coinDetailViewModel.coinOrder.state.btcQuantity,
                        userSeedMoney = coinDetailViewModel.coinOrder.state.userSeedMoney
                    )
                    OrderScreenQuantity(coinDetailViewModel, marketState)
                    OrderScreenPrice(
                        currentTradePriceState = coinDetailViewModel.coinOrder.state.currentTradePriceState,
                        marketState = marketState
                    )
                    OrderScreenTotalPrice(
                        marketState = marketState,
                        askBidSelectedTab = coinDetailViewModel.coinOrder.state.askBidSelectedTab,
                        bidQuantity = coinDetailViewModel.coinOrder.state.bidQuantity,
                        askQuantity = coinDetailViewModel.coinOrder.state.askQuantity,
                        currentTradePriceState = coinDetailViewModel.coinOrder.state.currentTradePriceState,
                        currentBTCPrice = coinDetailViewModel.coinOrder.state.currentBTCPrice
                    )
                    OrderScreenButtons(coinDetailViewModel, marketState)
                    OrderScreenNotice(
                        marketState = marketState,
                        askBidSelectedTab = coinDetailViewModel.coinOrder.state.askBidSelectedTab,
                        commissionStateList = coinDetailViewModel.coinOrder.state.commissionStateList,
                        showAdjustCommissionDialog = coinDetailViewModel.coinOrder.state.showAdjustFeeDialog
                    )
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
fun OrderScreenTabs(
    askBidSelectedTab: MutableState<Int>,
    getTransactionInfoList: suspend () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        Modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        Box(modifier = Modifier
            .background(getTabBackGround(askBidSelectedTab.value, ASK_BID_SCREEN_BID_TAB))
            .weight(1f)
            .fillMaxHeight()
            .wrapContentHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { askBidSelectedTab.value = ASK_BID_SCREEN_BID_TAB }) {
            Text(
                text = stringResource(id = R.string.bid),
                modifier = Modifier.fillMaxWidth(),
                style = getTabTextStyle(askBidSelectedTab.value, ASK_BID_SCREEN_BID_TAB)
            )
        }
        Box(modifier = Modifier
            .background(getTabBackGround(askBidSelectedTab.value, ASK_BID_SCREEN_ASK_TAB))
            .weight(1f)
            .fillMaxHeight()
            .wrapContentHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { askBidSelectedTab.value = ASK_BID_SCREEN_ASK_TAB }
        ) {
            Text(
                text = stringResource(id = R.string.ask),
                modifier = Modifier.fillMaxWidth(),
                style = getTabTextStyle(askBidSelectedTab.value, ASK_BID_SCREEN_ASK_TAB)
            )
        }
        Box(modifier = Modifier
            .background(
                getTabBackGround(
                    askBidSelectedTab.value,
                    ASK_BID_SCREEN_TRANSACTION_TAB
                )
            )
            .weight(1f)
            .fillMaxHeight()
            .wrapContentHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                CoroutineScope(mainDispatcher).launch {
                    getTransactionInfoList()
                    askBidSelectedTab.value = 3
                }
            }
        ) {
            Text(
                text = stringResource(id = R.string.tradHistory),
                modifier = Modifier.fillMaxWidth(),
                style = getTabTextStyle(askBidSelectedTab.value, ASK_BID_SCREEN_TRANSACTION_TAB)
            )
        }
    }
}

/**
 * 유저가 할 수 있는 매수, 매도 수량
 */
@Composable
fun OrderScreenUserSeedMoney(
    askBidSelectedTab: MutableState<Int>,
    marketState: Int,
    symbol: String,
    userCoinQuantity: MutableState<Double>,
    currentTradePriceState: MutableState<Double>,
    currentBTCPrice: MutableState<Double>,
    btcQuantity: MutableState<Double>,
    userSeedMoney: MutableState<Long>
) {
    val krwOrSymbol: String // 환산 심볼인지 krw인지
    val currentBTCCoinPrice =
        CurrentCalculator.tradePriceCalculator(
            currentBTCPrice.value * btcQuantity.value, // btc 가격 * 수량
            SELECTED_KRW_MARKET
        )
    var currentUserCoinValue = "" // 유저 보유 코인 총가격

    val userSeedMoneyOrCoin =
        if (askBidSelectedTab.value == ASK_BID_SCREEN_BID_TAB && marketState == SELECTED_KRW_MARKET) { // KRW 매수
            krwOrSymbol = SYMBOL_KRW
            userSeedMoney.value.commaFormat()
        } else if (askBidSelectedTab.value == ASK_BID_SCREEN_ASK_TAB && marketState == SELECTED_KRW_MARKET) { // KRW 매도
            currentUserCoinValue = if (userCoinQuantity.value == 0.0) {
                "0"
            } else {
                round(currentTradePriceState.value * userCoinQuantity.value).commaFormat()
            }
            krwOrSymbol = symbol
            if (userCoinQuantity.value == 0.0) {
                "0"
            } else {
                userCoinQuantity.value.eighthDecimal()
            }

        } else if (askBidSelectedTab.value == ASK_BID_SCREEN_BID_TAB && marketState == SELECTED_BTC_MARKET) { // BTC 매수
            krwOrSymbol = SYMBOL_BTC
            if (btcQuantity.value == 0.0 || btcQuantity.value < 0.00000001) {
                "0"
            } else {
                btcQuantity.value.eighthDecimal()
            }

        } else { // BTC 매도
            currentUserCoinValue = if (userCoinQuantity.value == 0.0) {
                "0"
            } else {
                round(currentTradePriceState.value * userCoinQuantity.value * currentBTCPrice.value).commaFormat()
            }
            krwOrSymbol = symbol

            if (userCoinQuantity.value == 0.0) {
                "0"
            } else {
                userCoinQuantity.value.eighthDecimal()
            }
        }

    Column(
        modifier = Modifier
            .padding(0.dp, 10.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row {
            Text(
                text = stringResource(id = R.string.orderable),
                modifier = Modifier.wrapContentWidth(),
                fontSize = 13.sp,
                style = TextStyle(color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground)
            )
            AutoSizeText(
                modifier = Modifier.weight(1f, true),
                text = userSeedMoneyOrCoin,
                textStyle = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    fontSize = DpToSp(15.dp)
                )
            )
            Text(
                text = "  ".plus(krwOrSymbol),
                modifier = Modifier.wrapContentWidth(),
                fontWeight = FontWeight.Bold, fontSize = DpToSp(15.dp),
                textAlign = TextAlign.End,
                style = TextStyle(color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground)
            )
        }

        // 코인 현재 KRW 가격
        if (askBidSelectedTab.value == ASK_BID_SCREEN_ASK_TAB || marketState == SELECTED_BTC_MARKET && askBidSelectedTab.value == ASK_BID_SCREEN_ASK_TAB) {
            Text(
                text = "= $currentUserCoinValue  $SYMBOL_KRW",
                modifier = Modifier
                    .padding(0.dp, 2.dp)
                    .align(Alignment.End),
                style = TextStyle(color = userHoldCoinPriceColor(), fontSize = DpToSp(12.dp))
            )
        } else if (askBidSelectedTab.value == ASK_BID_SCREEN_BID_TAB && marketState == SELECTED_BTC_MARKET) {
            Text(
                text = "= $currentBTCCoinPrice  $SYMBOL_KRW",
                modifier = Modifier
                    .padding(0.dp, 2.dp)
                    .align(Alignment.End),
                style = TextStyle(color = userHoldCoinPriceColor(), fontSize = DpToSp(12.dp))
            )
        }
    }
}

@Composable
fun OrderScreenQuantity(coinDetailViewModel: CoinDetailViewModel, marketState: Int) {
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
                style = TextStyle(
                    fontSize = DpToSp(15.dp),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                )
            )
            OrderScreenQuantityTextField(
                modifier = Modifier.weight(1f, true),
                placeholderText = "0",
                fontSize = DpToSp(15.dp),
                askBidSelectedTab = coinDetailViewModel.coinOrder.state.askBidSelectedTab,
                bidQuantity = coinDetailViewModel.coinOrder.state.bidQuantity,
                askQuantity = coinDetailViewModel.coinOrder.state.askQuantity,
                currentTradePriceState = coinDetailViewModel.coinOrder.state.currentTradePriceState
            )
        }
        OrderScreenQuantityDropDown(
            marketState = marketState,
            askBidSelectedTab = coinDetailViewModel.coinOrder.state.askBidSelectedTab,
            currentTradePriceState = coinDetailViewModel.coinOrder.state.currentTradePriceState,
            getCommission = coinDetailViewModel::getCommission,
            userSeedMoney = coinDetailViewModel.coinOrder.state.userSeedMoney,
            btcQuantity = coinDetailViewModel.coinOrder.state.btcQuantity,
            userCoinQuantity = coinDetailViewModel.coinOrder.state.userCoinQuantity,
            askQuantity = coinDetailViewModel.coinOrder.state.askQuantity,
            bidQuantity = coinDetailViewModel.coinOrder.state.bidQuantity
        )
    }
}

@Composable
fun RowScope.OrderScreenQuantityDropDown(
    marketState: Int,
    askBidSelectedTab: MutableState<Int>,
    currentTradePriceState: MutableState<Double>,
    getCommission: (String) -> Float,
    userSeedMoney: MutableState<Long>,
    btcQuantity: MutableState<Double>,
    userCoinQuantity: MutableState<Double>,
    askQuantity: MutableState<String>,
    bidQuantity: MutableState<String>
) {
    val possible = stringResource(id = R.string.possible)
    val max = stringResource(id = R.string.max)
    val bidButtonText = remember { mutableStateOf(possible) }
    val askButtonText = remember { mutableStateOf(possible) }
    val suggestions = remember { listOf(max, "50%", "25%", "10%") }
    val expanded = remember { mutableStateOf(false) }
    val imageVector = if (expanded.value) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }
    val textButtonWidth = remember {
        mutableStateOf(0f)
    }

    Box(modifier = Modifier.weight(1f)) {
        TextButton(
            onClick = { expanded.value = !expanded.value },
            modifier = Modifier.onGloballyPositioned { coordinates ->
                textButtonWidth.value = coordinates.size.toSize().width
            }
        ) {
            val buttonText = when (askBidSelectedTab.value) {
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

            Text(
                buttonText,
                style = TextStyle(color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground, fontSize = DpToSp(14.dp))
            )
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
            )
        }
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier
                .background(color = androidx.compose.material3.MaterialTheme.colorScheme.background)
                .width(with(LocalDensity.current) { textButtonWidth.value.toDp() })
        ) {
            suggestions.forEach { label ->
                DropdownMenuItem(onClick = {
                    expanded.value = false
                    if (askBidSelectedTab.value == ASK_BID_SCREEN_BID_TAB && currentTradePriceState.value != 0.0) {
                        bidButtonText.value = label
                        val bidFee = getCommission(PREF_KEY_KRW_BID_COMMISSION)
                        val quantity = if (marketState == SELECTED_KRW_MARKET) {
                            Calculator.orderScreenSpinnerBidValueCalculator(
                                bidButtonText.value,
                                userSeedMoney.value,
                                currentTradePriceState.value,
                                bidFee
                            )
                        } else {
                            val bidFee2 = getCommission(PREF_KEY_BTC_BID_COMMISSION)
                            Calculator.orderScreenSpinnerBidValueCalculatorForBTC(
                                bidButtonText.value,
                                btcQuantity.value,
                                currentTradePriceState.value,
                                bidFee2
                            )
                        }
                        val quantityResult = quantity.toDoubleOrNull()
                        if (quantityResult != 0.0 && quantityResult != null) {
                            bidQuantity.value = quantity
                        }
                    } else if (askBidSelectedTab.value == ASK_BID_SCREEN_ASK_TAB && currentTradePriceState.value != 0.0) {
                        askButtonText.value = label
                        val quantity = Calculator.orderScreenSpinnerAskValueCalculator(
                            askButtonText.value,
                            userCoinQuantity.value
                        )
                        val quantityResult = quantity.toDoubleOrNull()
                        if (quantityResult != 0.0 && quantityResult != null) {
                            askQuantity.value = quantity
                        }
                    }
                }) {
                    Text(
                        text = label,
                        style = TextStyle(color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground, fontSize = DpToSp(14.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun OrderScreenPrice(
    currentTradePriceState: MutableState<Double>,
    marketState: Int
) {
    Row(
        modifier = Modifier
            .padding(0.dp, 5.dp)
            .fillMaxWidth()
            .height(35.dp)
            .border(0.7.dp, Color.LightGray)
            .wrapContentHeight()
    ) {
        Text(
            text = stringResource(id = R.string.price),
            modifier = Modifier
                .wrapContentWidth()
                .padding(8.dp, 0.dp, 8.dp, 0.dp),
            style = TextStyle(
                fontSize = DpToSp(15.dp),
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
            )
        )
        Text(
            text = CurrentCalculator.tradePriceCalculator(
                currentTradePriceState.value,
                marketState
            ),
            modifier = Modifier.weight(1f, true),
            textAlign = TextAlign.End,
            style = TextStyle(
                fontSize = DpToSp(15.dp),
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
            )
        )
        Text(
            text = "  ".plus(Utils.getUnit(marketState)),
            modifier = Modifier
                .wrapContentWidth()
                .padding(0.dp, 0.dp, 8.dp, 0.dp),
            style = TextStyle(
                fontSize = DpToSp(15.dp),
                fontWeight = FontWeight.Bold,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
            )
        )
    }
}

@Composable
fun OrderScreenTotalPrice(
    marketState: Int,
    askBidSelectedTab: MutableState<Int>,
    bidQuantity: MutableState<String>,
    askQuantity: MutableState<String>,
    currentTradePriceState: MutableState<Double>,
    currentBTCPrice: MutableState<Double>,
) {
    val totalPrice = if (askBidSelectedTab.value == ASK_BID_SCREEN_BID_TAB) {
        if (bidQuantity.value.isEmpty()) {
            "0"
        } else {
            Calculator.orderScreenTotalPriceCalculator(
                bidQuantity.value.toDouble(),
                currentTradePriceState.value,
                marketState
            )
        }
    } else {
        if (askQuantity.value.isEmpty()) {
            "0"
        } else {
            Calculator.orderScreenTotalPriceCalculator(
                askQuantity.value.toDouble(),
                currentTradePriceState.value,
                marketState
            )
        }
    }
    val totalPriceBtcToKrw = if (marketState == SELECTED_BTC_MARKET) {
        CurrentCalculator.tradePriceCalculator(
            currentBTCPrice.value * totalPrice.toDouble(),
            SELECTED_KRW_MARKET
        )
    } else {
        "0"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 5.dp)
                .fillMaxWidth()
                .height(35.dp)
                .border(0.7.dp, Color.LightGray)
                .wrapContentHeight()
        ) {
            Text(
                text = stringResource(id = R.string.total),
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(8.dp, 0.dp, 8.dp, 0.dp),
                style = TextStyle(
                    fontSize = DpToSp(15.dp),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = totalPrice,
                modifier = Modifier.weight(1f, true),
                textAlign = TextAlign.End,
                style = TextStyle(
                    fontSize = DpToSp(15.dp),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = "  ".plus(Utils.getUnit(marketState)),
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(0.dp, 0.dp, 8.dp, 0.dp),
                style = TextStyle(
                    fontSize = DpToSp(15.dp),
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                )
            )
        }
        if (marketState == SELECTED_BTC_MARKET) {
            Text(
                text = "= $totalPriceBtcToKrw  $SYMBOL_KRW",
                modifier = Modifier
                    .padding(5.dp, 2.dp)
                    .align(Alignment.End),
                style = TextStyle(
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                    fontSize = DpToSp(12.dp)
                )
            )
        }
    }
}

@Composable
fun OrderScreenButtons(coinDetailViewModel: CoinDetailViewModel, marketState: Int) {
    val buttonBackground =
        getButtonsBackground(coinDetailViewModel.coinOrder.state.askBidSelectedTab.value)
    val buttonText = getButtonsText(coinDetailViewModel.coinOrder.state.askBidSelectedTab.value)
    val currentPrice = remember {
        mutableStateOf(0.0)
    }
    val context = LocalContext.current
    TotalAmountDesignatedDialog(
        askBidDialogState = coinDetailViewModel.coinOrder.state.askBidDialogState,
        userSeedMoney = coinDetailViewModel.coinOrder.state.userSeedMoney,
        btcQuantity = coinDetailViewModel.coinOrder.state.btcQuantity,
        currentBTCPrice = coinDetailViewModel.coinOrder.state.currentBTCPrice,
        userCoinQuantity = coinDetailViewModel.coinOrder.state.userCoinQuantity,
        currentTradePriceState = coinDetailViewModel.coinOrder.state.currentTradePriceState,
        askBidSelectedTab = coinDetailViewModel.coinOrder.state.askBidSelectedTab,
        totalPriceDesignated = coinDetailViewModel.coinOrder.state.totalPriceDesignated,
        marketState = Utils.getSelectedMarket(coinDetailViewModel.market),
        getCommission = coinDetailViewModel::getCommission,
        askRequest = coinDetailViewModel::askRequest,
        bidRequest = coinDetailViewModel::bidRequest
    )
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
                    if (coinDetailViewModel.coinOrder.state.askBidSelectedTab.value == 1) {
                        coinDetailViewModel.coinOrder.state.bidQuantity.value = ""
                    } else {
                        coinDetailViewModel.coinOrder.state.askQuantity.value = ""
                    }
                }, modifier = Modifier
                    .padding(0.dp, 0.dp, 4.dp, 0.dp)
                    .weight(1f)
                    .background(color = Color.LightGray)
                    .fillMaxHeight()
            ) {
                Text(
                    text = stringResource(id = R.string.reset),
                    style = TextStyle(color = Color.White),
                    fontSize = DpToSp(18.dp)
                )
            }

            // 매수 or 매도버튼
            TextButton(
                onClick = {
                    currentPrice.value =
                        coinDetailViewModel.coinOrder.state.currentTradePriceState.value // 현재 코인 가격
                    val quantity =
                        if (coinDetailViewModel.coinOrder.state.askBidSelectedTab.value == 1) {
                            coinDetailViewModel.coinOrder.state.bidQuantity.value.ifEmpty { "0" }
                        } else {
                            coinDetailViewModel.coinOrder.state.askQuantity.value.ifEmpty { "0" }
                        }
                    val totalPrice = Calculator.orderScreenBidTotalPriceCalculator(
                        quantity.toDouble(),
                        currentPrice.value,
                        marketState
                    )
                    val userSeedMoney = coinDetailViewModel.coinOrder.state.userSeedMoney
                    val userCoin = coinDetailViewModel.coinOrder.state.userCoinQuantity
                    val selectedTab = coinDetailViewModel.coinOrder.state.askBidSelectedTab.value
                    val userBtcCoin = coinDetailViewModel.coinOrder.state.btcQuantity
                    if (marketState == SELECTED_KRW_MARKET) {
                        val commission =
                            coinDetailViewModel.getCommission(PREF_KEY_KRW_BID_COMMISSION)
                        when {
                            currentPrice.value == 0.0 -> {
                                context.showToast(context.getString(R.string.NETWORK_ERROR))
                            }
                            OneTimeNetworkCheck.networkCheck(context) == null -> {
                                context.showToast(context.getString(R.string.NO_INTERNET_CONNECTION))
                            }
                            UpBitTickerWebSocket.currentSocketState != SOCKET_IS_CONNECTED -> {
                                context.showToast(context.getString(R.string.NETWORK_ERROR))
                            }
                            totalPrice.toLong() < 5000 -> {
                                context.showToast(context.getString(R.string.notMinimumOrderMessage))
                            }
                            selectedTab == ASK_BID_SCREEN_BID_TAB && userSeedMoney.value < (totalPrice + floor(
                                totalPrice * (commission * 0.01)
                            )) -> {
                                context.showToast(context.getString(R.string.youHaveNoMoneyMessage))
                            }
                            selectedTab == ASK_BID_SCREEN_ASK_TAB && userCoin.value.eighthDecimal()
                                .toDouble() < coinDetailViewModel.coinOrder.state.askQuantity.value.toDouble() -> {
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
                        val currentBtcPrice =
                            coinDetailViewModel.coinOrder.state.currentBTCPrice.value
                        val fee = coinDetailViewModel.getCommission(PREF_KEY_BTC_BID_COMMISSION)
                        when {
                            currentPrice.value == 0.0 || currentBtcPrice == 0.0 -> {
                                context.showToast(context.getString(R.string.NETWORK_ERROR))
                            }
                            OneTimeNetworkCheck.networkCheck(context) == null -> {
                                context.showToast(context.getString(R.string.NO_INTERNET_CONNECTION))
                            }
                            UpBitTickerWebSocket.currentSocketState != SOCKET_IS_CONNECTED -> {
                                context.showToast(context.getString(R.string.NETWORK_ERROR))
                            }
                            totalPrice < 0.0005 -> {
                                context.showToast(context.getString(R.string.notMinimumOrderBtcMessage))
                            }
                            selectedTab == ASK_BID_SCREEN_BID_TAB && userBtcCoin.value.eighthDecimal()
                                .toDouble() < totalPrice + (floor(totalPrice * (fee * 0.01) * 100000000) * 0.00000001) -> {
                                context.showToast(context.getString(R.string.youHaveNoMoneyMessage))
                            }
                            selectedTab == ASK_BID_SCREEN_ASK_TAB && userCoin.value.eighthDecimal()
                                .toDouble() < coinDetailViewModel.coinOrder.state.askQuantity.value.toDouble() -> {
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
                Text(
                    text = buttonText,
                    style = TextStyle(color = Color.White),
                    fontSize = DpToSp(18.dp)
                )
            }
        }
        TextButton(
            onClick = {
                coinDetailViewModel.coinOrder.state.totalPriceDesignated.value = ""
                coinDetailViewModel.coinOrder.state.askBidDialogState.value = true
            }, modifier = Modifier
                .padding(0.dp, 5.dp)
                .fillMaxWidth()
                .background(buttonBackground)
                .height(40.dp)
        ) {
            Text(
                text = stringResource(id = R.string.designation).plus(" ").plus(buttonText),
                style = TextStyle(color = Color.White),
                fontSize = DpToSp(18.dp)
            )
        }
    }
}

@Composable
fun OrderScreenNotice(
    marketState: Int,
    askBidSelectedTab: MutableState<Int>,
    commissionStateList: SnapshotStateList<MutableState<Float>>,
    showAdjustCommissionDialog: MutableState<Boolean>
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val texts =
        Utils.getCoinDetailScreenInfo(marketState, askBidSelectedTab.value)
    val fee = commissionStateList[texts[1].toInt()].value.toString()
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
                text = stringResource(id = R.string.commission),
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
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = stringResource(id = R.string.adjust_commission),
                style = TextStyle(
                    color = decreaseColor(),
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        showAdjustCommissionDialog.value = true
                    },
                textAlign = TextAlign.Start
            )
            AndroidView(
                modifier = Modifier.size(25.dp),
                factory = {
                    ImageView(it).apply {
                        val drawable = ContextCompat.getDrawable(it, R.drawable.img_info)
                        setImageDrawable(drawable)
                        setOnClickListener {
                            showAlignTop(balloon)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun getTabTextStyle(selectedTab: Int, tabNum: Int): TextStyle {
    return if (selectedTab == tabNum) {
        TextStyle(
            color = orderBookTabTextColor(),
            fontWeight = FontWeight.Bold,
            fontSize = DpToSp(15.dp),
            textAlign = TextAlign.Center
        )
    } else {
        val color = if (!isSystemInDarkTheme()) {
            Color.Gray
        } else {
            Color.LightGray
        }
        TextStyle(color = color, fontSize = DpToSp(15.dp), textAlign = TextAlign.Center)
    }
}

@Composable
fun getTabBackGround(selectedTab: Int, tabNum: Int): Color {
    return if (selectedTab == tabNum) {
        androidx.compose.material3.MaterialTheme.colorScheme.background
    } else {
        orderBookTabBackground()
    }
}

@Composable
fun getButtonsBackground(selectedTab: Int): Color {
    return if (selectedTab == 1) {
        increaseColor()
    } else {
        decreaseColor()
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