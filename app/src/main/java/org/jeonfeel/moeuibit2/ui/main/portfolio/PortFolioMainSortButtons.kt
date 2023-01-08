package org.jeonfeel.moeuibit2.ui.main.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jeonfeel.moeuibit2.ui.custom.DpToSp
import org.jeonfeel.moeuibit2.ui.viewmodels.MainViewModel
import org.jeonfeel.moeuibit2.ui.custom.drawUnderLine

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
            fontSize = DpToSp(15),
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
            fontSize = DpToSp(15),
            textAlign = TextAlign.Center,
            style = TextStyle(color = orderByRateTextInfo[1] as Color)
        )
    }
}