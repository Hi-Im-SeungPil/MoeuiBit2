package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.main.viewmodel.MainViewModel
import org.jeonfeel.moeuibit2.constant.SELECTED_BTC_MARKET
import org.jeonfeel.moeuibit2.constant.SELECTED_FAVORITE
import org.jeonfeel.moeuibit2.constant.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.ui.util.drawUnderLine

/**
 * 거래소 화면에 원화, 관심, btc 버튼
 */
@Composable
fun MarketButtons(mainViewModel: MainViewModel = viewModel()) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .drawUnderLine(lineColor = Color.DarkGray)
    ) {
        MarketButton(interactionSource,
            mainViewModel,
            stringResource(id = R.string.krw),
            SELECTED_KRW_MARKET)
        MarketButton(interactionSource,
            mainViewModel,
            stringResource(id = R.string.btc),
            SELECTED_BTC_MARKET)
        MarketButton(interactionSource,
            mainViewModel,
            stringResource(id = R.string.favorite),
            SELECTED_FAVORITE)
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight()
        ) {
            Text(text = "")
        }
    }
}

/**
 * 원화 btc 관심 버튼 동작.
 */
@Composable
fun RowScope.MarketButton(
    interactionSource: MutableInteractionSource,
    mainViewModel: MainViewModel,
    text: String,
    buttonId: Int,
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .wrapContentHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                mainViewModel.showFavoriteState.value = buttonId == SELECTED_FAVORITE
                mainViewModel.selectedMarketState.value = buttonId
                mainViewModel.sortList(mainViewModel.sortButtonState.value)
            }
    ) {
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                color = getTextColor(
                    mainViewModel,
                    buttonId
                ), fontSize = 17.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
            )
        )
    }
}

/**
 * 라디오 버튼 기능.
 */
@Composable
fun getTextColor(mainViewModel: MainViewModel = viewModel(), buttonId: Int): Color {
    return if (mainViewModel.selectedMarketState.value == buttonId) {
        colorResource(R.color.C0F0F5C)
    } else {
        Color.LightGray
    }
}