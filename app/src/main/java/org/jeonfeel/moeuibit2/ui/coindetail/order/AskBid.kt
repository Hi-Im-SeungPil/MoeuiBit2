package org.jeonfeel.moeuibit2.ui.coindetail.order

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.ui.custom.OrderScreenQuantityTextField
import org.jeonfeel.moeuibit2.util.Calculator
import org.jeonfeel.moeuibit2.viewmodel.coindetail.CoinDetailViewModel

@Composable
fun OrderScreenAskBid(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    Column(modifier = Modifier
        .fillMaxSize()) {
        OrderScreenTabs(coinDetailViewModel)
        Box(modifier = Modifier
            .padding(10.dp, 0.dp)
            .weight(1f)) {
            Column() {
                OrderScreenUserSeedMoney()
                OrderScreenQuantity()
                OrderScreenPrice(coinDetailViewModel)
                OrderScreenTotalPrice()
                OrderScreenButtons(coinDetailViewModel)
            }
        }
    }
}

@Composable
fun OrderScreenTabs(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    val selectedTab = coinDetailViewModel.askBidSelectedTab

    Row(Modifier
        .fillMaxWidth()
        .height(40.dp)) {
        TextButton(onClick = { selectedTab.value = 1 }, modifier = Modifier
            .background(getTabBackGround(selectedTab.value, 1))
            .weight(1f)
            .fillMaxHeight()) {
            Text(text = "매수", style = getTabTextStyle(selectedTab.value, 1))
        }
        TextButton(onClick = { selectedTab.value = 2 }, modifier = Modifier
            .background(getTabBackGround(selectedTab.value, 2))
            .weight(1f)
            .fillMaxHeight()) {
            Text(text = "매도", style = getTabTextStyle(selectedTab.value, 2))
        }
        TextButton(onClick = { selectedTab.value = 3 }, modifier = Modifier
            .background(getTabBackGround(selectedTab.value, 3))
            .weight(1f)
            .fillMaxHeight()) {
            Text(text = "거래내역", style = getTabTextStyle(selectedTab.value, 3))
        }
    }
}

@Composable
fun OrderScreenUserSeedMoney() {

    val textStyleBody1 = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold,
        textAlign = TextAlign.End,
        fontSize = 15.sp)
    val textStyle = remember { mutableStateOf(textStyleBody1) }

    Row(modifier = Modifier
        .padding(0.dp, 10.dp)
        .fillMaxWidth()
        .height(30.dp)
        .wrapContentHeight()
    ) {
        Text(text = "주문가능", modifier = Modifier.wrapContentWidth(), fontSize = 13.sp)
        AutoSizeText(text = "500,000,000,000",
            textStyle = textStyle.value,
            modifier = Modifier.weight(1f, true))
        Text(text = "  KRW",
            modifier = Modifier.wrapContentWidth(),
            fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
fun OrderScreenQuantity() {

    val text = remember { mutableStateOf("") }

    Row(modifier = Modifier
        .fillMaxWidth()
        .height(35.dp)) {
        Row(modifier = Modifier
            .weight(2f)
            .fillMaxHeight()
            .border(0.7.dp, Color.LightGray)
            .wrapContentHeight()) {
            Text(text = "수량",
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(8.dp, 0.dp, 8.dp, 0.dp),
                style = TextStyle(fontSize = 15.sp))
            OrderScreenQuantityTextField(textFieldValue = text,
                modifier = Modifier.weight(1f, true),
                placeholderText = "0",
                fontSize = 15.sp)
        }
        Text(text = "가능", modifier = Modifier.weight(1f))
    }
}

@Composable
fun OrderScreenPrice(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    Row(modifier = Modifier
        .padding(0.dp, 5.dp)
        .fillMaxWidth()
        .height(35.dp)
        .border(0.7.dp, Color.LightGray)
        .wrapContentHeight()) {
        Text(text = "가격", modifier = Modifier
            .wrapContentWidth()
            .padding(8.dp, 0.dp, 8.dp, 0.dp), style = TextStyle(fontSize = 15.sp))
        Text(text = Calculator.tradePriceCalculatorForChart(coinDetailViewModel.currentTradePriceState.value),
            modifier = Modifier.weight(1f, true),
            textAlign = TextAlign.End,
            style = TextStyle(fontSize = 15.sp))
        Text(text = "  KRW",
            modifier = Modifier
                .wrapContentWidth()
                .padding(0.dp, 0.dp, 8.dp, 0.dp),
            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold))
    }
}

@Composable
fun OrderScreenTotalPrice() {
    Row(modifier = Modifier
        .padding(0.dp, 0.dp, 0.dp, 5.dp)
        .fillMaxWidth()
        .height(35.dp)
        .border(0.7.dp, Color.LightGray)
        .wrapContentHeight()) {
        Text(text = "총액", modifier = Modifier
            .wrapContentWidth()
            .padding(8.dp, 0.dp, 8.dp, 0.dp), style = TextStyle(fontSize = 15.sp))
        Text(text = "1,000,000",
            modifier = Modifier.weight(1f, true),
            textAlign = TextAlign.End,
            style = TextStyle(fontSize = 15.sp))
        Text(text = "  KRW",
            modifier = Modifier
                .wrapContentWidth()
                .padding(0.dp, 0.dp, 8.dp, 0.dp),
            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold))
    }
}

