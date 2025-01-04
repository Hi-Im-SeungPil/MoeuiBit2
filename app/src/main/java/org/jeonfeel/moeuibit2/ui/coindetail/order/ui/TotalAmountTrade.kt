package org.jeonfeel.moeuibit2.ui.coindetail.order.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.main.coinsite.item.BYBIT_COLOR

@Composable
fun TotalAmountTradeBottomSheet(
    dialogState: State<Boolean>,
    hideSheet: () -> Unit,
) {
    val plusAmountButtonList = remember {
        arrayOf(1, 10, 100, 1_000)
    }

    if (dialogState.value) {
        Dialog(
            onDismissRequest = { hideSheet() }
        ) {
            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.White, shape = RoundedCornerShape(15.dp))
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.background(Color.White)) {
                    Text(
                        text = "총액 지정하여 매수",
                        modifier = Modifier
                            .align(alignment = Alignment.CenterHorizontally),
                        style = TextStyle(fontWeight = FontWeight.W600, fontSize = DpToSp(16.dp))
                    )
                    Item(
                        text = "보유",
                        value = "10,000,000",
                        symbol = "KRW"
                    )
                    Item(
                        text = "현재가",
                        value = "51,993",
                        symbol = "KRW"
                    )
                    Item(
                        text = "매수 수량",
                        value = "31,1331",
                        symbol = "BTC"
                    )

                    Divider(
                        Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .height(2.dp), color = Color(BYBIT_COLOR)
                    )

                    Row(modifier = Modifier.padding(top = 10.dp)) {
                        Text(
                            "총액",
                            modifier = Modifier
                                .padding(end = 15.dp)
                                .align(Alignment.CenterVertically)
                        )
                        TransparentTextField(TextFieldValue(), {})
                        Text(" KRW", modifier = Modifier.align(Alignment.CenterVertically))
                    }

                    Row(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        plusAmountButtonList.forEach {
                            Text(
                                text = "+${it}만",
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .weight(1f)
                                    .border(
                                        2.dp,
                                        color = Color.LightGray,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(vertical = 5.dp),
                                style = TextStyle(
                                    textAlign = TextAlign.Center,
                                    fontSize = DpToSp(10.dp),
                                    color = Color.Black
                                )
                            )
                        }
                        Text(
                            text = "+1억",
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    2.dp,
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(vertical = 5.dp),
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                                fontSize = DpToSp(10.dp),
                                color = Color.Black
                            )
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            "취소",
                            modifier = Modifier
                                .weight(2f)
                                .background(
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(999.dp)
                                )
                                .padding(vertical = 7.dp),
                            style = TextStyle(textAlign = TextAlign.Center, color = Color.White)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "매수",
                            modifier = Modifier
                                .weight(3f)
                                .background(color = Color.Red, shape = RoundedCornerShape(999.dp))
                                .padding(vertical = 7.dp),
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                                fontSize = DpToSp(14.dp),
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Item(
    text: String,
    value: String,
    symbol: String
) {
    Row(modifier = Modifier.padding(top = 10.dp)) {
        Text(
            text,
            modifier = Modifier
                .padding(end = 15.dp)
                .align(Alignment.CenterVertically)
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        AutoSizeText(
            text = value,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Text(
            " $symbol",
            modifier = Modifier.align(Alignment.CenterVertically),
            style = TextStyle(fontWeight = FontWeight.W600)
        )
    }
}

@Composable
fun RowScope.TransparentTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String = "총액을 입력해 주세요"
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .background(Color.Transparent)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                Box {
                    if (value.text.isEmpty()) {
                        androidx.compose.material3.Text(
                            text = placeholder,
                            modifier = Modifier.fillMaxWidth(),
                            style = TextStyle(color = Color.Gray, textAlign = TextAlign.End)
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
@Preview()
fun TotalAmountTradeBottomSheetPreview() {
    TotalAmountTradeBottomSheet(dialogState = remember { mutableStateOf(true) }, hideSheet = {})
}