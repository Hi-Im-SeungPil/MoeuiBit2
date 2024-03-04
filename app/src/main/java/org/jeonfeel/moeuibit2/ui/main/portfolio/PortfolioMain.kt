package org.jeonfeel.moeuibit2.ui.main.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.view.UserHoldCoinPieChart
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.drawUnderLine
import org.jeonfeel.moeuibit2.ui.theme.chargingKrwBackgroundColor
import org.jeonfeel.moeuibit2.ui.theme.portfolioSortButtonSelectedBackgroundColor
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator
import org.jeonfeel.moeuibit2.utils.secondDecimal
import kotlin.math.round

enum class PortfolioSortButton {
    BUTTON_NAME, BUTTON_RATE
}

@Composable
fun PortfolioMain(
    portfolioOrderState: MutableState<Int>,
    totalValuedAssets: MutableState<Double>,
    totalPurchase: MutableState<Double>,
    userSeedMoney: MutableState<Long>,
    orderByNameTextInfo: List<Any>,
    orderByRateTextInfo: List<Any>,
    adDialogState: MutableState<Boolean>,
    pieChartState: MutableState<Boolean>,
    userHoldCoinList: List<MyCoin?>,
    sortUserHoldCoin: (orderState: Int) -> Unit,
    isPortfolioSocketRunning: MutableState<Boolean>
) {
    val calcTotalValuedAssets = Calculator.getDecimalFormat()
        .format(round(totalValuedAssets.value).toLong())
    val totalPurchaseValue =
        Calculator.getDecimalFormat().format(round(totalPurchase.value).toLong())
    val calcUserSeedMoney = Calculator.getDecimalFormat().format(userSeedMoney.value)
    val totalHoldings = Calculator.getDecimalFormat()
        .format(round(userSeedMoney.value + totalValuedAssets.value).toLong())
    val valuationGainOrLoss = Calculator.getDecimalFormat()
        .format(round(totalValuedAssets.value - totalPurchase.value).toLong())
    val aReturn = if (totalValuedAssets.value == 0.0) {
        "0"
    } else {
        ((totalValuedAssets.value - totalPurchase.value) / totalPurchase.value * 100).secondDecimal()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
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
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = DpToSp(dp = 22.dp)
                )
            )
            Card(
                modifier = Modifier
                    .background(color = Color.Transparent)
                    .padding(0.dp, 12.dp, 8.dp, 12.dp)
                    .wrapContentWidth(),
                elevation = 4.dp,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.chargeMoney),
                    modifier = Modifier
                        .background(color = chargingKrwBackgroundColor())
                        .padding(13.dp)
                        .wrapContentWidth()
                        .align(Alignment.CenterVertically)
                        .clickable {
                            adDialogState.value = true
                        },
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = DpToSp(dp = 18.dp)
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
            val colorStandard = round(totalValuedAssets.value - totalPurchase.value).toLong()
            if (MoeuiBitDataStore.isKor) {
                PortfolioMainItem(
                    text1 = stringResource(id = R.string.userSeedMoney),
                    text2 = calcUserSeedMoney,
                    text3 = stringResource(id = R.string.totalPurchaseValue),
                    text4 = totalPurchaseValue,
                    text5 = stringResource(id = R.string.totalValuedAssets),
                    text6 = calcTotalValuedAssets,
                    colorStandard = colorStandard
                )
                PortfolioMainItem(
                    text1 = stringResource(id = R.string.totalHoldings),
                    text2 = totalHoldings,
                    text3 = stringResource(id = R.string.valuationGainOrLoss),
                    text4 = valuationGainOrLoss,
                    text5 = stringResource(id = R.string.aReturn),
                    text6 = aReturn.plus("%"),
                    colorStandard = colorStandard
                )
            } else {
                PortfolioMainItemForEn(
                    text1 = stringResource(id = R.string.userSeedMoney),
                    text2 = calcUserSeedMoney,
                    text3 = stringResource(id = R.string.totalPurchaseValue),
                    text4 = totalPurchaseValue,
                    text5 = stringResource(id = R.string.totalValuedAssets),
                    text6 = calcTotalValuedAssets,
                    colorStandard = colorStandard
                )
                PortfolioMainItemForEn(
                    text1 = stringResource(id = R.string.totalHoldings),
                    text2 = totalHoldings,
                    text3 = stringResource(id = R.string.valuationGainOrLoss),
                    text4 = valuationGainOrLoss,
                    text5 = stringResource(id = R.string.aReturn),
                    text6 = aReturn.plus("%"),
                    colorStandard = colorStandard
                )
            }
        }
        PortfolioPieChart(
            pieChartState = pieChartState,
            userSeedMoney = userSeedMoney,
            userHoldCoinList = userHoldCoinList
        )
        PortfolioMainSortButtons(
            orderByNameTextInfo = orderByNameTextInfo,
            orderByRateTextInfo = orderByRateTextInfo,
            isPortfolioSocketRunning = isPortfolioSocketRunning,
            portfolioOrderState = portfolioOrderState,
            sortUserHoldCoin = sortUserHoldCoin
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
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = DpToSp(18.dp),
            )
        )
        AutoSizeText(
            text = text2,
            modifier = Modifier
                .padding(8.dp, 5.dp, 8.dp, 0.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            textStyle = TextStyle(
                fontSize = DpToSp(22.dp),
                fontWeight = FontWeight.Bold,
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
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = DpToSp(14.dp),
                )
            )
            AutoSizeText(
                text = text4,
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .weight(1f, true)
                    .wrapContentHeight(),
                textStyle = TextStyle(
                    fontSize = DpToSp(14.dp),
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
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = DpToSp(14.dp),
                )
            )
            AutoSizeText(
                text = text6,
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .weight(1f, true)
                    .wrapContentHeight(),
                textStyle = TextStyle(
                    fontSize = DpToSp(14.dp),
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
                fontSize = DpToSp(18.dp),
            )
        )
        AutoSizeText(
            text = text2,
            modifier = Modifier
                .padding(8.dp, 5.dp, 8.dp, 0.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            textStyle = TextStyle(
                fontSize = DpToSp(22.dp),
                fontWeight = FontWeight.Bold
            )
        )
        AutoSizeText(
            text = "= \$ ${
                CurrentCalculator.krwToUsd(
                    Utils.removeComma(text2).toDouble(),
                    MoeuiBitDataStore.usdPrice
                )
            }",
            modifier = Modifier
                .padding(8.dp, 5.dp, 8.dp, 0.dp)
                .wrapContentHeight(),
            textStyle = TextStyle(
                fontSize = DpToSp(15.dp),
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
                text = "= \$ ${
                    CurrentCalculator.krwToUsd(
                        Utils.removeComma(text4).toDouble(),
                        MoeuiBitDataStore.usdPrice
                    )
                }",
                modifier = Modifier
                    .padding(8.dp, 4.dp, 8.dp, 0.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                textStyle = TextStyle(
                    fontSize = DpToSp(dp = 13.dp),
                    textAlign = TextAlign.End
                ),
                color = if (text3 == stringResource(id = R.string.totalPurchaseValue)) Color.Gray else textColor
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
                    text = "= \$ ${
                        CurrentCalculator.krwToUsd(
                            Utils.removeComma(text6).toDouble(),
                            MoeuiBitDataStore.usdPrice
                        )
                    }",
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
fun getTextColors(button: PortfolioSortButton, textState: Int): List<Any> {
    return when (button) {
        PortfolioSortButton.BUTTON_NAME -> {
            when (textState) {
                0 -> {
                    listOf(
                        stringResource(id = R.string.nameDown),
                        Color.White,
                        portfolioSortButtonSelectedBackgroundColor()
                    )
                }
                1 -> {
                    listOf(
                        stringResource(id = R.string.nameUp),
                        Color.White,
                        portfolioSortButtonSelectedBackgroundColor()
                    )
                }
                else -> {
                    listOf(
                        stringResource(id = R.string.nameUpDown),
                        MaterialTheme.colorScheme.onBackground,
                        MaterialTheme.colorScheme.background
                    )
                }
            }
        }
        PortfolioSortButton.BUTTON_RATE -> {
            when (textState) {
                2 -> {
                    listOf(
                        stringResource(id = R.string.aReturnDown),
                        Color.White,
                        portfolioSortButtonSelectedBackgroundColor()
                    )
                }
                3 -> {
                    listOf(
                        stringResource(id = R.string.aReturnUp),
                        Color.White,
                        portfolioSortButtonSelectedBackgroundColor()
                    )
                }
                else -> {
                    listOf(
                        stringResource(id = R.string.aReturnUpDown),
                        MaterialTheme.colorScheme.onBackground,
                        MaterialTheme.colorScheme.background
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
            .background(color = MaterialTheme.colorScheme.background)
            .drawUnderLine(lineColor = Color.DarkGray, strokeWidth = 2f)
    )
}