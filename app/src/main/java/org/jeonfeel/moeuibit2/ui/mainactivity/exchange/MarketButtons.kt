package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import android.util.Log
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.main.viewmodel.MainViewModel
import org.jeonfeel.moeuibit2.ui.util.drawUnderLine

@OptIn(ExperimentalPagerApi::class)
@Composable
fun marketButtons(
    mainViewModel: MainViewModel,
    pagerState: PagerState,
    tabTitleList: List<String>,
) {
    val coroutineScope = rememberCoroutineScope()

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
                                Log.e("tabActive", "tabActive")
                                mainViewModel.selectedMarketState.value = index
                            }
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

private object NoRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = Color.Unspecified

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(0.0f, 0.0f, 0.0f, 0.0f)
}