package org.jeonfeel.moeuibit2.ui.mainactivity.portfolio

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitPortfolioWebSocket
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.ui.custom.PortfolioAutoSizeText
import org.jeonfeel.moeuibit2.util.Calculator
import org.jeonfeel.moeuibit2.util.OnLifecycleEvent
import org.jeonfeel.moeuibit2.viewmodel.ExchangeViewModel
import kotlin.math.round

@Composable
fun PortfolioScreen(exchangeViewModel: ExchangeViewModel = viewModel()) {

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> UpBitPortfolioWebSocket.onPause()
            Lifecycle.Event.ON_RESUME -> {
                exchangeViewModel.getUserHoldCoins()
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth(),
                backgroundColor = colorResource(id = R.color.design_default_color_background),
            ) {
                Text(
                    text = "투자 내역",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp, 0.dp, 0.dp, 0.dp)
                        .fillMaxHeight()
                        .wrapContentHeight(),
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
            PortfolioMain(exchangeViewModel)
        }
    }
}

@Composable
fun PortfolioMain(exchangeViewModel: ExchangeViewModel = viewModel()) {
    exchangeViewModel.getUserSeedMoney()
    val totalValuedAssets = Calculator.getDecimalFormat()
        .format(round(exchangeViewModel.totalValuedAssets.value).toLong())
    val totalPurchaseValue =
        Calculator.getDecimalFormat().format(round(exchangeViewModel.totalPurchase.value).toLong())
    val userSeedMoney = Calculator.getDecimalFormat().format(exchangeViewModel.userSeedMoney.value)
    val totalHoldings = Calculator.getDecimalFormat()
        .format(round(exchangeViewModel.userSeedMoney.value + exchangeViewModel.totalValuedAssets.value).toLong())
    val valuationGainOrLoss = Calculator.getDecimalFormat()
        .format(round(exchangeViewModel.totalValuedAssets.value - exchangeViewModel.totalPurchase.value).toLong())
    val aReturn = if (exchangeViewModel.totalValuedAssets.value == 0.0) {
        "0"
    } else {
        String.format(
            "%.2f",
            (exchangeViewModel.totalValuedAssets.value - exchangeViewModel.totalPurchase.value) / exchangeViewModel.totalPurchase.value * 100
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Text(
            text = "보유 자산",
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 20.dp, 0.dp, 20.dp)
                .wrapContentHeight(),
            style = TextStyle(
                color = Color.Black,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .drawWithContent {
                drawContent()
                clipRect {
                    val strokeWidth = 2f
                    val y = size.height
                    drawLine(
                        brush = SolidColor(Color.LightGray),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Square,
                        start = Offset.Zero.copy(y = y),
                        end = Offset(x = size.width, y = y)
                    )
                }
            }) {
            PortfolioMainItem(
                text1 = "보유 KRW",
                text2 = userSeedMoney,
                "총매수",
                totalPurchaseValue,
                "총평가",
                totalValuedAssets,
                round(exchangeViewModel.totalValuedAssets.value - exchangeViewModel.totalPurchase.value).toLong()
            )

            PortfolioMainItem(
                text1 = "총 보유자산",
                text2 = totalHoldings,
                "평가손익",
                valuationGainOrLoss,
                "수익률",
                aReturn.plus("%"),
                round(exchangeViewModel.totalValuedAssets.value - exchangeViewModel.totalPurchase.value).toLong()
            )
        }
        UserHoldCoinLazyColumn(exchangeViewModel)
    }

}


@Composable
fun RowScope.PortfolioMainItem(
    text1: String,
    text2: String,
    text3: String,
    text4: String,
    text5: String,
    text6: String,
    colorStandard: Long
) {
    val textColor = getReturnTextColor(colorStandard, text5)
    Column(
        modifier = Modifier
            .padding()
            .wrapContentHeight()
            .weight(2f, true)
    ) {
        Text(
            text = text1,
            modifier = Modifier
                .padding(8.dp, 0.dp, 0.dp, 0.dp)
                .wrapContentHeight()
                .fillMaxWidth(),
            style = TextStyle(
                color = Color.Black,
                fontSize = 18.sp,
            )
        )

        PortfolioAutoSizeText(
            text = text2,
            modifier = Modifier
                .padding(8.dp, 5.dp, 8.dp, 0.dp)
                .wrapContentHeight()
                .fillMaxWidth(),
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 15.dp, 0.dp, 0.dp)
                .wrapContentHeight()
        ) {
            Text(
                text = text3,
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .wrapContentHeight()
                    .wrapContentWidth(),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 14.sp,
                )
            )
            PortfolioAutoSizeText(
                text = text4,
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .weight(1f, true)
                    .wrapContentHeight(),
                textStyle = TextStyle(
                    color = textColor,
                    fontSize = 14.sp,
                    textAlign = TextAlign.End
                )
            )
        }
        Row(
            modifier = Modifier
                .padding(0.dp, 8.dp, 0.dp, 25.dp)
                .wrapContentHeight()
        ) {
            Text(
                text = text5,
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .wrapContentHeight()
                    .wrapContentWidth(),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 14.sp,
                )
            )
            PortfolioAutoSizeText(
                text = text6,
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .weight(1f, true)
                    .wrapContentHeight(),
                textStyle = TextStyle(
                    color = textColor,
                    fontSize = 14.sp,
                    textAlign = TextAlign.End
                )
            )
        }
    }
}

