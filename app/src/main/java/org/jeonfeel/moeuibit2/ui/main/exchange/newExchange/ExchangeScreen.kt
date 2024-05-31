package org.jeonfeel.moeuibit2.ui.main.exchange.newExchange

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.common.CommonText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.clearFocusOnKeyboardDismiss
import org.jeonfeel.moeuibit2.ui.common.drawUnderLine
import org.jeonfeel.moeuibit2.ui.theme.exchangeMarketButtonTextColor

enum class SortButtonID {
    SORT_BUTTON_PRICE,
    SORT_BUTTON_RATE,
    SORT_BUTTON_VOLUME
}

enum class SortState {
    SORT_STATE_NONE,
    SORT_STATE_PRICE_DESC,
    SORT_STATE_PRICE_ASC,
    SORT_STATE_VOLUME_DESC,
    SORT_STATE_VOLUME_ASC,
    SORT_STATE_CHANGE_DESC,
    SORT_STATE_CHANGE_ASC,
}

@Composable
fun ExchangeScreen() {

}

@Composable
private fun SearchSection(
    textFieldValueState: MutableState<String>,
) {
    val focusManager = LocalFocusManager.current

    BasicTextField(value = textFieldValueState.value, onValueChange = {
        textFieldValueState.value = it
    }, singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .clearFocusOnKeyboardDismiss(),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.primary,
            fontSize = DpToSp(17.dp)
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(25.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Box(Modifier.weight(1f)) {
                    if (textFieldValueState.value.isEmpty()) {
                        CommonText(
                            stringResource(id = R.string.textFieldText),
                            textStyle = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.primary,
                            ),
                            fontSize = 17.dp
                        )
                    }
                    innerTextField()
                }
                if (textFieldValueState.value.isNotEmpty()) {
                    IconButton(onClick = {
                        textFieldValueState.value = ""
                        focusManager.clearFocus(true)
                    }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(25.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        })
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun SelectTradeCurrencySection(
    selectedMarketState: State<Int>,
    pagerState: PagerState,
    tabTitleList: List<String>,
    changeSelectedMarketState: (Int) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .drawUnderLine(lineColor = MaterialTheme.colorScheme.outline)
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
            backgroundColor = MaterialTheme.colorScheme.background,
            divider = {}
        ) {
            tabTitleList.forEachIndexed { index, title ->
                Tab(
                    text = {
                        CommonText(
                            text = title,
                            textStyle = TextStyle(
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            ),
                            fontSize = 17.dp
                        )
                    },
                    selectedContentColor = exchangeMarketButtonTextColor(selected = true),
                    unselectedContentColor = exchangeMarketButtonTextColor(selected = false),
                    selected = selectedMarketState.value == index,
                    onClick = {
                        if (selectedMarketState.value != index) {
                            changeSelectedMarketState(index)
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun SortingSection() {

}

@Composable
fun CoinTickerSection() {
//    Row()
}

@Composable
fun CoinTickerView() {

}