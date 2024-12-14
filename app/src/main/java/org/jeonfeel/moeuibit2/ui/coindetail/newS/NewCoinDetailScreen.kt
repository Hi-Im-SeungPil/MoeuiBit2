package org.jeonfeel.moeuibit2.ui.coindetail.newS

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
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
import androidx.navigation.NavHostController
import com.skydoves.landscapist.glide.GlideImage
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.coinImageUrl
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
    navController: NavHostController
) {
    val state = rememberCoinDetailStateHolder(context = LocalContext.current)

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

    Column(modifier = Modifier.fillMaxSize()) {
        NewCoinDetailTopAppBar(
            coinSymbol = market.substring(4),
            title = viewModel.koreanCoinName.value,
            showToast = state::showToast,
            updateIsFavorite = viewModel::updateIsFavorite,
            isFavorite = viewModel.isFavorite,
            backPressed = { navController.popBackStack() }
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
            market = market
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
}

@Composable
fun NewCoinDetailTopAppBar(
    coinSymbol: String,
    title: String,
    showToast: KFunction1<String, Unit>,
    updateIsFavorite: KFunction0<Unit>,
    isFavorite: State<Boolean>,
    backPressed: () -> Boolean
) {
    Row {
        IconButton(onClick = {
            backPressed()
        }) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Color.Black
            )
        }

        Column {
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
            Text(
                text = coinSymbol,
                style = TextStyle(
                    color = Color.Black,
                    fontSize = DpToSp(dp = 13.dp),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth(1f),
                maxLines = 1
            )
        }

        IconButton(onClick = {
            updateIsFavorite()
            if (isFavorite.value) {
                showToast("관심목록에 추가되었습니다.")
            } else {
                showToast("관심목록에서 삭제되었습니다.")
            }
        }) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = Color.Black
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
    market: String
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
                .wrapContentHeight()
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
                    .size(65.dp)
                    .padding(end = 20.dp),
                contentScale = ContentScale.Inside,
                error = ImageBitmap.imageResource(R.drawable.img_glide_placeholder),
            )
        }
        // 코인 상세화면 주문 ,차트 ,정보 버튼
    }
}