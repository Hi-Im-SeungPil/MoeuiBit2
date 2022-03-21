package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Colors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.viewmodel.ExchangeViewModel

@Composable
fun SortButtons(exchangeViewModel: ExchangeViewModel = viewModel()) {
    val selectedButtonState = remember { mutableStateOf(-1) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .drawWithContent {
                drawContent()
                clipRect { // Not needed if you do not care about painting half stroke outside
                    val strokeWidth = Stroke.DefaultMiter
                    val y = size.height // - strokeWidth
                    // if the whole line should be inside component
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
        Text(modifier = Modifier.weight(1f), text = "")

        Button(onClick = {
            when {
                selectedButtonState.value != 0 && selectedButtonState.value != 1 -> {
                    selectedButtonState.value = 0
                }
                selectedButtonState.value == 0 -> {
                    selectedButtonState.value = 1
                }
                else -> {
                    selectedButtonState.value = -1
                }
            }
            sortList(exchangeViewModel, selectedButtonState.value)
        }, modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)) {
            when (selectedButtonState.value) {
                0 -> {
                    Text(text = "현재가↓", fontSize = 10.sp)
                }
                1 -> {
                    Text(text = "현재가↑", fontSize = 10.sp)
                }
                else -> {
                    Text(text = "현재가↓↑", fontSize = 10.sp)
                }
            }
        }

        Button(onClick = {
            when {
                selectedButtonState.value != 2 && selectedButtonState.value != 3 -> {
                    selectedButtonState.value = 2
                }
                selectedButtonState.value == 2 -> {
                    selectedButtonState.value = 3
                }
                else -> {
                    selectedButtonState.value = -1
                }
            }
            sortList(exchangeViewModel, selectedButtonState.value)
        }, modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)) {
            when (selectedButtonState.value) {
                2 -> {
                    Text(text = "전일대비↓", fontSize = 10.sp, maxLines = 1)
                }
                3 -> {
                    Text(text = "전일대비↑", fontSize = 10.sp, maxLines = 1)
                }
                else -> {
                    Text(text = "전일대비↓↑", fontSize = 10.sp, maxLines = 1)
                }
            }
        }

        Button(onClick = {
            when {
                selectedButtonState.value != 4 && selectedButtonState.value != 5 -> {
                    selectedButtonState.value = 4
                }
                selectedButtonState.value == 4 -> {
                    selectedButtonState.value = 5
                }
                else -> {
                    selectedButtonState.value = -1
                }
            }
            sortList(exchangeViewModel, selectedButtonState.value)
        }, modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)) {
            when (selectedButtonState.value) {
                4 -> {
                    Text(text = "거래량↓", fontSize = 10.sp)
                }
                5 -> {
                    Text(text = "거래량↑", fontSize = 10.sp)
                }
                else -> {
                    Text(text = "거래량↓↑", fontSize = 10.sp)
                }
            }
        }
    }
}

fun sortList(exchangeViewModel: ExchangeViewModel, sortStandard: Int) {
    exchangeViewModel.isSocketRunning = false

    when (sortStandard) {
        0 -> {
            exchangeViewModel.krwExchangeModelList.sortByDescending { element ->
                element.tradePrice
            }
            exchangeViewModel.preItemArray.sortByDescending { element ->
                element.tradePrice
            }
        }
        1 -> {
            exchangeViewModel.krwExchangeModelList.sortBy { element ->
                element.tradePrice
            }
            exchangeViewModel.preItemArray.sortBy { element ->
                element.tradePrice
            }
        }
        2 -> {
            exchangeViewModel.krwExchangeModelList.sortByDescending { element ->
                element.signedChangeRate
            }
            exchangeViewModel.preItemArray.sortByDescending { element ->
                element.signedChangeRate
            }
        }
        3 -> {
            exchangeViewModel.krwExchangeModelList.sortBy { element ->
                element.signedChangeRate
            }
            exchangeViewModel.preItemArray.sortBy { element ->
                element.signedChangeRate
            }
        }
        4 -> {
            exchangeViewModel.krwExchangeModelList.sortByDescending { element ->
                element.accTradePrice24h
            }
            exchangeViewModel.preItemArray.sortByDescending { element ->
                element.accTradePrice24h
            }
        }
        5 -> {
            exchangeViewModel.krwExchangeModelList.sortBy { element ->
                element.accTradePrice24h
            }
            exchangeViewModel.preItemArray.sortBy { element ->
                element.accTradePrice24h
            }
        }
        else -> {
            exchangeViewModel.krwExchangeModelList.sortByDescending { element ->
                element.accTradePrice24h
            }
            exchangeViewModel.preItemArray.sortByDescending { element ->
                element.accTradePrice24h
            }
        }
    }

    for (i in 0 until exchangeViewModel.krwExchangeModelList.size) {
        exchangeViewModel.krwExchangeModelListPosition[exchangeViewModel.krwExchangeModelList[i].market] =
            i
    }
    exchangeViewModel.krwExchangeModelMutableStateList.clear()
    exchangeViewModel.krwExchangeModelMutableStateList.addAll(exchangeViewModel.krwExchangeModelList)

    exchangeViewModel.isSocketRunning = true
}