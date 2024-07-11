package org.jeonfeel.moeuibit2.ui.coindetail.newS

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skydoves.landscapist.glide.GlideImage
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.coinImageUrl
import org.jeonfeel.moeuibit2.ui.coindetail.newScreen.NewCoinDetailViewModel
import org.jeonfeel.moeuibit2.ui.coindetail.newScreen.order.NewOrderScreen
import org.jeonfeel.moeuibit2.ui.coindetail.newScreen.order.OrderScreenRoute
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import org.jeonfeel.moeuibit2.utils.secondDecimal

@Composable
fun NewCoinDetailScreen(
    viewModel: NewCoinDetailViewModel = hiltViewModel(),
    market: String,
    warning: Boolean
) {
    val state = rememberCoinDetailStateHolder(context = LocalContext.current)

    AddLifecycleEvent(
        onCreateAction = {
            viewModel.init(market)
        },
        onPauseAction = {

        },
        onResumeAction = {

        }
    )

    Column(modifier = Modifier.fillMaxSize()) {
        NewCoinDetailTopAppBar(
            coinSymbol = market.substring(4),
            title = state.getCoinDetailTitle(
                koreanCoinName = viewModel.koreanCoinName.value,
                warning = warning
            )
        )
        CoinDetailPriceSection(
            fluctuateRate = viewModel.coinTicker.value?.signedChangeRate?.secondDecimal() ?: "",
            fluctuatePrice = viewModel.coinTicker.value?.signedChangePrice?.toString() ?: "",
            price = state.getCoinDetailPrice(
                viewModel.coinTicker.value?.tradePrice?.toDouble() ?: 0.0,
                viewModel.rootExchange ?: "",
                market
            ),
            symbol = market.substring(4),
            priceTextColor = state.getCoinDetailPriceTextColor(
                viewModel.coinTicker.value?.signedChangeRate?.secondDecimal()?.toDouble() ?: 0.0
            )
        )
        OrderScreenRoute(market = market)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCoinDetailTopAppBar(
    coinSymbol: String,
    title: String
) {
    CenterAlignedTopAppBar(
        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer),
        title = {
            Column {
                Text(
                    text = title,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = DpToSp(dp = 22.dp),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = coinSymbol)
            }
        },
        navigationIcon = {
            IconButton(onClick = {

            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        },
        actions = {

        }
    )
}

@Composable
fun CoinDetailPriceSection(
    price: String,
    priceTextColor: Color,
    fluctuateRate: String,
    fluctuatePrice: String,
    symbol: String,
) {
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(0.dp, 10.dp, 0.dp, 0.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
            ) {
                Row(modifier = Modifier.padding(20.dp, 0.dp, 0.dp, 0.dp)) {
                    Text(
                        text = price,
                        style = TextStyle(color = priceTextColor, fontSize = DpToSp(dp = 27.dp))
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(20.dp, 0.dp, 0.dp, 10.dp)
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.Bottom)
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
                            .align(Alignment.CenterVertically)
                            .weight(1f),
                        style = TextStyle(color = priceTextColor, fontSize = DpToSp(13.dp)),
                        maxLines = 1
                    )
                    AutoSizeText(
                        text = fluctuatePrice,
                        modifier = Modifier.weight(1f),
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
                    .padding(0.dp, 0.dp, 0.dp, 10.dp)
                    .weight(1f),
                contentScale = ContentScale.FillHeight,
                error = ImageBitmap.imageResource(R.drawable.img_glide_placeholder),
            )
        }
        // 코인 상세화면 주문 ,차트 ,정보 버튼
//        CoinDetailMainTabRow(navController = navController)
//        TabRowMainNavigation(navController)
    }
}