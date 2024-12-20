package org.jeonfeel.moeuibit2.ui.main.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.drawUnderLine
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable

@Composable
fun PortfolioMainSortButtons(
    orderByRateTextInfo: List<Any>,
    orderByNameTextInfo: List<Any>,
    portfolioOrderState: State<Int>,
    sortUserHoldCoin: (orderState: Int) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(color = Color(0x80ECECEC))
            .padding(vertical = 7.dp)
    ) {
        Text(
            text = orderByNameTextInfo[0] as String,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .noRippleClickable {
                    if (portfolioOrderState.value != PortfolioViewModel.SORT_NAME_DEC && portfolioOrderState.value != PortfolioViewModel.SORT_NAME_ASC) {
                        sortUserHoldCoin(PortfolioViewModel.SORT_NAME_DEC)
                    } else if (portfolioOrderState.value == PortfolioViewModel.SORT_NAME_DEC) {
                        sortUserHoldCoin(PortfolioViewModel.SORT_NAME_ASC)
                    } else {
                        sortUserHoldCoin(PortfolioViewModel.SORT_DEFAULT)
                    }
                },
            fontSize = DpToSp(15.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(color = orderByNameTextInfo[1] as Color)
        )

        Divider(
            Modifier
                .width(1.dp)
                .height(30.dp)
                .align(Alignment.CenterVertically), color = Color.LightGray
        )

        Text(
            text = orderByRateTextInfo[0] as String,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .noRippleClickable {
                    if (portfolioOrderState.value != PortfolioViewModel.SORT_RATE_DEC && portfolioOrderState.value != PortfolioViewModel.SORT_RATE_ASC) {
                        sortUserHoldCoin(PortfolioViewModel.SORT_RATE_DEC)
                    } else if (portfolioOrderState.value == PortfolioViewModel.SORT_RATE_DEC) {
                        sortUserHoldCoin(PortfolioViewModel.SORT_RATE_ASC)
                    } else {
                        sortUserHoldCoin(PortfolioViewModel.SORT_DEFAULT)
                    }
                },
            fontSize = DpToSp(15.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(color = orderByRateTextInfo[1] as Color)
        )
    }
}