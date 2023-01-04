package org.jeonfeel.moeuibit2.ui.main.portfolio

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ScaffoldState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.MoeuiBitDataStore.isKor
import org.jeonfeel.moeuibit2.MoeuiBitDataStore.usdPrice
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.viewmodels.MainViewModel
import org.jeonfeel.moeuibit2.constants.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitPortfolioWebSocket
import org.jeonfeel.moeuibit2.ui.coindetail.chart.UserHoldCoinPieChart
import org.jeonfeel.moeuibit2.ui.common.CommonLoadingDialog
import org.jeonfeel.moeuibit2.ui.common.TwoButtonCommonDialog
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.ui.theme.decrease_color
import org.jeonfeel.moeuibit2.ui.theme.increase_color
import org.jeonfeel.moeuibit2.ui.custom.DpToSp
import org.jeonfeel.moeuibit2.ui.custom.drawUnderLine
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.Utils.removeComma
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator.krwToUsd
import org.jeonfeel.moeuibit2.utils.secondDecimal
import org.jeonfeel.moeuibit2.utils.showToast
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
//                            mainViewModel.earnReward()
                        },
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                )
            }
//            Card(
//                modifier = Modifier
//                    .padding(0.dp, 12.dp, 8.dp, 12.dp)
//                    .wrapContentWidth(),
//                elevation = 4.dp,
//            ) {
//                Text(
//                    text = "테스트 10조 충전 버튼",
//                    modifier = Modifier
//                        .wrapContentWidth()
//                        .align(Alignment.CenterVertically)
//                        .padding(8.dp)
//                        .clickable {
//                            mainViewModel.test()
//                        },
//                    style = TextStyle(
//                        color = Color.Black,
//                        fontSize = 18.sp
//                    )
//                )
//            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .drawUnderLine(lineColor = Color.DarkGray, strokeWidth = 2f)
        ) {
            if (isKor) {
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
            } else {
                PortfolioMainItemForEn(
                    text1 = stringResource(id = R.string.userSeedMoney),
                    text2 = userSeedMoney,
                    text3 = stringResource(id = R.string.totalPurchaseValue),
                    text4 = totalPurchaseValue,
                    text5 = stringResource(id = R.string.totalValuedAssets),
                    text6 = totalValuedAssets,
                    colorStandard = round(mainViewModel.totalValuedAssets.value - mainViewModel.totalPurchase.value).toLong()
                )
                PortfolioMainItemForEn(
                    text1 = stringResource(id = R.string.totalHoldings),
                    text2 = totalHoldings,
                    text3 = stringResource(id = R.string.valuationGainOrLoss),
                    text4 = valuationGainOrLoss,
                    text5 = stringResource(id = R.string.aReturn),
                    text6 = aReturn.plus("%"),
                    colorStandard = round(mainViewModel.totalValuedAssets.value - mainViewModel.totalPurchase.value).toLong()
                )
            }
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
        AutoSizeText(
            text = text2,
            modifier = Modifier
                .padding(8.dp, 5.dp, 8.dp, 0.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            textStyle = TextStyle(
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
            AutoSizeText(
                text = text4,
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .weight(1f, true)
                    .wrapContentHeight(),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    textAlign = TextAlign.End,
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
            AutoSizeText(
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
fun RowScope.PortfolioMainItemForEn(
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
        AutoSizeText(
            text = text2,
            modifier = Modifier
                .padding(8.dp, 5.dp, 8.dp, 0.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            textStyle = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        )
        AutoSizeText(
            text = "= \$ ${krwToUsd(removeComma(text2).toDouble(), usdPrice)}",
            modifier = Modifier
                .padding(8.dp, 5.dp, 8.dp, 0.dp)
                .wrapContentHeight(),
            textStyle = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color.Gray
        )
        Column(
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
                    .fillMaxWidth(),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = DpToSp(dp = 17.dp),
                    textAlign = TextAlign.Center
                )
            )
            AutoSizeText(
                text = text4,
                modifier = Modifier
                    .padding(8.dp, 4.dp, 8.dp, 0.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                textStyle = TextStyle(
                    fontSize = DpToSp(dp = 15.dp),
                    textAlign = TextAlign.End,
                ),
                color = textColor
            )
            AutoSizeText(
                text = "= \$ ${krwToUsd(removeComma(text4).toDouble(), usdPrice)}",
                modifier = Modifier
                    .padding(8.dp, 4.dp, 8.dp, 0.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                textStyle = TextStyle(
                    fontSize = DpToSp(dp = 13.dp),
                    textAlign = TextAlign.End
                ),
                color = if(text3 == stringResource(id = R.string.totalPurchaseValue)) Color.Gray else textColor
            )
        }
        Column(
            modifier = Modifier
                .padding(0.dp, 8.dp, 0.dp, 25.dp)
                .wrapContentHeight()
        ) {
            Text(
                text = text5,
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .wrapContentHeight()
                    .fillMaxWidth(),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = DpToSp(dp = 17.dp),
                    textAlign = TextAlign.Center
                )
            )
            AutoSizeText(
                text = text6,
                modifier = Modifier
                    .padding(8.dp, 4.dp, 8.dp, 0.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                textStyle = TextStyle(
                    fontSize = DpToSp(dp = 15.dp),
                    textAlign = TextAlign.End
                ),
                color = textColor
            )
            if (text5 != stringResource(id = R.string.aReturn)) {
                AutoSizeText(
                    text = "= \$ ${krwToUsd(removeComma(text6).toDouble(), usdPrice)}",
                    modifier = Modifier
                        .padding(8.dp, 4.dp, 8.dp, 0.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    textStyle = TextStyle(
                        fontSize = DpToSp(dp = 13.dp),
                        textAlign = TextAlign.End
                    ),
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun getTextColors(buttonNum: Int, textState: Int): List<Any> {
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
                decrease_color
            }
            colorStandard > 0 -> {
                increase_color
            }
            else -> {
                Color.Black
            }
        }
    } else {
        Color.Black
    }
}