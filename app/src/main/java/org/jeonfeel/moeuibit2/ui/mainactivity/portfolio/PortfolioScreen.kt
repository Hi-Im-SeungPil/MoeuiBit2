package org.jeonfeel.moeuibit2.ui.mainactivity.portfolio

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitPortfolioWebSocket
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.ui.custom.PortfolioAutoSizeText
import org.jeonfeel.moeuibit2.ui.mainactivity.exchange.getButtonBackgroundColor
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
            if (exchangeViewModel.loadingComplete.value) {
                UserHoldCoinLazyColumn(exchangeViewModel)
            }
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
    val portfolioOrderState = remember {
        mutableStateOf(-1)
    }
//    val orderByNameTextInfo = getTextColors(buttonNum = 1, textState = portfolioOrderState.value)
//    val orderByRateTextInfo = getTextColors(buttonNum = 2, textState = portfolioOrderState.value)
    val orderByNameTextInfo = remember {
        mutableStateOf(
            listOf(
                "이름↓↑",
                Color.Black,
                Color.White
            )
        )
    }
    val orderByRateTextInfo = remember {
        mutableStateOf(
            listOf(
                "수익률↓↑",
                Color.Black,
                Color.White
            )
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
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
        Row(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text = orderByNameTextInfo.value[0] as String,
                modifier = Modifier
                    .weight(1f)
                    .background(color = orderByNameTextInfo.value[2] as Color)
                    .clickable() {
                        if (exchangeViewModel.isPortfolioSocketRunning) {
                            if (portfolioOrderState.value != 0 && portfolioOrderState.value != 1) {
                                portfolioOrderState.value = 0
                            } else if (portfolioOrderState.value == 0) {
                                portfolioOrderState.value = 1
                            } else {
                                portfolioOrderState.value = -1
                            }
                            orderByNameTextInfo.value = getTextColors(1,portfolioOrderState.value)
                            orderByRateTextInfo.value = getTextColors(2,portfolioOrderState.value)
                            exchangeViewModel.sortUserHoldCoin(portfolioOrderState.value)
                        }
                    },
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                style = TextStyle(color = orderByNameTextInfo.value[1] as Color)
            )
            Text(
                text = orderByRateTextInfo.value[0] as String,
                modifier = Modifier
                    .weight(1f)
                    .background(color = orderByRateTextInfo.value[2] as Color)
                    .clickable {
                        if (exchangeViewModel.isPortfolioSocketRunning) {
                            if (portfolioOrderState.value != 2 && portfolioOrderState.value != 3) {
                                portfolioOrderState.value = 2
                            } else if (portfolioOrderState.value == 2) {
                                portfolioOrderState.value = 3
                            } else {
                                portfolioOrderState.value = -1
                            }
                            orderByNameTextInfo.value = getTextColors(1,portfolioOrderState.value)
                            orderByRateTextInfo.value = getTextColors(2,portfolioOrderState.value)
                            exchangeViewModel.sortUserHoldCoin(portfolioOrderState.value)
                        }
                    },
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                style = TextStyle(color = orderByRateTextInfo.value[1] as Color)
            )
        }
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
                    fontSize = 14.sp,
                    textAlign = TextAlign.End
                ),
                color = textColor
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
                    fontSize = 14.sp,
                    textAlign = TextAlign.End
                ),
                color = textColor
            )
        }
    }
}

@Composable
fun UserHoldCoinLazyColumn(exchangeViewModel: ExchangeViewModel) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        val userHoldCoinDtoList = exchangeViewModel.userHoldCoinDtoList
        item {
            PortfolioMain(exchangeViewModel)
        }
        itemsIndexed(items = userHoldCoinDtoList) { _, item ->
            if (userHoldCoinDtoList.isNotEmpty()) {
                val koreanName = item.myCoinsKoreanName
                val symbol = item.myCoinsSymbol
                val purchaseAmount =
                    round(item.myCoinsQuantity * item.myCoinsBuyingAverage).toLong()
                val evaluationAmount = round(item.myCoinsQuantity * item.currentPrice).toLong()
                val purchaseAverage = item.myCoinsBuyingAverage
                val valuationGainOrLoss = evaluationAmount - purchaseAmount
                val coinQuantity = Calculator.getDecimalDecimalFormat().format(item.myCoinsQuantity)
                val aReturn =
                    if (((item.currentPrice - item.myCoinsBuyingAverage) / item.myCoinsBuyingAverage * 100).isNaN()) {
                        0
                    } else {
                        String.format(
                            "%.2f",
                            (item.currentPrice - item.myCoinsBuyingAverage) / item.myCoinsBuyingAverage * 100
                        )
                    }
                val color = if (valuationGainOrLoss > 0) {
                    Color.Red
                } else if (valuationGainOrLoss < 0) {
                    Color.Blue
                } else {
                    Color.Black
                }

                UserHoldCoinLazyColumnItem(
                    koreanName,
                    symbol,
                    Calculator.valuationGainOrLossDecimal(valuationGainOrLoss.toDouble()),
                    aReturn.toString().plus("%"),
                    coinQuantity,
                    Calculator.tradePriceCalculatorForChart(purchaseAverage),
                    Calculator.tradePriceCalculatorForChart(purchaseAmount.toDouble()),
                    Calculator.getDecimalFormat().format(evaluationAmount),
                    color
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
    evaluationAmount: String,
    color: Color
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .drawWithContent {
            drawContent()
            clipRect {
                val strokeWidth = 2f
                val y = size.height
                drawLine(
                    brush = SolidColor(Color.Gray),
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
                        text = valuationGainOrLoss,
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 0.dp, 4.dp)
                            .weight(1f, true),
                        textStyle = TextStyle(textAlign = TextAlign.End, fontSize = 15.sp),
                        color = color
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "수익률", modifier = Modifier.wrapContentWidth())
                    PortfolioAutoSizeText(
                        text = aReturn,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, true),
                        textStyle = TextStyle(textAlign = TextAlign.End, fontSize = 15.sp),
                        color = color
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            UserHoldCoinLazyColumnItemContent(coinQuantity, symbol, "보유수량")
            Log.d(symbol, coinQuantity.toString())
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
fun RowScope.UserHoldCoinLazyColumnItemContent(
    text1: String,
    text2: String,
    text3: String,
    color: Color = Color.Black
) {
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
//            PortfolioAutoSize
            AutoSizeText(
                text = text1,
                modifier = Modifier.weight(1f, true),
                textStyle = TextStyle(textAlign = TextAlign.End, fontSize = 15.sp)
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
private fun getTextColors(buttonNum: Int, textState: Int): List<Any> {
    return when (buttonNum) {
        1 -> {
            when (textState) {
                0 -> {
                    listOf("이름↓", Color.White, Color.Magenta)
                }
                1 -> {
                    listOf("이름↑", Color.White, Color.Magenta)
                }
                else -> {
                    listOf(
                        "이름↓↑",
                        Color.Black,
                        Color.White
                    )
                }
            }
        }
        2 -> {
            when (textState) {
                2 -> {
                    listOf("수익률↓", Color.White, Color.Magenta)
                }
                3 -> {
                    listOf("수익률↑", Color.White, Color.Magenta)
                }
                else -> {
                    listOf(
                        "수익률↓↑",
                        Color.Black,
                        Color.White
                    )
                }
            }
        }
        else -> {
            listOf("수익률아래", Color.White, Color.Black)
        }
    }
}


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