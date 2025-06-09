package org.jeonfeel.moeuibit2.ui.main.exchange.sections

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.GlobalState
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.main.exchange.dialog.SelectExchangeDialog
import org.jeonfeel.moeuibit2.ui.theme.newtheme.coinsite.coinSiteIconBorderColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.utils.NetworkConnectivityObserver

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SelectTradeCurrencySection(
    tradeCurrencyState: State<Int>,
    pagerState: PagerState,
    tabTitleList: Array<String> = stringArrayResource(id = R.array.exchange_screen_tab_list),
    modifier: Modifier = Modifier,
    changeTradeCurrency: (Int) -> Unit,
    coroutineScope: CoroutineScope,
    changeExchange: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = Modifier.height(0.dp))
    Row(
        modifier.fillMaxWidth()
    ) {
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
            backgroundColor = commonBackground(),
            divider = {}
        ) {
            tabTitleList.forEachIndexed { index, title ->
                Tab(
                    interactionSource = object : MutableInteractionSource {
                        override val interactions: Flow<Interaction> = emptyFlow()
                        override suspend fun emit(interaction: Interaction) {}
                        override fun tryEmit(interaction: Interaction) = true
                    },
                    text = {
                        Text(
                            text = title,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            ),
                            fontSize = DpToSp(dp = 17.dp),
                            modifier = Modifier.weight(1f)
                        )
                    },
                    selectedContentColor = commonTextColor(),
                    unselectedContentColor = Color(0xff8C939E),
                    selected = tradeCurrencyState.value == index,
                    onClick = {
                        if (tradeCurrencyState.value != index && NetworkConnectivityObserver.isNetworkAvailable.value) {
                            coroutineScope.launch {
                                keyboardController?.hide()
                                changeTradeCurrency(index)
                            }
                        }
                    },
                )
            }
        }
//        Spacer(modifier = Modifier.weight(1f))
        SelectExchangeSection(
            changeExchange = changeExchange
        )
    }
}

@Composable
fun RowScope.SelectExchangeSection(changeExchange: () -> Unit) {
    val image = when (GlobalState.globalExchangeState.value) {
        EXCHANGE_UPBIT -> R.drawable.img_upbit
        EXCHANGE_BITTHUMB -> R.drawable.img_bitthumb
        else -> R.drawable.img_upbit
    }

    val text = when (GlobalState.globalExchangeState.value) {
        EXCHANGE_UPBIT -> "업비트"
        EXCHANGE_BITTHUMB -> "빗썸"
        else -> "업비트"
    }

    val showDialog = remember { mutableStateOf(false) }


    SelectExchangeDialog(
        dialogState = showDialog,
        initialExchange = GlobalState.globalExchangeState.value,
        onConfirm = { newExchange ->
            GlobalState.setExchangeState(newExchange)
            changeExchange()
        }
    )

    Row(
        modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically)
            .clickable {
                showDialog.value = true
            }
    ) {
        Image(
            painter = painterResource(image),
            modifier = Modifier
                .size(25.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(1.3.dp, color = coinSiteIconBorderColor(), RoundedCornerShape(10.dp))
                .align(Alignment.CenterVertically),
            contentDescription = ""
        )

        Text(
            text = text,
            style = TextStyle(fontSize = DpToSp(14.dp), color = commonTextColor()),
            modifier = Modifier
                .padding(start = 7.dp)
                .align(Alignment.CenterVertically)
        )
    }
}