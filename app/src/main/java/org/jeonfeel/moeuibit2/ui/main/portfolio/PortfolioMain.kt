package org.jeonfeel.moeuibit2.ui.main.portfolio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.ui.coindetail.chart.UserHoldCoinPieChart
import org.jeonfeel.moeuibit2.ui.custom.*
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
    earnReward: () -> Unit
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
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            FontMediumText(
                text = stringResource(id = R.string.holdings),
                modifier = Modifier
                    .weight(1f, true)
                    .padding(8.dp, 20.dp, 0.dp, 20.dp)
                    .wrapContentHeight(),
                fontSize = 22,
                textStyle = TextStyle(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            )
            Card(
                modifier = Modifier
                    .padding(0.dp, 12.dp, 8.dp, 12.dp)
                    .wrapContentWidth(),
                elevation = 4.dp,
            ) {
                FontLightText(
                    text = stringResource(id = R.string.chargeMoney),
                    modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.CenterVertically)
                        .clickable {
                            adDialogState.value = true
//                            earnReward()
                        },
                    fontSize = 18,
                    textStyle = TextStyle(
                        color = Color.Black,
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
//        PortfolioMainSortButtons(
//            orderByNameTextInfo = orderByRateTextInfo,
//            orderByRateTextInfo = orderByNameTextInfo,
//            mainViewModel,
//            portfolioOrderState = portfolioOrderState
//        )
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
                fontSize = DpToSp(18),
            )
        )
        AutoSizeText(
            text = text2,
            modifier = Modifier
                .padding(8.dp, 5.dp, 8.dp, 0.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            textStyle = TextStyle(
                fontSize = DpToSp(22),
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
                    fontSize = DpToSp(14),
                )
            )
            AutoSizeText(
                text = text4,
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .weight(1f, true)
                    .wrapContentHeight(),
                textStyle = TextStyle(
                    fontSize = DpToSp(14),
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
                    fontSize = DpToSp(14),
                )
            )
            AutoSizeText(
                text = text6,
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .weight(1f, true)
                    .wrapContentHeight(),
                textStyle = TextStyle(
                    fontSize = DpToSp(14),
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
                fontSize = DpToSp(18),
            )
        )
        AutoSizeText(
            text = text2,
            modifier = Modifier
                .padding(8.dp, 5.dp, 8.dp, 0.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            textStyle = TextStyle(
                fontSize = DpToSp(22),
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
                fontSize = DpToSp(15),
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
                    fontSize = DpToSp(dp = 17),
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
                    fontSize = DpToSp(dp = 15),
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
                    fontSize = DpToSp(dp = 13),
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
                    fontSize = DpToSp(dp = 17),
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
                    fontSize = DpToSp(dp = 15),
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
                        fontSize = DpToSp(dp = 13),
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
        PortfolioSortButton.BUTTON_RATE -> {
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