@Composable
fun OrderScreenButtons(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    val buttonBackground = getButtonsBackground(coinDetailViewModel.askBidSelectedTab.value)
    val buttonText = getButtonsText(coinDetailViewModel.askBidSelectedTab.value)

    Column(modifier = Modifier
        .fillMaxWidth()) {
        Row(modifier = Modifier
            .padding(0.dp, 5.dp)
            .fillMaxWidth()
            .height(40.dp)) {
            TextButton(onClick = {}, modifier = Modifier
                .padding(0.dp, 0.dp, 4.dp, 0.dp)
                .weight(1f)
                .background(color = Color.LightGray)
                .fillMaxHeight()) {
                Text(text = "초기화", style = TextStyle(color = Color.White) , fontSize = 18.sp)
            }
            TextButton(onClick = { }, modifier = Modifier
                .padding(4.dp, 0.dp, 0.dp, 0.dp)
                .weight(1f)
                .background(buttonBackground)
                .fillMaxHeight()) {
                Text(text = buttonText, style = TextStyle(color = Color.White), fontSize = 18.sp)
            }
        }

        TextButton(onClick = { }, modifier = Modifier
            .padding(0.dp, 5.dp)
            .fillMaxWidth()
            .background(buttonBackground)
            .height(40.dp)) {
            Text(text = "총액 지정하여 ".plus(buttonText), style = TextStyle(color = Color.White) , fontSize = 18.sp)
        }
    }
}

@Composable
fun getTabTextStyle(selectedTab: Int, tabNum: Int): TextStyle {
    return if (selectedTab == tabNum) {
        TextStyle(color = colorResource(id = R.color.C0F0F5C),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp)
    } else {
        TextStyle(color = Color.Gray, fontSize = 15.sp)
    }
}

@Composable
fun getTabBackGround(selectedTab: Int, tabNum: Int): Color {
    return if (selectedTab == tabNum) {
        colorResource(id = R.color.design_default_color_background)
    } else {
        colorResource(id = R.color.CF6F6F6)
    }
}

@Composable
fun getButtonsBackground(selectedTab: Int): Color {
    return if (selectedTab == 1) {
        colorResource(id = R.color.CEA3030)
    } else {
        colorResource(id = R.color.C3048EA)
    }
}

@Composable
fun getButtonsText(selectedTab: Int): String {
    return if (selectedTab == 1) {
        "매수"
    } else {
        "매도"
    }
}