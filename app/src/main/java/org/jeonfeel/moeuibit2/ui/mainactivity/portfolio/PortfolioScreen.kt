package org.jeonfeel.moeuibit2.ui.mainactivity.portfolio

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.coindetail.CoinDetailActivity
import org.jeonfeel.moeuibit2.activity.main.MainActivity
import org.jeonfeel.moeuibit2.activity.main.viewmodel.MainViewModel
import org.jeonfeel.moeuibit2.constant.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitPortfolioWebSocket
import org.jeonfeel.moeuibit2.ui.coindetail.chart.UserHoldCoinPieChart
import org.jeonfeel.moeuibit2.ui.common.TwoButtonCommonDialog
import org.jeonfeel.moeuibit2.ui.common.CommonLoadingDialog
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.ui.custom.PortfolioAutoSizeText
import org.jeonfeel.moeuibit2.ui.util.drawUnderLine
import org.jeonfeel.moeuibit2.util.AddLifecycleEvent
import org.jeonfeel.moeuibit2.util.NetworkMonitorUtil
import org.jeonfeel.moeuibit2.util.calculator.Calculator
import org.jeonfeel.moeuibit2.util.secondDecimal
import org.jeonfeel.moeuibit2.util.showToast
import kotlin.math.round

@Composable
fun PortfolioScreen(
    mainViewModel: MainViewModel = viewModel(),
    startForActivityResult: ActivityResultLauncher<Intent>,
    scaffoldState: ScaffoldState,
) {
    val context = LocalContext.current
    val dialogState = remember {
        mutableStateOf(false)
    }
    val editHoldCoinDialogState = remember {
        mutableStateOf(false)
    }
    if (editHoldCoinDialogState.value) {
        EditUserHoldCoinDialog(mainViewModel = mainViewModel, dialogState = editHoldCoinDialogState)
    }
    val secondExceptionHandler = remember {
        CoroutineExceptionHandler { _, _ ->
            context.showToast(context.getString(R.string.secondError))
        }
    }
    val firstExceptionHandler = remember {
        CoroutineExceptionHandler { _, _ ->
            CoroutineScope(Dispatchers.Main).launch(secondExceptionHandler) {
                val snackBarResult =
                    scaffoldState.snackbarHostState.showSnackbar(
                        context.getString(R.string.firstError),
                        context.getString(R.string.retry)
                    )
                when (snackBarResult) {
                    SnackbarResult.Dismissed -> {}
                    SnackbarResult.ActionPerformed -> {
                        mainViewModel.getUserHoldCoins()
                    }
                }
            }
        }
    }

    TwoButtonCommonDialog(
        dialogState = mainViewModel.adDialogState,
        title = stringResource(id = R.string.chargeMoney),
        content = stringResource(id = R.string.adDialogContent),
        leftButtonText = stringResource(id = R.string.commonCancel),
        rightButtonText = stringResource(id = R.string.commonAccept),
        leftButtonAction = { mainViewModel.adDialogState.value = false },
        rightButtonAction = {
            mainViewModel.updateAdLiveData()
            mainViewModel.adDialogState.value = false
        })
    CommonLoadingDialog(mainViewModel.adLoadingDialogState, stringResource(id = R.string.loadAd))

    if (mainViewModel.removeCoinCount.value == 1) {
        context.showToast(stringResource(id = R.string.notRemovedCoin))
    } else if (mainViewModel.removeCoinCount.value == -1) {
        context.showToast(stringResource(id = R.string.NETWORK_ERROR))
    } else if (mainViewModel.removeCoinCount.value > 1) {
        context.showToast(
            stringResource(
                id = R.string.removedCoin,
                mainViewModel.removeCoinCount.value - 1
            )
        )
    }

    AddLifecycleEvent(onPauseAction = {
        mainViewModel.isPortfolioSocketRunning = false
        UpBitPortfolioWebSocket.getListener().setPortfolioMessageListener(null)
        UpBitPortfolioWebSocket.onPause()
    },
        onResumeAction = {
            if (NetworkMonitorUtil.currentNetworkState == INTERNET_CONNECTION) {
                CoroutineScope(Dispatchers.Main).launch(firstExceptionHandler) {
                    mainViewModel.getUserHoldCoins()
                }
            } else {
                context.showToast(context.getString(R.string.NO_INTERNET_CONNECTION))
            }
        }
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth(),
                backgroundColor = colorResource(id = R.color.design_default_color_background)
            ) {
                Text(
                    text = stringResource(id = R.string.investmentDetail),
                    modifier = Modifier
                        .padding(5.dp, 0.dp, 0.dp, 0.dp)
                        .weight(1f, true)
                        .fillMaxHeight()
                        .wrapContentHeight(),
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                IconButton(onClick = { editHoldCoinDialogState.value = true }) {
                    Icon(
                        painterResource(id = R.drawable.img_eraser),
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
            if (mainViewModel.portfolioLoadingComplete.value) {
                UserHoldCoinLazyColumn(mainViewModel, startForActivityResult, dialogState)
            }
        }
    }
}

@Composable
fun PortfolioMain(
    mainViewModel: MainViewModel = viewModel(),
    portfolioOrderState: MutableState<Int>,
    orderByNameTextInfo: List<Any>,
    orderByRateTextInfo: List<Any>,
    pieChartState: MutableState<Boolean>,
) {
    mainViewModel.getUserSeedMoney()
    val totalValuedAssets = Calculator.getDecimalFormat()
        .format(round(mainViewModel.totalValuedAssets.value).toLong())
    val totalPurchaseValue =
        Calculator.getDecimalFormat().format(round(mainViewModel.totalPurchase.value).toLong())
    val userSeedMoney = Calculator.getDecimalFormat().format(mainViewModel.userSeedMoney.value)
    val totalHoldings = Calculator.getDecimalFormat()
        .format(round(mainViewModel.userSeedMoney.value + mainViewModel.totalValuedAssets.value).toLong())
    val valuationGainOrLoss = Calculator.getDecimalFormat()
        .format(round(mainViewModel.totalValuedAssets.value - mainViewModel.totalPurchase.value).toLong())
    val aReturn = if (mainViewModel.totalValuedAssets.value == 0.0) {
        "0"
    } else {
        ((mainViewModel.totalValuedAssets.value - mainViewModel.totalPurchase.value) / mainViewModel.totalPurchase.value * 100).secondDecimal()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text = stringResource(id = R.string.holdings),
                modifier = Modifier
                    .weight(1f, true)
                    .padding(8.dp, 20.dp, 0.dp, 20.dp)
                    .wrapContentHeight(),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Card(
                modifier = Modifier
                    .padding(0.dp, 12.dp, 8.dp, 12.dp)
                    .wrapContentWidth(),
                elevation = 4.dp,
            ) {
                Text(
                    text = stringResource(id = R.string.chargeMoney),
                    modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.CenterVertically)
                        .padding(8.dp)
                        .clickable {
                            mainViewModel.adDialogState.value = true
                        },
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .drawUnderLine(lineColor = Color.DarkGray, strokeWidth = 2f)
        ) {
            PortfolioMainItem(
                text1 = stringResource(id = R.string.userSeedMoney),
                text2 = userSeedMoney,
                stringResource(id = R.string.totalPurchaseValue),
                totalPurchaseValue,
                stringResource(id = R.string.totalValuedAssets),
                totalValuedAssets,
                round(mainViewModel.totalValuedAssets.value - mainViewModel.totalPurchase.value).toLong()
            )
            PortfolioMainItem(
                text1 = stringResource(id = R.string.totalHoldings),
                text2 = totalHoldings,
                stringResource(id = R.string.valuationGainOrLoss),
                valuationGainOrLoss,
                stringResource(id = R.string.aReturn),
                aReturn.plus("%"),
                round(mainViewModel.totalValuedAssets.value - mainViewModel.totalPurchase.value).toLong()
            )
        }
        PortfolioPieChart(
            pieChartState,
            mainViewModel.userSeedMoney,
            mainViewModel.userHoldCoinList
        )
        PortfolioMainSortButtons(
            orderByRateTextInfo,
            orderByNameTextInfo,
            mainViewModel,
            portfolioOrderState
        )
    }
}

@Composable
fun PortfolioMainSortButtons(
    orderByRateTextInfo: List<Any>,
    orderByNameTextInfo: List<Any>,
    mainViewModel: MainViewModel,
    portfolioOrderState: MutableState<Int>,
    ) {
    Row(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .drawUnderLine(lineColor = Color.DarkGray, strokeWidth = 2f)
    ) {
        Text(
            text = orderByNameTextInfo[0] as String,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(color = orderByNameTextInfo[2] as Color)
                .clickable {
                    if (mainViewModel.isPortfolioSocketRunning) {
                        if (portfolioOrderState.value != 0 && portfolioOrderState.value != 1) {
                            portfolioOrderState.value = 0
                        } else if (portfolioOrderState.value == 0) {
                            portfolioOrderState.value = 1
                        } else {
                            portfolioOrderState.value = -1
                        }
                        mainViewModel.sortUserHoldCoin(portfolioOrderState.value)
                    }
                }
                .padding(0.dp, 8.dp),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            style = TextStyle(color = orderByNameTextInfo[1] as Color)
        )
        Text(
            text = orderByRateTextInfo[0] as String,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(color = orderByRateTextInfo[2] as Color)
                .clickable {
                    if (mainViewModel.isPortfolioSocketRunning) {
                        if (portfolioOrderState.value != 2 && portfolioOrderState.value != 3) {
                            portfolioOrderState.value = 2
                        } else if (portfolioOrderState.value == 2) {
                            portfolioOrderState.value = 3
                        } else {
                            portfolioOrderState.value = -1
                        }
                        mainViewModel.sortUserHoldCoin(portfolioOrderState.value)
                    }
                }
                .padding(0.dp, 8.dp),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            style = TextStyle(color = orderByRateTextInfo[1] as Color)
        )
    }
}

@Composable
fun PortfolioPieChart(
    pieChartState: MutableState<Boolean>,
    userSeedMoney: MutableState<Long>,
    userHoldCoinList: List<MyCoin?>,
) {
    val imageVector = remember {
        mutableStateOf(Icons.Filled.KeyboardArrowDown)
    }
    if (pieChartState.value) {
        imageVector.value = Icons.Filled.KeyboardArrowUp
    } else {
        imageVector.value = Icons.Filled.KeyboardArrowDown
    }

    Column(modifier = Modifier.wrapContentSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .drawUnderLine(lineColor = Color.Gray, strokeWidth = 2f)
                .clickable { pieChartState.value = !pieChartState.value }
        ) {
            Text(
                text = stringResource(id = R.string.holdingAssetsPortfolio), modifier = Modifier
                    .padding(8.dp, 8.dp, 0.dp, 8.dp)
                    .weight(1f, true)
                    .align(Alignment.CenterVertically), style = TextStyle(fontSize = 16.sp)
            )
            Icon(
                imageVector = imageVector.value,
                contentDescription = null,
                tint = colorResource(
                    id = R.color.C0F0F5C
                ),
                modifier = Modifier
                    .padding(8.dp, 8.dp)
                    .fillMaxHeight()
            )

        }
        if (pieChartState.value) {
            HoldCoinPieChart(
                userSeedMoney = userSeedMoney.value,
                userHoldCoinList = userHoldCoinList
            )
        }
    }
}

@Composable
fun RowScope.PortfolioMainItem(
    text1: String,
    text2: String,
    text3: String,
    text4: String,
    text5: String,
    text6: String,
    colorStandard: Long,
) {
    val textColor = getReturnTextColor(colorStandard, text5)
    Column(
        modifier = Modifier
            .padding()
            .wrapContentHeight()
            .weight(2f, true)
    ) {
        Text(
            text = text1,
            modifier = Modifier
                .padding(8.dp, 0.dp, 0.dp, 0.dp)
                .wrapContentHeight()
                .fillMaxWidth(),
            style = TextStyle(
                color = Color.Black,
                fontSize = 18.sp,
            )
        )

        PortfolioAutoSizeText(
            text = text2,
            modifier = Modifier
                .padding(8.dp, 5.dp, 8.dp, 0.dp)
                .wrapContentHeight()
                .fillMaxWidth(),
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 15.dp, 0.dp, 0.dp)
                .wrapContentHeight()
        ) {
            Text(
                text = text3,
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .wrapContentHeight()
                    .wrapContentWidth(),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 14.sp,
                )
            )
            PortfolioAutoSizeText(
                text = text4,
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .weight(1f, true)
                    .wrapContentHeight(),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    textAlign = TextAlign.End
                ),
                color = textColor
            )
        }
        Row(
            modifier = Modifier
                .padding(0.dp, 8.dp, 0.dp, 25.dp)
                .wrapContentHeight()
        ) {
            Text(
                text = text5,
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .wrapContentHeight()
                    .wrapContentWidth(),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 14.sp,
                )
            )
            PortfolioAutoSizeText(
                text = text6,
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .weight(1f, true)
                    .wrapContentHeight(),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    textAlign = TextAlign.End
                ),
                color = textColor
            )
        }
    }
}

