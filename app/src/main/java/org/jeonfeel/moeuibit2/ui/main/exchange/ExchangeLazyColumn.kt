package org.jeonfeel.moeuibit2.ui.main.exchange

import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.SELECTED_BTC_MARKET
import org.jeonfeel.moeuibit2.constants.SYMBOL_KRW
import org.jeonfeel.moeuibit2.constants.SYMBOL_USD
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.SwipeDetector
import org.jeonfeel.moeuibit2.ui.common.drawUnderLine
import org.jeonfeel.moeuibit2.ui.theme.decreaseColor
import org.jeonfeel.moeuibit2.ui.theme.increaseColor
import org.jeonfeel.moeuibit2.ui.theme.lazyColumnItemUnderLineColor
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator

@Composable
fun ExchangeScreenLazyColumns(
    filteredExchangeCoinList: List<CommonExchangeModel>,
    preCoinListAndPosition: Pair<ArrayList<CommonExchangeModel>, HashMap<String, Int>>,
    textFieldValueState: MutableState<String>,
    loadingFavorite: State<Boolean>? = null,
    btcPrice: State<Double>,
    scrollState: LazyListState,
    lazyColumnItemClickAction: (CommonExchangeModel) -> Unit,
    createCoinName: (String, String) -> AnnotatedString,
    changeSelectedMarketState: (Int) -> Unit,
    selectedMarketState: State<Int>,
) {
    val lazyColumnVisibility = remember {
        mutableStateOf(true)
    }
    val coroutineScope = rememberCoroutineScope()

    when {
        loadingFavorite != null && !loadingFavorite.value && filteredExchangeCoinList.isEmpty() -> {
            Text(
                text = stringResource(id = R.string.noFavorite),
                modifier = Modifier
                    .padding(0.dp, 20.dp, 0.dp, 0.dp)
                    .fillMaxSize(),
                fontSize = DpToSp(dp = 20.dp),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = TextStyle(color = MaterialTheme.colorScheme.onBackground)
            )
        }

        filteredExchangeCoinList.isEmpty() && textFieldValueState.value.isNotEmpty() -> {
            Text(
                text = stringResource(id = R.string.noSearchingCoin),
                modifier = Modifier
                    .padding(0.dp, 20.dp, 0.dp, 0.dp)
                    .fillMaxSize(),
                fontSize = DpToSp(dp = 20.dp),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = TextStyle(color = MaterialTheme.colorScheme.onBackground)
            )
        }

        else -> {
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .SwipeDetector(
                    onSwipeLeftAction = {
                        coroutineScope.launch {
                            lazyColumnVisibility.value = false
                            if (selectedMarketState.value != 0) changeSelectedMarketState(
                                selectedMarketState.value - 1
                            )
                            delay(500L)
                            lazyColumnVisibility.value = true
                        }
                    },
                    onSwipeRightAction = {
                        coroutineScope.launch {
                            lazyColumnVisibility.value = false
                            if (selectedMarketState.value != 2) changeSelectedMarketState(
                                selectedMarketState.value + 1
                            )
                            delay(500L)
                            lazyColumnVisibility.value = true
                        }
                    }
                ), state = scrollState) {
                itemsIndexed(
                    items = filteredExchangeCoinList
                ) { _, coinListElement ->
                    val preCoinListPosition =
                        preCoinListAndPosition.second[coinListElement.market] ?: 0
                    val preCoinElement = preCoinListAndPosition.first[preCoinListPosition]
                    val currentMarketState = Utils.getSelectedMarket(coinListElement.market)
                    ExchangeScreenLazyColumnItem(
                        commonExchangeModel = coinListElement,
                        currentMarketState = currentMarketState,
                        signedChangeRate = CurrentCalculator.signedChangeRateCalculator(
                            coinListElement.signedChangeRate
                        ),
                        curTradePrice = CurrentCalculator.tradePriceCalculator(
                            coinListElement.tradePrice.toDouble(),
                            currentMarketState
                        ),
                        accTradePrice24h = CurrentCalculator.accTradePrice24hCalculator(
                            coinListElement.accTradePrice24h.toDouble(),
                            currentMarketState
                        ),
                        formattedPreTradePrice = CurrentCalculator.tradePriceCalculator(
                            preCoinElement.tradePrice.toDouble(),
                            currentMarketState
                        ),
                        btcToKrw = CurrentCalculator.btcToKrw(
                            (coinListElement.tradePrice.toDouble() * btcPrice.value),
                            currentMarketState
                        ),
                        unit = Utils.getUnit(currentMarketState),
                        lazyColumnItemClickAction = lazyColumnItemClickAction,
                        createCoinName = createCoinName
                    )
                    preCoinListAndPosition.first[preCoinListPosition] = coinListElement
                }
            }
        }
    }
}

