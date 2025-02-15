package org.jeonfeel.moeuibit2.ui.main.portfolio.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.main.portfolio.PortfolioViewModel
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonDividerColor

@Composable
fun PortfolioMainSortButtons(
    orderByRateTextInfo: List<Any>,
    orderByNameTextInfo: List<Any>,
    portfolioOrderState: State<Int>,
    sortUserHoldCoin: (orderState: Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(commonBackground())
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically

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
                .height(23.dp)
                .align(Alignment.CenterVertically), color = commonDividerColor()
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