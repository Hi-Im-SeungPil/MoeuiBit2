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
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.ioDispatcher
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
import java.math.BigDecimal
import kotlin.math.round

enum class PortfolioSortButton {
    BUTTON_NAME, BUTTON_RATE
}

@Composable
fun PortfolioMain(
    portfolioOrderState: State<Int>,
    totalValuedAssets: State<BigDecimal>,
    totalPurchase: State<BigDecimal>,
    userSeedMoney: State<Long>,
    orderByNameTextInfo: List<Any>,
    orderByRateTextInfo: List<Any>,
    adDialogState: MutableState<Boolean>,
    pieChartState: MutableState<Boolean>,
    sortUserHoldCoin: (orderState: Int) -> Unit,
    getPortFolioMainInfoMap: (
        totalValuedAssets: State<BigDecimal>,
        totalPurchase: State<BigDecimal>,
        userSeedMoney: State<Long>
    ) -> Map<String, String>
) {
    val portFolioMainInfo = getPortFolioMainInfoMap(totalValuedAssets, totalPurchase, userSeedMoney)
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
//
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

            PortfolioMainItem(
                text1 = stringResource(id = R.string.userSeedMoney),
                text2 = portFolioMainInfo[PortfolioScreenStateHolder.PORTFOLIO_MAIN_KEY_CALC_USER_SEED_MONEY]
                    ?: "",
                text3 = stringResource(id = R.string.totalPurchaseValue),
                text4 = portFolioMainInfo[PortfolioScreenStateHolder.PORTFOLIO_MAIN_KEY_TOTAL_PURCHASE_VALUE]
                    ?: "",
                text5 = stringResource(id = R.string.totalValuedAssets),
                text6 = portFolioMainInfo[PortfolioScreenStateHolder.PORTFOLIO_MAIN_KEY_CALC_TOTAL_VALUED_ASSETS]
                    ?: "",
                colorStandard = portFolioMainInfo[PortfolioScreenStateHolder.PORTFOLIO_MAIN_KEY_COLOR_STANDARD]?.toLong()
                    ?: 0L
            )
            PortfolioMainItem(
                text1 = stringResource(id = R.string.totalHoldings),
                text2 = portFolioMainInfo[PortfolioScreenStateHolder.PORTFOLIO_MAIN_KEY_TOTAL_HOLDINGS]
                    ?: "",
                text3 = stringResource(id = R.string.valuationGainOrLoss),
                text4 = portFolioMainInfo[PortfolioScreenStateHolder.PORTFOLIO_MAIN_KEY_VALUATION_GAIN_OR_LOSE]
                    ?: "",
                text5 = stringResource(id = R.string.aReturn),
                text6 = portFolioMainInfo[PortfolioScreenStateHolder.PORTFOLIO_MAIN_KEY_A_RETURN]
                    ?: "",
                colorStandard = portFolioMainInfo[PortfolioScreenStateHolder.PORTFOLIO_MAIN_KEY_COLOR_STANDARD]?.toLong()
                    ?: 0L
            )

        }
//        PortfolioPieChart(
//            pieChartState = pieChartState,
//            userSeedMoney = userSeedMoney,
//            userHoldCoinList = userHoldCoinList
//        )
        PortfolioMainSortButtons(
            orderByNameTextInfo = orderByNameTextInfo,
            orderByRateTextInfo = orderByRateTextInfo,
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