@Composable
fun UserHoldCoinLazyColumn(
    mainViewModel: MainViewModel,
    startForActivityResult: ActivityResultLauncher<Intent>,
    dialogState: MutableState<Boolean>,
) {
    val portfolioOrderState = remember {
        mutableStateOf(-1)
    }
    val selectedCoinKoreanName = remember {
        mutableStateOf("")
    }
    val pieChartState = remember {
        mutableStateOf(false)
    }
    val orderByNameTextInfo = getTextColors(buttonNum = 1, textState = portfolioOrderState.value)
    val orderByRateTextInfo = getTextColors(buttonNum = 2, textState = portfolioOrderState.value)
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        val userHoldCoinDtoList = mainViewModel.userHoldCoinDtoList
        item {
            PortfolioMain(
                mainViewModel,
                portfolioOrderState,
                orderByNameTextInfo,
                orderByRateTextInfo,
                pieChartState
            )
        }
        itemsIndexed(items = userHoldCoinDtoList) { _, item ->
            if (userHoldCoinDtoList.isNotEmpty()) {
                val openingPrice = item.openingPrice
                val warning = item.warning
                val isFavorite = item.isFavorite
                val koreanName = item.myCoinsKoreanName
                val symbol = item.myCoinsSymbol
                val currentPrice = item.currentPrice
                val purchaseAmount =
                    round(item.myCoinsQuantity * item.myCoinsBuyingAverage).toLong()
                val evaluationAmount = round(item.myCoinsQuantity * currentPrice).toLong()
                val purchaseAverage = item.myCoinsBuyingAverage
                val valuationGainOrLoss = evaluationAmount - purchaseAmount
                val coinQuantity = Calculator.getDecimalDecimalFormat().format(item.myCoinsQuantity)
                val aReturn =
                    if (((currentPrice - item.myCoinsBuyingAverage) / item.myCoinsBuyingAverage * 100).isNaN()) {
                        0
                    } else {
                        ((currentPrice - item.myCoinsBuyingAverage) / item.myCoinsBuyingAverage * 100).secondDecimal()
                    }
                val color = if (valuationGainOrLoss > 0) {
                    Color.Red
                } else if (valuationGainOrLoss < 0) {
                    Color.Blue
                } else {
                    Color.Black
                }

                UserHoldCoinLazyColumnItem(
                    koreanName,
                    symbol,
                    Calculator.valuationGainOrLossDecimal(valuationGainOrLoss.toDouble()),
                    aReturn.toString().plus("%"),
                    coinQuantity,
                    Calculator.tradePriceCalculatorForChart(purchaseAverage),
                    Calculator.tradePriceCalculatorForChart(purchaseAmount.toDouble()),
                    Calculator.getDecimalFormat().format(evaluationAmount),
                    color,
                    openingPrice,
                    warning,
                    isFavorite,
                    currentPrice,
                    startForActivityResult,
                    selectedCoinKoreanName,
                    dialogState
                )
            }
        }
    }
}

