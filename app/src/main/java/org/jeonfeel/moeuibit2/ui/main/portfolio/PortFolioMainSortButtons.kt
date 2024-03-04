package org.jeonfeel.moeuibit2.ui.main.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.drawUnderLine

@Composable
fun PortfolioMainSortButtons(
    orderByRateTextInfo: List<Any>,
    orderByNameTextInfo: List<Any>,
    isPortfolioSocketRunning: MutableState<Boolean>,
    portfolioOrderState: MutableState<Int>,
    sortUserHoldCoin: (orderState: Int) -> Unit
) {
    Row(
        Modifier
            .background(color = MaterialTheme.colorScheme.background)
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
                    if (isPortfolioSocketRunning.value) {
                        if (portfolioOrderState.value != PortfolioViewModel.SORT_NAME_DEC && portfolioOrderState.value != PortfolioViewModel.SORT_NAME_ASC) {
                            portfolioOrderState.value = PortfolioViewModel.SORT_NAME_DEC
                        } else if (portfolioOrderState.value == PortfolioViewModel.SORT_NAME_DEC) {
                            portfolioOrderState.value = PortfolioViewModel.SORT_NAME_ASC
                        } else {
                            portfolioOrderState.value = PortfolioViewModel.SORT_DEFAULT
                        }
                        sortUserHoldCoin(portfolioOrderState.value)
                    }
                }
                .padding(0.dp, 8.dp),
            fontSize = DpToSp(15.dp),
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
                    if (isPortfolioSocketRunning.value) {
                        if (portfolioOrderState.value != PortfolioViewModel.SORT_RATE_DEC && portfolioOrderState.value != PortfolioViewModel.SORT_RATE_ASC) {
                            portfolioOrderState.value = PortfolioViewModel.SORT_RATE_DEC
                        } else if (portfolioOrderState.value == PortfolioViewModel.SORT_RATE_DEC) {
                            portfolioOrderState.value = PortfolioViewModel.SORT_RATE_ASC
                        } else {
                            portfolioOrderState.value = PortfolioViewModel.SORT_DEFAULT
                        }
                        sortUserHoldCoin(portfolioOrderState.value)
                    }
                }
                .padding(0.dp, 8.dp),
            fontSize = DpToSp(15.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(color = orderByRateTextInfo[1] as Color)
        )
    }
}