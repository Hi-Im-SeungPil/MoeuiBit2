package org.jeonfeel.moeuibit2.ui.coindetail.order

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.viewmodel.coindetail.CoinDetailViewModel

@Composable
fun OrderScreenAskBid(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    Column(modifier =Modifier
        .fillMaxSize()) {
        OrderScreenTabs()
        Box(modifier = Modifier
            .padding(10.dp,0.dp)
            .fillMaxSize()) {
            Column() {
                OrderScreenUserSeedMoney()
                OrderScreenQuantity()
                OrderScreenPrice()
            }
        }
    }
}

@Composable
fun OrderScreenTabs(selected: Int = 1) {
    Row(Modifier
        .fillMaxWidth()
        .height(35.dp)) {
        TextButton(onClick = { }, modifier = Modifier.weight(1f)) {
            Text(text = "매수")
        }
        TextButton(onClick = { }, modifier = Modifier.weight(1f)) {
            Text(text = "매도")
        }
        TextButton(onClick = { }, modifier = Modifier.weight(1f)) {
            Text(text = "거래내역")
        }
    }
}

@Composable
fun OrderScreenUserSeedMoney() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(25.dp)) {
        Text(text = "주문가능", modifier = Modifier.weight(0.8f))
        Text(text = "0 KRW", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
    }
}

@Composable
fun OrderScreenQuantity() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(25.dp)) {
        Row(modifier = Modifier
            .weight(2f)
            .border(0.7.dp, Color.LightGray)){
            Text(text = "수량" , modifier = Modifier.weight(1f))
            OrderScreenQuantityTextField(modifier = Modifier.weight(3f), placeholderText = "0")
        }
        Text(text = "가능", modifier = Modifier.weight(1f))
    }
}

@Composable
fun OrderScreenPrice() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(25.dp)) {
        Row(modifier = Modifier
            .weight(2f)
            .border(0.7.dp, Color.LightGray)){
            Text(text = "가격" , modifier = Modifier.weight(1f))
            Text(text = "1645", modifier = Modifier.weight(3f), textAlign = TextAlign.End)
        }
    }
}

@Composable
fun OrderScreenQuantityTextField(
    modifier: Modifier = Modifier,
    textFieldValue: MutableState<String> = mutableStateOf(""),
    placeholderText: String = "Placeholder",
    fontSize: TextUnit = MaterialTheme.typography.body2.fontSize,
) {
    BasicTextField(value = textFieldValue.value, onValueChange = {
        textFieldValue.value = it
    }, singleLine = true,
        textStyle = TextStyle(color = colorResource(id = R.color.C0F0F5C),
            fontSize = 17.sp),
        decorationBox = { innerTextField ->
            Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier) {
                    if (textFieldValue.value.isEmpty()) {
                        Text(
                            placeholderText,
                            style = LocalTextStyle.current.copy(
                                color = colorResource(id = R.color.C0F0F5C),
                                fontSize = fontSize,
                                textAlign = TextAlign.End
                            )
                        )
                    }
                    innerTextField()
                }
            }
        })
}

//@Composable
//@Preview(showBackground = true)
//fun prev() {
//    OrderScreenAskBid()
//}