@Composable
fun UserHoldCoinLazyColumnItem(
    coinKoreanName: String,
    symbol: String,
    valuationGainOrLoss: String,
    aReturn: String,
    coinQuantity: String,
    purchaseAverage: String,
    purchaseAmount: String,
    evaluationAmount: String,
    color: Color,
    openingPrice: Double,
    warning: String,
    isFavorite: Int?,
    currentPrice: Double,
    startForActivityResult: ActivityResultLauncher<Intent>,
    selectedCoinKoreanName: MutableState<String>,
    dialogState: MutableState<Boolean>,
) {
    if (dialogState.value && coinKoreanName == selectedCoinKoreanName.value) {
        UserHoldCoinLazyColumnItemDialog(
            dialogState,
            coinKoreanName,
            currentPrice,
            symbol,
            openingPrice,
            isFavorite,
            warning,
            startForActivityResult
        )
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .drawUnderLine(lineColor = Color.Gray, strokeWidth = 2f)
        .clickable {
            selectedCoinKoreanName.value = coinKoreanName
            dialogState.value = true
        }
    ) {
        Row(
            modifier = Modifier
                .padding(0.dp, 8.dp, 0.dp, 0.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text(
                    text = coinKoreanName,
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 0.dp, 1.dp)
                        .fillMaxWidth(),
                    style = TextStyle(
                        color = colorResource(id = R.color.C0F0F5C),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = symbol,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(
                        color = colorResource(id = R.color.C0F0F5C),
                        fontSize = 17.sp
                    ),
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.valuationGainOrLoss),
                        modifier = Modifier.wrapContentWidth()
                    )
                    PortfolioAutoSizeText(
                        text = valuationGainOrLoss,
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 0.dp, 4.dp)
                            .weight(1f, true),
                        textStyle = TextStyle(textAlign = TextAlign.End, fontSize = 15.sp),
                        color = color
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.aReturn),
                        modifier = Modifier.wrapContentWidth()
                    )
                    PortfolioAutoSizeText(
                        text = aReturn,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, true),
                        textStyle = TextStyle(textAlign = TextAlign.End, fontSize = 15.sp),
                        color = color
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            UserHoldCoinLazyColumnItemContent(
                coinQuantity,
                symbol,
                stringResource(id = R.string.holdingQuantity)
            )
            UserHoldCoinLazyColumnItemContent(
                purchaseAverage,
                "KRW",
                stringResource(id = R.string.purchaseAverage)
            )
        }
        Row(
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 8.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            UserHoldCoinLazyColumnItemContent(
                evaluationAmount,
                "KRW",
                stringResource(id = R.string.evaluationAmount)
            )
            UserHoldCoinLazyColumnItemContent(
                purchaseAmount,
                "KRW",
                stringResource(id = R.string.purchaseAmount)
            )
        }
    }
}