@Composable
fun ExchangeScreenLazyColumnItem(
    commonExchangeModel: CommonExchangeModel,
    currentMarketState: Int,
    signedChangeRate: String,
    curTradePrice: String,
    accTradePrice24h: String,
    formattedPreTradePrice: String,
    btcToKrw: String,
    unit: String,
    lazyColumnItemClickAction: (CommonExchangeModel) -> Unit,
    createCoinName: (String, String) -> AnnotatedString
) {
    val textColor = Utils.getIncreaseOrDecreaseColor(signedChangeRate.toFloat())

    Row(
        Modifier
            .fillMaxWidth()
            .height(50.dp)
            .drawUnderLine(lineColor = lazyColumnItemUnderLineColor())
            .clickable {
                lazyColumnItemClickAction(commonExchangeModel)
            }
    ) {
        // 코인명 심볼
        Column(
            Modifier
                .weight(1f)
                .align(Alignment.Bottom)
        ) {
            Text(
                text = createCoinName(
                    commonExchangeModel.warning.toString(),
                    commonExchangeModel.koreanName
                ),
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Bottom),
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = DpToSp(dp = 13.dp)
                ),
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${commonExchangeModel.symbol}/$unit",
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontSize = DpToSp(dp = 13.dp)
                ),
                overflow = TextOverflow.Ellipsis
            )
        }
        // 코인가격
        Box(
            modifier = Modifier
                .padding(0.dp, 4.dp)
                .fillMaxHeight()
                .border(
                    1.dp, color = when {
                        formattedPreTradePrice < curTradePrice -> {
                            increaseColor()
                        }

                        formattedPreTradePrice > curTradePrice -> {
                            decreaseColor()
                        }

                        else -> {
                            Color.Transparent
                        }
                    }
                )
                .weight(1f)
        ) {
            TradePrice(
                tradePrice = curTradePrice,
                textColor = textColor,
                btcToKrw = btcToKrw,
                doubleTradePrice = commonExchangeModel.tradePrice.toDouble(),
                selectedMarket = currentMarketState
            )
        }
        // 코인 변동률
        Text(
            text = signedChangeRate
                .plus("%"),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight(),
            style = TextStyle(
                textAlign = TextAlign.Center, color = textColor, fontSize = DpToSp(dp = 13.dp)
            )
        )
        // 거래대금
        Volume(
            accTradePrice24h,
            commonExchangeModel.accTradePrice24h.toDouble(),
            commonExchangeModel.market
        )
    }
}

@Composable
fun TradePrice(
    tradePrice: String,
    textColor: Color,
    btcToKrw: String = "",
    doubleTradePrice: Double,
    selectedMarket: Int
) {
    if (btcToKrw.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = tradePrice,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .wrapContentHeight(),
                style = TextStyle(
                    textAlign = TextAlign.Center, color = textColor, fontSize = DpToSp(dp = 13.dp)
                )
            )
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = tradePrice,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .wrapContentHeight(),
                style = TextStyle(
                    textAlign = TextAlign.Center, color = textColor, fontSize = DpToSp(dp = 13.dp)
                )
            )
            if (btcToKrw == "0.0000") {
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .fillMaxHeight()
                )
            } else {
                if (selectedMarket == SELECTED_BTC_MARKET) {
                    AutoSizeText(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .fillMaxHeight(), text = btcToKrw.plus(" $SYMBOL_KRW"),
                        TextStyle(fontSize = DpToSp(dp = 13.dp), textAlign = TextAlign.End),
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.Volume(volume: String, doubleVolume: Double, market: String) {
    val unit = if (market.startsWith(SYMBOL_KRW)) {
        stringResource(id = R.string.million)
    } else {
        ""
    }
    Column(
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = volume.plus(unit),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .fillMaxHeight()
                .wrapContentHeight(),
            style = TextStyle(
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = DpToSp(dp = 13.dp)
            )
        )
    }
}