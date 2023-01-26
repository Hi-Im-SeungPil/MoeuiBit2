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
import org.jeonfeel.moeuibit2.ui.viewmodels.PortfolioViewModel
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
    portfolioViewModel: PortfolioViewModel = viewModel(),
    startForActivityResult: ActivityResultLauncher<Intent>
) {
    val context = LocalContext.current
    EditUserHoldCoinDialog(
        dialogState = portfolioViewModel.state.editHoldCoinDialogState,
        editUserHoldCoin = portfolioViewModel::editUserHoldCoin
    )
    TwoButtonCommonDialog(
        dialogState = portfolioViewModel.state.adDialogState,
        title = stringResource(id = R.string.chargeMoney),
        content = stringResource(id = R.string.adDialogContent),
        leftButtonText = stringResource(id = R.string.commonCancel),
        rightButtonText = stringResource(id = R.string.commonAccept),
        leftButtonAction = { portfolioViewModel.state.adDialogState.value = false },
        rightButtonAction = {
            portfolioViewModel.updateAdLiveData()
            portfolioViewModel.state.adDialogState.value = false
        })
    CommonLoadingDialog(
        portfolioViewModel.state.adLoadingDialogState,
        stringResource(id = R.string.loadAd)
    )

    AddLifecycleEvent(
        onPauseAction = {
            portfolioViewModel.isPortfolioSocketRunning = false
            UpBitPortfolioWebSocket.getListener().setPortfolioMessageListener(null)
            UpBitPortfolioWebSocket.onPause()
        },
        onResumeAction = {
            if (NetworkMonitorUtil.currentNetworkState == INTERNET_CONNECTION) {
                portfolioViewModel.getUserSeedMoney()
                portfolioViewModel.getUserHoldCoins()
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
                        fontSize = DpToSp(25.dp),
                        fontWeight = FontWeight.Bold
                    )
                )
                IconButton(onClick = {
                    portfolioViewModel.state.editHoldCoinDialogState.value = true
                }) {
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
            UserHoldCoinLazyColumn(
                startForActivityResult = startForActivityResult,
                columnItemDialogState = portfolioViewModel.state.dialogState,
                portfolioOrderState = portfolioViewModel.state.portfolioOrderState,
                totalValuedAssets = portfolioViewModel.state.totalValuedAssets,
                totalPurchase = portfolioViewModel.state.totalPurchase,
                userSeedMoney = portfolioViewModel.state.userSeedMoney,
                adDialogState = portfolioViewModel.state.adDialogState,
                pieChartState = portfolioViewModel.state.pieChartState,
                userHoldCoinList = portfolioViewModel.userHoldCoinList,
                earnReward = portfolioViewModel::earnReward,
                userHoldCoinDTOList = portfolioViewModel.state.userHoldCoinDtoList,
                selectedCoinKoreanName = portfolioViewModel.state.selectedCoinKoreanName,
                btcTradePrice = portfolioViewModel.state.btcTradePrice
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