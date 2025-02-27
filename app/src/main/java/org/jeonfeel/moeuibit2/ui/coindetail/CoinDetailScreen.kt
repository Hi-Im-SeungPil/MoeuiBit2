package org.jeonfeel.moeuibit2.ui.coindetail

import android.widget.TextView
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.orhanobut.logger.Logger
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.COIN_IMAGE_BASE_URL
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.Caution
import org.jeonfeel.moeuibit2.ui.coindetail.order.ui.CoinDetailMainTabRow
import org.jeonfeel.moeuibit2.ui.coindetail.order.ui.TabRowMainNavigation
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonRiseColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringForBtc
import org.jeonfeel.moeuibit2.utils.isTradeCurrencyKrw
import org.jeonfeel.moeuibit2.utils.secondDecimal
import java.math.BigDecimal
import kotlin.reflect.KFunction0
import kotlin.reflect.KFunction1

@Composable
fun CoinDetailScreen(
    viewModel: NewCoinDetailViewModel = hiltViewModel(),
    market: String,
    warning: Boolean,
    navController: NavHostController,
    caution: Caution?
) {
    val state = rememberCoinDetailStateHolder(context = LocalContext.current, caution = caution)
    val coroutineScope = rememberCoroutineScope()
    val cautionMessageList = remember {
        state.getCautionMessageList(viewModel.coinTicker.value?.signedChangeRate ?: 0.0)
    }
    val snackBarHostState = remember {
        SnackbarHostState()
    }

    AddLifecycleEvent(
        onCreateAction = {
            viewModel.init(market)
        },
        onStartAction = {
            viewModel.onStart(market)
        },
        onStopAction = {
            viewModel.onStop()
        }
    )

    LaunchedEffect(viewModel.isShowDeListingSnackBar.value) {
        if (viewModel.isShowDeListingSnackBar.value) {
            Logger.e("snackBar")
            coroutineScope.launch {
                snackBarHostState.showSnackbar(
                    message = viewModel.deListingMessage,
                    actionLabel = "확인",
                    duration = androidx.compose.material3.SnackbarDuration.Indefinite
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .background(commonBackground())
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(commonBackground())
        ) {
            NewCoinDetailTopAppBar(
                coinSymbol = market.substring(4),
                title = viewModel.koreanCoinName.value,
                showToast = state::showToast,
                updateIsFavorite = viewModel::updateIsFavorite,
                isFavorite = viewModel.isFavorite,
                backPressed = { navController.popBackStack() },
                warning = warning,
                isCaution = cautionMessageList.isNotEmpty()
            )
            CoinDetailPriceSection(
                fluctuateRate = state.getFluctuateRate(
                    viewModel.coinTicker.value?.signedChangeRate ?: 1.0
                ),
                fluctuatePrice = state.getFluctuatePrice(
                    viewModel.coinTicker.value?.signedChangePrice ?: 0.0,
                    market = market
                ),
                price = state.getCoinDetailPrice(
                    viewModel.coinTicker.value?.tradePrice?.toDouble() ?: 0.0,
                    viewModel.rootExchange ?: "",
                    market
                ),
                symbol = market.substring(4),
                priceTextColor = state.getCoinDetailPriceTextColor(
                    viewModel.coinTicker.value?.signedChangeRate?.secondDecimal()?.toDouble() ?: 0.0
                ),
                btcPrice = viewModel.btcPrice.value,
                market = market,
                cautionMessageList = cautionMessageList,
                lineChartDataList = viewModel.lineChartData.value
            )
            CoinDetailMainTabRow(navController = state.navController)

            Box {
                TabRowMainNavigation(
                    navHostController = state.navController,
                    market = market,
                    viewModel = viewModel
                )
            }
        }

        androidx.compose.material3.SnackbarHost(
            hostState = snackBarHostState, modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun NewCoinDetailTopAppBar(
    coinSymbol: String,
    title: String,
    showToast: KFunction1<String, Unit>,
    updateIsFavorite: KFunction0<Unit>,
    isFavorite: State<Boolean>,
    backPressed: () -> Boolean,
    warning: Boolean,
    isCaution: Boolean
) {
    Row(modifier = Modifier.background(commonBackground())) {
        IconButton(
            modifier = Modifier
                .padding(start = 15.dp)
                .size(25.dp)
                .align(Alignment.CenterVertically),
            onClick = {
                backPressed()
            }) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = commonTextColor()
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = title,
                style = TextStyle(
                    color = commonTextColor(),
                    fontSize = DpToSp(dp = 17.dp),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            ConstraintLayout(modifier = Modifier.fillMaxWidth(1f)) {
                val (symbolRef, warningRef, cautionRef) = createRefs()
                val textSize = DpToSp(10.dp)
                Text(
                    text = coinSymbol,
                    style = TextStyle(
                        color = commonTextColor(),
                        fontSize = DpToSp(dp = 13.dp),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .wrapContentWidth()
                        .constrainAs(symbolRef) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        },
                    maxLines = 1
                )

                if (warning) {
                    Text(
                        text = "유",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = textSize,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.W600
                        ),
                        modifier = Modifier
                            .padding(start = 3.dp)
                            .constrainAs(warningRef) {
                                start.linkTo(symbolRef.end)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                            .background(color = commonRiseColor())
                            .padding(horizontal = 3.dp)
                    )
                }

                if (isCaution) {
                    Text(
                        text = "주",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = textSize,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.W600
                        ),
                        modifier = Modifier
                            .padding(start = if (warning) 20.dp else 3.dp)
                            .constrainAs(cautionRef) {
                                start.linkTo(symbolRef.end)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                            .background(color = Color(0xffF2AD24))
                            .padding(horizontal = 3.dp)
                    )
                }
            }
        }

        IconButton(
            modifier = Modifier
                .padding(end = 15.dp)
                .size(25.dp)
                .align(Alignment.CenterVertically),
            onClick = {
                updateIsFavorite()
                if (isFavorite.value) {
                    showToast("관심목록에 추가되었습니다.")
                } else {
                    showToast("관심목록에서 삭제되었습니다.")
                }
            }) {
            Icon(
                painter = if (isFavorite.value) painterResource(R.drawable.img_filled_star)
                else painterResource(R.drawable.img_empty_star),
                contentDescription = null,
                tint = if (!isFavorite.value) commonTextColor() else Color.Unspecified
            )
        }
    }
}

@Composable
fun CoinDetailPriceSection(
    price: String,
    priceTextColor: Color,
    fluctuateRate: String,
    fluctuatePrice: String,
    symbol: String,
    btcPrice: BigDecimal,
    market: String,
    cautionMessageList: List<String>,
    lineChartDataList: List<Float> = emptyList(),
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(commonBackground())
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
            ) {
                Row(modifier = Modifier.padding(20.dp, 0.dp, 0.dp, 0.dp)) {
                    Text(
                        text = if (price == "0") "" else price,
                        style = TextStyle(color = priceTextColor, fontSize = DpToSp(dp = 27.dp))
                    )
                    if (price != "0" && !market.isTradeCurrencyKrw()) {
                        Text(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .align(Alignment.Bottom),
                            text = btcPrice.multiply(BigDecimal(price)).formattedStringForBtc()
                                .plus(" KRW"),
                            style = TextStyle(
                                color = Color.Gray,
                                fontSize = DpToSp(dp = 11.dp)
                            )
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(20.dp, 7.dp, 0.dp, 10.dp)
                        .wrapContentHeight()
                ) {
                    Text(
                        text = stringResource(id = R.string.netChange), modifier = Modifier
                            .wrapContentWidth()
                            .padding(0.dp, 0.dp, 10.dp, 0.dp),
                        style = TextStyle(
                            color = commonTextColor(),
                            fontSize = DpToSp(13.dp)
                        )
                    )
                    Text(
                        text = if (price == "0") "" else fluctuateRate,
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        style = TextStyle(color = priceTextColor, fontSize = DpToSp(13.dp)),
                        maxLines = 1
                    )
                    AutoSizeText(
                        text = if (price == "0") "" else fluctuatePrice,
                        modifier = Modifier
                            .padding(start = 30.dp)
                            .align(Alignment.CenterVertically),
                        textStyle = TextStyle(
                            textAlign = TextAlign.Start,
                            fontSize = DpToSp(13.dp)
                        ),
                        color = priceTextColor
                    )
                }
            }

            if (lineChartDataList.isNotEmpty()) {
                CoinDetailLineChart(
                    data = lineChartDataList,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .height(60.dp)
                        .width(120.dp)
                        .padding(end = 10.dp)
                )
            }
        }
        if (cautionMessageList.isNotEmpty()) {
            AutoScrollingBanner(cautionMessageList)
        }
    }
}

@Composable
fun CoinDetailLineChart(data: List<Float>, modifier: Modifier) {
    val context = LocalContext.current
    val lineChart = remember { LineChart(context) }
    val chartIsReady = remember { mutableStateOf(false) }

    LaunchedEffect(data) {
        if (data.isNotEmpty()) {
            val entries = data.mapIndexed { index, value -> Entry(index.toFloat(), value) }
            val lastValue = data.lastOrNull() ?: 0f
            val firstValue = data.firstOrNull() ?: 0f
            val isUp = lastValue >= firstValue

            val dataSet = LineDataSet(entries, "").apply {
                color = ContextCompat.getColor(
                    context,
                    if (isUp) R.color.increase_color else R.color.decrease_color // 상승 시 파랑, 하락 시 빨강
                )
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawFilled(true)
                fillDrawable = ContextCompat.getDrawable(
                    context,
                    if (isUp) R.drawable.gradient_red else R.drawable.gradient_blue
                )

                setDrawCircles(false)
                setDrawValues(false)
                lineWidth = 1f
            }

            // 데이터 범위에 맞게 최소/최대 설정
            lineChart.xAxis.axisMinimum = entries.minOf { it.x + 1 }
            lineChart.xAxis.axisMaximum = entries.maxOf { it.x + 1 }
            lineChart.axisLeft.axisMinimum = entries.minOf { it.y }
            lineChart.axisLeft.axisMaximum = entries.maxOf { it.y }

            lineChart.data = LineData(dataSet)

            val currentPrice = data.lastOrNull() ?: return@LaunchedEffect

            // 점선 Limit Line 추가
            val limitLine = LimitLine(currentPrice, "").apply {
                lineWidth = 1f
                enableDashedLine(10f, 10f, 0f)  // 점선 스타일 설정
                lineColor = ContextCompat.getColor(context, R.color.hint_text_color)  // 점선 색상
            }

            // 기존 Limit Line 제거 후 새로 추가
            lineChart.axisLeft.removeAllLimitLines()
            lineChart.axisLeft.addLimitLine(limitLine)

            // 스케일 자동 조정 (invalidate 전에 적용)
            lineChart.isAutoScaleMinMaxEnabled = true
            lineChart.invalidate()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            if(chartIsReady.value) {
                TextView(context)
            } else {
                lineChart.apply {
                    legend.isEnabled = false
                    setTouchEnabled(false)
                    setPinchZoom(false)
                    description.isEnabled = false

                    // X축 설정
                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        setDrawGridLines(false)
                        setDrawLabels(false)
                        isEnabled = false
                        setAvoidFirstLastClipping(true)
                    }

                    // Y축 설정
                    axisLeft.apply {
                        setDrawGridLines(false)
                        setDrawLabels(false)
                        setDrawAxisLine(false)
                        isEnabled = true
                        spaceTop = 0f
                        spaceBottom = 0f
                    }

                    axisRight.isEnabled = false

                    // 여백 제거
                    setExtraOffsets(0f, 10f, 0f, 10f)
                    setViewPortOffsets(0f, 10f, 0f, 10f)

                    // 배경 설정
                    setBackgroundColor(ContextCompat.getColor(context, R.color.background))
                }
            }
        }
    )
}

@Composable
fun AutoScrollingBanner(items: List<String>) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val itemSize = 25.dp
    val density = LocalDensity.current
    val itemSizePx = with(density) { itemSize.toPx() }
    val itemsScrollCount = items.size

    var currentIndex = remember { 0 }

    LaunchedEffect(Unit) {
        if (items.size <= 1) return@LaunchedEffect

        while (true) {
            delay(3000) // 3초마다 다음 아이템으로 이동
            coroutineScope.launch {
                listState.animateScrollBy(
                    value = itemSizePx,
                    animationSpec = tween(durationMillis = 1500)
                )

                if (currentIndex + 1 == itemsScrollCount) {
                    listState.scrollToItem(0)
                    currentIndex = 0
                } else {
                    currentIndex += 1
                }
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .height(25.dp)
            .background(commonBackground()),
        userScrollEnabled = false
    ) {
        items(items + items.first()) { item ->
            Row(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .height(25.dp)
            ) {
                Text(
                    text = "주의",
                    modifier = Modifier
                        .padding(start = 20.dp, end = 10.dp)
                        .align(Alignment.CenterVertically),
                    style = TextStyle(
                        color = commonRiseColor(),
                        fontWeight = FontWeight.W700,
                        textAlign = TextAlign.Center
                    ),
                    fontSize = DpToSp(13.dp),
                )

                Text(
                    text = item,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .align(Alignment.CenterVertically),
                    style = TextStyle(color = commonTextColor(), textAlign = TextAlign.Center),
                    fontSize = DpToSp(13.dp)
                )
            }
        }
    }
}