package org.jeonfeel.moeuibit2.ui.coindetail.newS

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.coinImageUrl
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.Caution
import org.jeonfeel.moeuibit2.ui.coindetail.CoinDetailMainTabRow
import org.jeonfeel.moeuibit2.ui.coindetail.TabRowMainNavigation
import org.jeonfeel.moeuibit2.ui.coindetail.newScreen.NewCoinDetailViewModel
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringForBtc
import org.jeonfeel.moeuibit2.utils.isTradeCurrencyKrw
import org.jeonfeel.moeuibit2.utils.secondDecimal
import java.math.BigDecimal
import kotlin.reflect.KFunction0
import kotlin.reflect.KFunction1

@Composable
fun NewCoinDetailScreen(
    viewModel: NewCoinDetailViewModel = hiltViewModel(),
    market: String,
    warning: Boolean,
    navController: NavHostController,
    caution: Caution?
) {
    val state = rememberCoinDetailStateHolder(context = LocalContext.current, caution = caution)
    val cautionMessageList = remember {
        state.getCautionMessageList(viewModel.coinTicker.value?.signedChangeRate ?: 0.0)
    }

    AddLifecycleEvent(
        onCreateAction = {
            viewModel.init(market)
        },
        onPauseAction = {
            viewModel.onPause()
        },
        onResumeAction = {
            viewModel.onResume(market)
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
        )
        CoinDetailMainTabRow(navController = state.navController)

        if (cautionMessageList.isNotEmpty()) {
            AutoScrollingBanner(cautionMessageList)
        }

        Box {
            TabRowMainNavigation(
                navHostController = state.navController,
                market = market,
                viewModel = viewModel
            )
        }
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
    Row(modifier = Modifier.background(Color.White)) {
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
                tint = Color.Black
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
                    color = Color.Black,
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
                        color = Color.Black,
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
                            .background(color = Color.Red)
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
                tint = Color.Unspecified
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
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
            ) {
                Row(modifier = Modifier.padding(20.dp, 0.dp, 0.dp, 0.dp)) {
                    Text(
                        text = price,
                        style = TextStyle(color = priceTextColor, fontSize = DpToSp(dp = 27.dp))
                    )
                    if (!market.isTradeCurrencyKrw()) {
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
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = DpToSp(13.dp)
                        )
                    )
                    Text(
                        text = fluctuateRate,
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        style = TextStyle(color = priceTextColor, fontSize = DpToSp(13.dp)),
                        maxLines = 1
                    )
                    AutoSizeText(
                        text = fluctuatePrice,
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
            // 코인 상세화면 코인 이미지
            GlideImage(
                imageModel = coinImageUrl.plus("$symbol.png"),
                modifier = Modifier
                    .padding(end = 20.dp)
                    .clip(CircleShape)
                    .size(55.dp)
                    .border(width = 1.dp, color = Color(0xFFE8E8E8), shape = CircleShape)
                    .background(Color.White),
                error = ImageBitmap.imageResource(R.drawable.img_glide_placeholder),
            )
        }
    }
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
            .background(Color(0x0DFF0000)),
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
                        color = Color.Red,
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
                    style = TextStyle(color = Color.Black, textAlign = TextAlign.Center),
                    fontSize = DpToSp(13.dp)
                )
            }
        }
    }
}