package org.jeonfeel.moeuibit2.ui.coindetail

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.skydoves.landscapist.glide.GlideImage
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.viewmodels.CoinDetailViewModel
import org.jeonfeel.moeuibit2.constants.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.constants.coinImageUrl
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.ui.theme.decrease_color
import org.jeonfeel.moeuibit2.ui.theme.increase_color
import org.jeonfeel.moeuibit2.ui.custom.DpToSp
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator

@Composable
fun CoinDetailMain(
    currentPrice: Double = 100.0,
    symbol: String,
    coinDetailViewModel: CoinDetailViewModel = viewModel(),
) {
    val marketState = Utils.getSelectedMarket(coinDetailViewModel.market) // krw 인지 btc인지
    val curTradePrice = CurrentCalculator.tradePriceCalculator(currentPrice, marketState) // 현재가
    val curChangeRate =
        Calculator.signedChangeRateCalculator(coinDetailViewModel.coinDetailModel.signedChangeRate) // 전일대비
    val curChangePrice =
        Calculator.changePriceCalculator(coinDetailViewModel.coinDetailModel.signed_change_price,
            marketState) // 전일대비 가격
    val textColor = getTextColor(curChangeRate) // 전일대비, 전일대비 가격 색상
    val navController = rememberNavController()

    Column(modifier = Modifier
        .padding(0.dp, 10.dp, 0.dp, 0.dp)
        .fillMaxWidth()
        .wrapContentHeight()) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)) {
            Column(modifier = Modifier
                .weight(2f)
                .fillMaxHeight()) {
                Row(modifier = Modifier.padding(20.dp, 0.dp, 0.dp, 0.dp)) {
                    Text(text = curTradePrice,
                        style = TextStyle(color = textColor, fontSize = DpToSp(dp = 27.dp)))
                    if (!MoeuiBitDataStore.isKor && marketState == SELECTED_KRW_MARKET) {
                        Text(text = "= \$ ${
                            CurrentCalculator.krwToUsd(Utils.removeComma(curTradePrice)
                                .toDouble(),
                                MoeuiBitDataStore.usdPrice)
                        }",
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Bottom),
                            style = TextStyle(color = textColor,
                                fontSize = DpToSp(dp = 13.dp),
                                textAlign = TextAlign.End))
                    }
                }
                Row(modifier = Modifier
                    .padding(20.dp, 0.dp, 0.dp, 10.dp)
                    .fillMaxHeight()
                    .wrapContentHeight(Alignment.Bottom)
                ) {
                    Text(text = stringResource(id = R.string.netChange), modifier = Modifier
                        .wrapContentWidth()
                        .padding(0.dp, 0.dp, 10.dp, 0.dp))
                    Text(text = curChangeRate.plus("%"),
                        modifier = Modifier.weight(1f),
                        style = TextStyle(color = textColor),
                        maxLines = 1)
                    AutoSizeText(text = curChangePrice,
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(textAlign = TextAlign.Start, fontSize = 13.sp),
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
                placeHolder = ImageBitmap.imageResource(R.drawable.img_glide_placeholder),
            )
        }
        // 코인 상세화면 주문 ,차트 ,정보 버튼
        CoinDetailMainTabRow(navController = navController)
        TabRowMainNavigation(navController, coinDetailViewModel)
    }
}

// 전일대비에 따른 색상
fun getTextColor(
    curChangeRate: String,
): Color {
    val curSignedChangeRate = curChangeRate.toDouble()
    return when {
        curSignedChangeRate > 0.0 -> {
            increase_color
        }
        curSignedChangeRate < 0.0 -> {
            decrease_color
        }
        else -> {
            Color.Black
        }
    }
}