@Composable
fun RowScope.UserHoldCoinLazyColumnItemContent(
    text1: String,
    text2: String,
    text3: String,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 2.dp)
        ) {
            AutoSizeText(
                modifier = Modifier.weight(1f, true),
                text = text1,
                textStyle = TextStyle(textAlign = TextAlign.End, fontSize = 15.sp)
            )
            Text(
                text = "  ".plus(text2),
                modifier = Modifier.wrapContentWidth(),
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = text3,
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(color = Color.Gray),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun getTextColors(buttonNum: Int, textState: Int): List<Any> {
    return when (buttonNum) {
        1 -> {
            when (textState) {
                0 -> {
                    listOf(
                        stringResource(id = R.string.nameDown),
                        Color.White,
                        colorResource(id = R.color.C0F0F5C)
                    )
                }
                1 -> {
                    listOf(
                        stringResource(id = R.string.nameUp),
                        Color.White,
                        colorResource(id = R.color.C0F0F5C)
                    )
                }
                else -> {
                    listOf(
                        stringResource(id = R.string.nameUpDown),
                        Color.Black,
                        colorResource(id = R.color.design_default_color_background)
                    )
                }
            }
        }
        2 -> {
            when (textState) {
                2 -> {
                    listOf(
                        stringResource(id = R.string.aReturnDown),
                        Color.White,
                        colorResource(id = R.color.C0F0F5C)
                    )
                }
                3 -> {
                    listOf(
                        stringResource(id = R.string.aReturnUp),
                        Color.White,
                        colorResource(id = R.color.C0F0F5C)
                    )
                }
                else -> {
                    listOf(
                        stringResource(id = R.string.aReturnUpDown),
                        Color.Black,
                        colorResource(id = R.color.design_default_color_background)
                    )
                }
            }
        }
        else -> {
            listOf("수익률아래", Color.White, Color.Black)
        }
    }
}

@Composable
fun UserHoldCoinLazyColumnItemDialog(
    dialogState: MutableState<Boolean>,
    koreanName: String,
    currentPrice: Double,
    symbol: String,
    openingPrice: Double,
    isFavorite: Int?,
    warning: String,
    startForActivityResult: ActivityResultLauncher<Intent>,
) {
    val context = LocalContext.current
    val textColor = if (openingPrice < currentPrice) {
        Color.Red
    } else if (openingPrice > currentPrice) {
        Color.Blue
    } else {
        Color.Black
    }
    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier
                .padding(40.dp, 0.dp)
                .wrapContentSize()
        ) {
            Column(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {
                Text(
                    text = koreanName.plus(stringResource(id = R.string.order)),
                    modifier = Modifier
                        .padding(0.dp, 20.dp)
                        .fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Row {
                    Text(
                        text = stringResource(id = R.string.currentPrice),
                        modifier = Modifier
                            .padding(20.dp, 20.dp, 0.dp, 20.dp)
                            .wrapContentWidth(),
                        style = TextStyle(fontSize = 18.sp)
                    )
                    Text(
                        text = Calculator.tradePriceCalculatorForChart(currentPrice),
                        modifier = Modifier
                            .padding(0.dp, 20.dp)
                            .weight(1f, true),
                        style = TextStyle(
                            color = textColor,
                            fontSize = 18.sp,
                            textAlign = TextAlign.End
                        )
                    )
                    Text(
                        text = "  KRW",
                        modifier = Modifier
                            .padding(0.dp, 20.dp, 20.dp, 20.dp)
                            .wrapContentWidth(),
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Row {
                    Text(
                        text = stringResource(id = R.string.netChange),
                        modifier = Modifier
                            .padding(20.dp, 20.dp, 0.dp, 20.dp)
                            .wrapContentWidth(),
                        style = TextStyle(fontSize = 18.sp)
                    )
                    Text(
                        text =
                        Calculator.orderBookRateCalculator(openingPrice, currentPrice)
                            .secondDecimal().plus("%"),
                        modifier = Modifier
                            .padding(0.dp, 20.dp, 20.dp, 40.dp)
                            .weight(1f, true),
                        style = TextStyle(
                            color = textColor,
                            fontSize = 18.sp,
                            textAlign = TextAlign.End
                        )
                    )
                }
                Divider(modifier = Modifier.fillMaxWidth(), Color.LightGray, 0.5.dp)
                Row {
                    Text(
                        text = stringResource(id = R.string.commonCancel), modifier = Modifier
                            .weight(1f)
                            .clickable {
                                dialogState.value = false
                            }
                            .padding(0.dp, 10.dp),
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        text = "", modifier = Modifier
                            .width(0.5.dp)
                            .border(0.5.dp, Color.LightGray)
                            .padding(0.dp, 10.dp), fontSize = 18.sp
                    )
                    Text(text = stringResource(id = R.string.commonMove),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val intent = Intent(context, CoinDetailActivity::class.java)
                                intent.putExtra("coinKoreanName", koreanName)
                                intent.putExtra("coinSymbol", symbol)
                                intent.putExtra("openingPrice", openingPrice)
                                intent.putExtra("isFavorite", isFavorite != null)
                                intent.putExtra("warning", warning)
                                startForActivityResult.launch(intent)
                                (context as MainActivity).overridePendingTransition(
                                    R.anim.lazy_column_item_slide_left,
                                    R.anim.none
                                )
                                dialogState.value = false
                            }
                            .padding(0.dp, 10.dp),
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun EditUserHoldCoinDialog(mainViewModel: MainViewModel, dialogState: MutableState<Boolean>) {
    Dialog(onDismissRequest = { dialogState.value = false }) {
        Card(
            modifier = Modifier
                .padding(20.dp, 0.dp)
                .wrapContentSize()
        ) {
            Column(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.clearCoin),
                    modifier = Modifier
                        .padding(0.dp, 20.dp)
                        .fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = stringResource(id = R.string.cleanUpDialogContent),
                    modifier = Modifier
                        .padding(10.dp, 10.dp, 10.dp, 20.dp)
                        .fillMaxWidth(),
                    style = TextStyle(fontSize = 18.sp)
                )
                Divider(modifier = Modifier.fillMaxWidth(), Color.LightGray, 0.5.dp)
                Row {
                    Text(
                        text = stringResource(id = R.string.commonCancel), modifier = Modifier
                            .weight(1f)
                            .clickable {
                                dialogState.value = false
                            }
                            .padding(0.dp, 10.dp),
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        text = "", modifier = Modifier
                            .width(0.5.dp)
                            .border(0.5.dp, Color.LightGray)
                            .padding(0.dp, 10.dp), fontSize = 18.sp
                    )
                    Text(text = stringResource(id = R.string.commonAccept),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                mainViewModel.editUserHoldCoin()
                                dialogState.value = false
                            }
                            .padding(0.dp, 10.dp),
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun HoldCoinPieChart(userSeedMoney: Long, userHoldCoinList: List<MyCoin?>) {
    AndroidView(
        factory = {
            UserHoldCoinPieChart(
                it, userSeedMoney, userHoldCoinList
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .drawUnderLine(lineColor = Color.DarkGray, strokeWidth = 2f)
    )
}

@Composable
fun getReturnTextColor(colorStandard: Long, text5: String): Color {
    return if (text5 == stringResource(id = R.string.aReturn)) {
        when {
            colorStandard < 0 -> {
                Color.Blue
            }
            colorStandard > 0 -> {
                Color.Red
            }
            else -> {
                Color.Black
            }
        }
    } else {
        Color.Black
    }
}