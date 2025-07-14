package org.jeonfeel.moeuibit2.ui.main.portfolio.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.view.UserHoldCoinPieChart
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.drawUnderLine
import org.jeonfeel.moeuibit2.ui.main.portfolio.PortfolioScreenStateHolder
import org.jeonfeel.moeuibit2.ui.main.portfolio.getReturnTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.APP_PRIMARY_COLOR
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonHintTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.portfolioMainBackground
import org.jeonfeel.moeuibit2.ui.theme.portfolioSortButtonSelectedBackgroundColor
import java.math.BigDecimal

enum class PortfolioSortButton {
    BUTTON_NAME, BUTTON_RATE
}

@Composable
fun LazyItemScope.PortfolioMain(
    totalValuedAssets: State<BigDecimal>,
    totalPurchase: State<BigDecimal>,
    userSeedMoney: State<Double>,
    getPortFolioMainInfoMap: (totalValuedAssets: State<BigDecimal>, totalPurchase: State<BigDecimal>, userSeedMoney: State<Double>) -> Map<String, String>,
) {
    val portFolioMainInfo = getPortFolioMainInfoMap(totalValuedAssets, totalPurchase, userSeedMoney)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = portfolioMainBackground())
            .padding(top = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
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
                colorStandard = portFolioMainInfo[PortfolioScreenStateHolder.PORTFOLIO_MAIN_KEY_COLOR_STANDARD]?.toBigDecimal()
                    ?: BigDecimal.ZERO
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
                colorStandard = portFolioMainInfo[PortfolioScreenStateHolder.PORTFOLIO_MAIN_KEY_COLOR_STANDARD]?.toBigDecimal()
                    ?: BigDecimal.ZERO
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
    colorStandard: BigDecimal,
) {
    val textColor = getReturnTextColor(colorStandard, text5)
    Column(
        modifier = Modifier
            .padding()
            .wrapContentHeight(align = Alignment.CenterVertically)
            .weight(2f, true)
    ) {
        Text(
            text = text1,
            modifier = Modifier
                .padding(8.dp, 0.dp, 0.dp, 0.dp)
                .wrapContentHeight()
                .fillMaxWidth(),
            style = TextStyle(
                color = commonTextColor(),
                fontSize = DpToSp(15.dp),
            )
        )
        AutoSizeText(
            text = text2,
            modifier = Modifier
                .padding(8.dp, 10.dp, 8.dp, 0.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            textStyle = TextStyle(
                fontSize = DpToSp(20.dp),
                fontWeight = FontWeight.W400,
            ),
            color = commonTextColor()
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
                    .wrapContentWidth()
                    .align(Alignment.CenterVertically),
                style = TextStyle(
                    color = commonTextColor(),
                    fontSize = DpToSp(14.dp),
                )
            )
            AutoSizeText(
                text = text4,
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .weight(1f, true)
                    .wrapContentHeight()
                    .align(Alignment.CenterVertically),
                textStyle = TextStyle(
                    fontSize = DpToSp(14.dp),
                    textAlign = TextAlign.End,
                ),
                color = textColor
            )
        }
        Row(
            modifier = Modifier
                .padding(0.dp, 8.dp, 0.dp, 20.dp)
                .wrapContentHeight()
        ) {
            Text(
                text = text5,
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .wrapContentHeight()
                    .wrapContentWidth()
                    .align(Alignment.CenterVertically),
                style = TextStyle(
                    color = commonTextColor(),
                    fontSize = DpToSp(14.dp),
                )
            )
            AutoSizeText(
                text = text6,
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .weight(1f, true)
                    .wrapContentHeight()
                    .align(Alignment.CenterVertically),
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
                        APP_PRIMARY_COLOR,
                        portfolioSortButtonSelectedBackgroundColor()
                    )
                }

                1 -> {
                    listOf(
                        stringResource(id = R.string.nameUp),
                        APP_PRIMARY_COLOR,
                        portfolioSortButtonSelectedBackgroundColor()
                    )
                }

                else -> {
                    listOf(
                        stringResource(id = R.string.nameUpDown),
                        commonHintTextColor(),
                        commonBackground()
                    )
                }
            }
        }

        PortfolioSortButton.BUTTON_RATE -> {
            when (textState) {
                2 -> {
                    listOf(
                        stringResource(id = R.string.aReturnDown),
                        APP_PRIMARY_COLOR,
                        portfolioSortButtonSelectedBackgroundColor()
                    )
                }

                3 -> {
                    listOf(
                        stringResource(id = R.string.aReturnUp),
                        APP_PRIMARY_COLOR,
                        portfolioSortButtonSelectedBackgroundColor()
                    )
                }

                else -> {
                    listOf(
                        stringResource(id = R.string.aReturnUpDown),
                        commonHintTextColor(),
                        commonBackground()
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
            .padding(7.dp)
            .fillMaxWidth()
            .height(300.dp)
            .background(color = commonBackground())
            .drawUnderLine(lineColor = Color.DarkGray, strokeWidth = 2f)
    )
}