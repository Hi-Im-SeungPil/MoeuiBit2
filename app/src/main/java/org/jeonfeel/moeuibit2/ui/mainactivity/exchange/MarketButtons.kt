package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import org.jeonfeel.moeuibit2.MoeuiBit.isKor
import org.jeonfeel.moeuibit2.MoeuiBit.usdPrice
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.main.viewmodel.MainViewModel
import org.jeonfeel.moeuibit2.ui.util.DpToSp
import org.jeonfeel.moeuibit2.ui.util.drawUnderLine
import kotlin.math.round

@OptIn(ExperimentalPagerApi::class)
@Composable
fun marketButtons(
    mainViewModel: MainViewModel,
    pagerState: PagerState,
    tabTitleList: List<String>,
) {

    Row(Modifier
        .fillMaxWidth()
        .drawUnderLine(lineColor = Color.DarkGray)) {
        CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                        color = Color.Transparent
                    )
                },
                modifier = Modifier
                    .weight(3f),
                backgroundColor = Color.White,
                divider = {}
            ) {
                tabTitleList.forEachIndexed { index, title ->
                    Tab(
                        text = {
                            Text(
                                text = title,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            )
                        },
                        selectedContentColor = colorResource(R.color.C0F0F5C),
                        unselectedContentColor = Color.LightGray,
                        selected = mainViewModel.selectedMarketState.value == index,
                        onClick = {
                            if (mainViewModel.selectedMarketState.value != index) {
                                mainViewModel.selectedMarketState.value = index
                            }
                        },
                    )
                }
            }
            if (isKor) {
                Spacer(modifier = Modifier.weight(1f))
            } else {
                Text(text = "\$1 =\n${round(usdPrice).toInt()} KRW",
                    fontSize = DpToSp(dp = 13.dp),
                    style = TextStyle(textAlign = TextAlign.Center),
                    modifier = Modifier.weight(1f).align(Alignment.CenterVertically))
            }
        }
    }
}

private object NoRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = Color.Unspecified

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(0.0f, 0.0f, 0.0f, 0.0f)
}