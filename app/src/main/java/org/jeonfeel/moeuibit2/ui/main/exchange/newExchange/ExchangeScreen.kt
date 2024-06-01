package org.jeonfeel.moeuibit2.ui.main.exchange.newExchange

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import kotlinx.coroutines.delay
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.ui.common.CommonText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.MarketChangeState
import org.jeonfeel.moeuibit2.ui.common.clearFocusOnKeyboardDismiss
import org.jeonfeel.moeuibit2.ui.common.drawUnderLine
import org.jeonfeel.moeuibit2.ui.main.exchange.SortButtons
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortOrder
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortType
import org.jeonfeel.moeuibit2.ui.theme.exchangeMarketButtonTextColor
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedFluctuateString
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedString
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import org.jeonfeel.moeuibit2.utils.EvenColor
import org.jeonfeel.moeuibit2.utils.FallColor
import org.jeonfeel.moeuibit2.utils.RiseColor
import org.jeonfeel.moeuibit2.utils.convertMarketChangeState
import org.jeonfeel.moeuibit2.utils.getFluctuateColor

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
private fun SortingSection() {
    val buttonIdList = SortButtons.entries.toTypedArray()
    var sortType by remember { mutableStateOf(SortType.NONE) }
    var sortOrder by remember { mutableStateOf(SortOrder.DESCENDING) }
    Row {
        SortButton(
            text = "가격",
            sortType = SortType.PRICE,
            selectedSortType = sortType,
            sortOrder = sortOrder,
            onSortClick = {
                sortType = it
                sortOrder =
                    if (sortOrder == SortOrder.DESCENDING) SortOrder.ASCENDING else SortOrder.DESCENDING
//                sortCoins(it, sortOrder)
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        SortButton(
            text = "거래량",
            sortType = SortType.VOLUME,
            selectedSortType = sortType,
            sortOrder = sortOrder,
            onSortClick = {
                sortType = it
                sortOrder =
                    if (sortOrder == SortOrder.DESCENDING) SortOrder.ASCENDING else SortOrder.DESCENDING
//                sortCoins(it, sortOrder)
            }
        )
    }
}

@Composable
private fun RowScope.SortButton(
    text: String,
    sortType: SortType,
    selectedSortType: SortType,
    sortOrder: SortOrder,
    onSortClick: (SortType) -> Unit
) {
    Button(
        onClick = { onSortClick(sortType) },
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.White
//               if (selectedSortType == sortType) MaterialTheme.colors.primary else MaterialTheme.colors.surface
        )
    ) {
        Text(text = text)
        if (selectedSortType == sortType) {
//            Icon(
//                imageVector = if (sortOrder == SortOrder.DESCENDING) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
//                contentDescription = null
//            )
        }
    }
}

@Composable
private fun CoinTickerSection(
    tickerList: List<CommonExchangeModel>
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = tickerList, key = { it.market }) {
            CoinTickerView(
                name = it.koreanName,
                lastPrice = it.tradePrice.toString(),
                fluctuateRate = it.signedChangeRate.toFloat(),
                fluctuatePrice = it.signedChangePrice.toFloat(),
                change = it.change.convertMarketChangeState(),
                onClickEvent = {},
                onFavoriteClick = {}
            )
        }
    }
}

@Composable
fun CoinTickerView(
    modifier: Modifier = Modifier,
    name: String,
    lastPrice: String,
    fluctuateRate: Float,
    fluctuatePrice: Float,
    change: MarketChangeState,
    onClickEvent: () -> Unit,
    onFavoriteClick: () -> Unit,
    infiniteTransition: InfiniteTransition = rememberInfiniteTransition(label = "")
) = Row(
    modifier = modifier
        .clickable { onClickEvent.invoke() }
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 8.dp),
    verticalAlignment = Alignment.Top
) {
    val animationDurationTimeMills = 200
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.0f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = animationDurationTimeMills
                0.25f at 100
            },
            repeatMode = RepeatMode.Reverse
        ),
        label = "animation alpha"
    )
    var animateNeed by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = fluctuateRate) {
        animateNeed = true
        delay(animationDurationTimeMills.toLong())
        animateNeed = false
    }
    Spacer(modifier = Modifier.width(10.dp))
    Text(
        text = name,
        modifier = Modifier,
        textAlign = TextAlign.Center,
        fontSize = DpToSp(18.dp),
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.width(10.dp))
    Spacer(modifier = Modifier.weight(1f))
    Text(
        text = lastPrice,
        modifier = Modifier
            .background(Color.White)
            .drawBehind {
                drawLine(
                    color = if (animateNeed) {
                        when (change) {
                            MarketChangeState.Even -> EvenColor.copy(alpha = alpha)
                            MarketChangeState.Fall -> FallColor.copy(alpha = alpha)
                            MarketChangeState.Rise -> RiseColor.copy(alpha = alpha)
                        }
                    } else Color.White,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = if (animateNeed) 2.dp.toPx() else 0.dp.toPx()
                )
            }
            .padding(2.dp),
        textAlign = TextAlign.End,
        fontSize = DpToSp(16.dp),
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.width(10.dp))
    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .widthIn(min = 60.dp)
            .fillMaxHeight()
    ) {
        FluctuationStatusView(
            fluctuateRate = fluctuateRate,
            fontSize = DpToSp(14.dp),
            radius = 4.dp
        )
        Text(
            modifier = Modifier.padding(end = 10.dp),
            text = fluctuatePrice.newBigDecimal(scale = 2).formattedString(),
            textAlign = TextAlign.End,
            fontSize = DpToSp(12.dp),
            fontWeight = FontWeight.Bold,
            color = fluctuatePrice.getFluctuateColor()
        )
    }
}

@Composable
fun FluctuationStatusView(
    modifier: Modifier = Modifier,
    fluctuateRate: Float,
    fontSize: TextUnit = DpToSp(8.dp),
    radius: Dp = 8.dp,
) = Surface(
    color = fluctuateRate.getFluctuateColor(),
    modifier = modifier,
    shape = RoundedCornerShape(radius)
) {
    Text(
        text = fluctuateRate.formattedFluctuateString() + "%",
        fontSize = fontSize,
        color = Color.White,
        modifier = Modifier.padding(horizontal = 10.dp)
    )
}