@Composable
fun UserHoldCoinLazyColumn(exchangeViewModel: ExchangeViewModel) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        val userHoldCoinDtoList = exchangeViewModel.userHoldCoinDtoList
        itemsIndexed(items = userHoldCoinDtoList) { _, item ->
            if(userHoldCoinDtoList.isNotEmpty()) {
                val koreanName = item.myCoinsKoreanName
                val symbol = item.myCoinsSymbol
                val purchaseAmount = item.myCoinsQuantity * item.myCoinsBuyingAverage
                val evaluationAmount = item.myCoinsQuantity * item.currentPrice
                val purchaseAverage = item.myCoinsBuyingAverage
                val valuationGainOrLoss = evaluationAmount - purchaseAmount
                val coinQuantity = item.myCoinsQuantity
                val aReturn = if(((item.currentPrice - item.myCoinsBuyingAverage) / item.myCoinsBuyingAverage * 100).isNaN()) {
                    0
                } else{
                    (item.currentPrice - item.myCoinsBuyingAverage) / item.myCoinsBuyingAverage * 100
                }

                UserHoldCoinLazyColumnItem(
                    koreanName,
                    symbol,
                    valuationGainOrLoss.toString(),
                    aReturn.toString(),
                    coinQuantity.toString(),
                    purchaseAverage.toString(),
                    purchaseAmount.toString(),
                    evaluationAmount.toString()
                )
            }
        }
    }
}

@Composable
fun UserHoldCoinLazyColumnItem(
    coinKoreanName: String,
    symbol: String,
    valuationGainOrLoss: String,
    aReturn: String,
    coinQuantity: String,
    purchaseAverage: String,
    purchaseAmount: String,
    evaluationAmount: String
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .drawWithContent {
            drawContent()
            clipRect {
                val strokeWidth = 1f
                val y = size.height
                drawLine(
                    brush = SolidColor(Color.LightGray),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Square,
                    start = Offset.Zero.copy(y = y),
                    end = Offset(x = size.width, y = y)
                )
            }
        }) {
        Row(
            modifier = Modifier
                .padding(0.dp, 8.dp, 0.dp, 0.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text(
                    text = coinKoreanName,
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 0.dp, 1.dp)
                        .fillMaxWidth(),
                    style = TextStyle(
                        color = colorResource(id = R.color.C0F0F5C),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = symbol,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(
                        color = colorResource(id = R.color.C0F0F5C),
                        fontSize = 17.sp
                    ),
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "평가손익", modifier = Modifier.wrapContentWidth())
                    PortfolioAutoSizeText(
                        text = "valuationGainOrLoss",
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 0.dp, 4.dp)
                            .weight(1f, true),
                        textStyle = TextStyle(textAlign = TextAlign.End)
                    )
//                    PortfolioAutoSizeText(
//                        text = "text1",
//                        modifier = Modifier.weight(1f, true),
//                        textStyle = TextStyle(textAlign = TextAlign.End)
//                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "수익률", modifier = Modifier.wrapContentWidth())
//                    PortfolioAutoSizeText(
//                        text = aReturn,
//                        modifier = Modifier.fillMaxWidth().weight(1f, true),
//                        textStyle = TextStyle(textAlign = TextAlign.End)
//                    )
                }

            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            UserHoldCoinLazyColumnItemContent(coinQuantity, symbol, "보유수량")
            UserHoldCoinLazyColumnItemContent(purchaseAverage, "KRW", "매수평균가")
        }
        Row(
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 8.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            UserHoldCoinLazyColumnItemContent(evaluationAmount, "KRW", "평가금액")
            UserHoldCoinLazyColumnItemContent(purchaseAmount, "KRW", "매수금액")
        }
    }
}

@Composable
fun RowScope.UserHoldCoinLazyColumnItemContent(text1: String, text2: String, text3: String) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 2.dp)
        ) {
            PortfolioAutoSizeText(
                text = text1,
                modifier = Modifier.weight(1f, true),
                textStyle = TextStyle(textAlign = TextAlign.End)
            )
            Text(
                text = "  ".plus(text2),
                modifier = Modifier.wrapContentWidth(),
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = text3,
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(color = Color.Gray),
            textAlign = TextAlign.End
        )
    }
}


//@Composable
//@Preview(showBackground = true)
//fun preview() {
//    UserHoldCoinLazyColumnItem()
//}

@Composable
fun getReturnTextColor(colorStandard: Long, text5: String): Color {
    return if (text5 == "수익률") {
        when {
            colorStandard < 0 -> {
                Color.Blue
            }
            colorStandard > 0 -> {
                Color.Red
            }
            else -> {
                Color.Black
            }
        }
    } else {
        Color.Black
    }
}