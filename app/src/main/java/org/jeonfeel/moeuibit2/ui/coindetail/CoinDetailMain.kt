package org.jeonfeel.moeuibit2.ui.coindetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.skydoves.landscapist.glide.GlideImage
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.coinImageUrl
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.decreaseColor
import org.jeonfeel.moeuibit2.ui.theme.increaseColor
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator

@Composable
fun CoinDetailMain(
    currentPrice: Double = 100.0,
    symbol: String,
    coinDetailViewModel: CoinDetailViewModel = viewModel(),
) {
    val marketState = remember {
        Utils.getSelectedMarket(coinDetailViewModel.market)
    } // krw 인지 btc인지
    val curTradePrice = CurrentCalculator.tradePriceCalculator(currentPrice, marketState) // 현재가
    val curChangeRate =
        Calculator.signedChangeRateCalculator(coinDetailViewModel.coinOrder.coinDetailModel.signedChangeRate) // 전일대비
    val curChangePrice =
        Calculator.changePriceCalculator(
            coinDetailViewModel.coinOrder.coinDetailModel.signedChangePrice,
            marketState
        ) // 전일대비 가격
    val textColor = getTextColor(curChangeRate) // 전일대비, 전일대비 가격 색상
    val navController = rememberNavController()

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
                .height(70.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
            ) {
                Row(modifier = Modifier.padding(20.dp, 0.dp, 0.dp, 0.dp)) {
                    Text(
                        text = curTradePrice,
                        style = TextStyle(color = textColor, fontSize = DpToSp(dp = 27.dp))
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
                        text = curChangeRate.plus("%"),
                        modifier = Modifier.weight(1f),
                        style = TextStyle(color = textColor, fontSize = DpToSp(13.dp)),
                        maxLines = 1
                    )
                    AutoSizeText(
                        text = curChangePrice,
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(
                            textAlign = TextAlign.Start,
                            fontSize = DpToSp(13.dp)
                        ),
                        color = textColor
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
        CoinDetailMainTabRow(navController = navController)
//        TabRowMainNavigation(navController)
    }
}

// 전일대비에 따른 색상
@Composable
fun getTextColor(
    curChangeRate: String,
): Color {
    val curSignedChangeRate = curChangeRate.toDouble()
    return when {
        curSignedChangeRate > 0.0 -> {
            increaseColor()
        }

        curSignedChangeRate < 0.0 -> {
            decreaseColor()
        }

        else -> {
            MaterialTheme.colorScheme.onBackground
        }
    }
}