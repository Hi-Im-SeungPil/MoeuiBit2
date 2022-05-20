package org.jeonfeel.moeuibit2.ui.mainactivity.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitPortfolioWebSocket
import org.jeonfeel.moeuibit2.util.Calculator
import org.jeonfeel.moeuibit2.util.OnLifecycleEvent
import org.jeonfeel.moeuibit2.viewmodel.ExchangeViewModel
import kotlin.math.round

@Composable
fun PortfolioScreen(exchangeViewModel: ExchangeViewModel = viewModel()) {

    OnLifecycleEvent{ _, event ->
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
    val totalValuedAssets = Calculator.getDecimalFormat().format(round(exchangeViewModel.totalValuedAssets.value).toLong())
    val totalPurchaseValue = Calculator.getDecimalFormat().format(round(exchangeViewModel.totalPurchase.value).toLong())
    val userSeedMoney = Calculator.getDecimalFormat().format(exchangeViewModel.userSeedMoney.value)
    val totalHoldings = Calculator.getDecimalFormat().format(round(exchangeViewModel.userSeedMoney.value + exchangeViewModel.totalValuedAssets.value).toLong())
    val valuationGainOrLoss = Calculator.getDecimalFormat().format(round(exchangeViewModel.totalValuedAssets.value - exchangeViewModel.totalPurchase.value).toLong())
    val aReturn = String.format("%.2f",(exchangeViewModel.totalValuedAssets.value - exchangeViewModel.totalPurchase.value) / exchangeViewModel.totalPurchase.value * 100)
    Box(
        modifier = Modifier
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
            }
    ) {
        Column(modifier = Modifier.wrapContentHeight()) {
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
                .wrapContentHeight()) {
                PortfolioMainItem(
                    text1 = "보유 KRW",
                    text2 = userSeedMoney,
                    "총매수",
                    totalPurchaseValue,
                    "총평가",
                    totalValuedAssets)

                PortfolioMainItem(text1 = "총 보유자산",
                    text2 = totalHoldings,
                    "평가손익",
                    valuationGainOrLoss,
                    "수익률",
                    aReturn.plus("%"))
            }
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Magenta))
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
) {
    val textColor = getReturnTextColor(text6.dropLast(1))
    Column(modifier = Modifier
        .padding()
        .wrapContentHeight()
        .weight(1f, true)) {
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
        Text(
            text = text2,
            modifier = Modifier
                .padding(8.dp, 5.dp, 8.dp, 0.dp)
                .wrapContentHeight()
                .fillMaxWidth(),
            style = TextStyle(
                color = Color.Black,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Row(modifier = Modifier
            .padding(0.dp, 15.dp, 0.dp, 0.dp)
            .wrapContentHeight()) {
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
            Text(
                text = text4,
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .weight(1f, true)
                    .wrapContentHeight(),
                style = TextStyle(
                    color = textColor,
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.End
            )
        }

        Row(modifier = Modifier
            .padding(0.dp, 8.dp, 0.dp, 25.dp)
            .wrapContentHeight()) {
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
            Text(
                text = text6,
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .weight(1f, true)
                    .wrapContentHeight(),
                style = TextStyle(
                    color = textColor,
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun getReturnTextColor(aReturn: String): Color {
    return if(aReturn.toDoubleOrNull() != null) {
        when {
            aReturn.toDouble() < 0.0 -> {
                Color.Blue
            }
            aReturn.toDouble() > 0.0 -> {
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
