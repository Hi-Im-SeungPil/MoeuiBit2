package org.jeonfeel.moeuibit2.ui.mainactivity.portfolio

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.main.viewmodel.MainViewModel
import org.jeonfeel.moeuibit2.constant.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitPortfolioWebSocket
import org.jeonfeel.moeuibit2.ui.coindetail.chart.UserHoldCoinPieChart
import org.jeonfeel.moeuibit2.ui.common.CommonLoadingDialog
import org.jeonfeel.moeuibit2.ui.common.TwoButtonCommonDialog
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
    val totalPurchaseValue = Calculator.getDecimalFormat().format(round(mainViewModel.totalPurchase.value).toLong())
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