package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.viewmodel.ExchangeViewModel
import org.jeonfeel.moeuibit2.R

@Composable
fun SortButtons(exchangeViewModel: ExchangeViewModel = viewModel()) {
    val selectedButtonState = remember { mutableStateOf(-1) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .drawWithContent {
                drawContent()
                clipRect {
                    val strokeWidth = Stroke.DefaultMiter
                    val y = size.height
                    drawLine(
                        brush = SolidColor(Color.LightGray),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Square,
                        start = Offset.Zero.copy(y = y),
                        end = Offset(x = size.width, y = y)
                    )
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(modifier = Modifier
            .weight(1f)
            .padding(0.dp, 0.dp, 0.dp, 0.5.dp), text = "")
        TextButton(onClick = {
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
            colors = getButtonBackgroundColor(buttonNum = 1,
                buttonState = selectedButtonState.value),
            shape = RectangleShape) {
            when (selectedButtonState.value) {
                0 -> {
                    Text(text = "현재가↓",
                        fontSize = 13.sp,
                        style = TextStyle(color = Color.White))
                }
                1 -> {
                    Text(text = "현재가↑",
                        fontSize = 13.sp,
                        style = TextStyle(color = Color.White))
                }
                else -> {
                    Text(text = "현재가↓↑",
                        fontSize = 13.sp,
                        style = TextStyle(color = Color.LightGray))
                }
            }
        }

        TextButton(onClick = {
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
        }, modifier = Modifier
            .weight(1f)
            .padding(0.dp, 0.dp, 0.dp, 0.5.dp),
            colors = getButtonBackgroundColor(buttonNum = 2,
                buttonState = selectedButtonState.value),
            shape = RectangleShape) {
            when (selectedButtonState.value) {
                2 -> {
                    Text(text = "전일대비↓",
                        fontSize = 13.sp,
                        maxLines = 1,
                        style = TextStyle(color = Color.White))
                }
                3 -> {
                    Text(text = "전일대비↑",
                        fontSize = 13.sp,
                        maxLines = 1,
                        style = TextStyle(color = Color.White))
                }
                else -> {
                    Text(text = "전일대비↓↑",
                        fontSize = 13.sp,
                        maxLines = 1,
                        style = TextStyle(color = Color.LightGray))
                }
            }
        }

        TextButton(onClick = {
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
        }, modifier = Modifier
            .weight(1f)
            .padding(0.dp, 0.dp, 0.dp, 0.5.dp),
            colors = getButtonBackgroundColor(buttonNum = 3,
                buttonState = selectedButtonState.value),
            shape = RectangleShape) {
            when (selectedButtonState.value) {
                4 -> {
                    Text(text = "거래량↓",
                        fontSize = 13.sp,
                        style = TextStyle(color = Color.White))
                }
                5 -> {
                    Text(text = "거래량↑",
                        fontSize = 13.sp,
                        style = TextStyle(color = Color.White))
                }
                else -> {
                    Text(text = "거래량↓↑",
                        fontSize = 13.sp,
                        style = TextStyle(color = Color.LightGray))
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
        }
        1 -> {
            exchangeViewModel.krwExchangeModelList.sortBy { element ->
                element.tradePrice
            }
        }
        2 -> {
            exchangeViewModel.krwExchangeModelList.sortByDescending { element ->
                element.signedChangeRate
            }
        }
        3 -> {
            exchangeViewModel.krwExchangeModelList.sortBy { element ->
                element.signedChangeRate
            }
        }
        4 -> {
            exchangeViewModel.krwExchangeModelList.sortByDescending { element ->
                element.accTradePrice24h
            }
        }
        5 -> {
            exchangeViewModel.krwExchangeModelList.sortBy { element ->
                element.accTradePrice24h
            }
        }
        else -> {
            exchangeViewModel.krwExchangeModelList.sortByDescending { element ->
                element.accTradePrice24h
            }
        }
    }

    exchangeViewModel.preItemArray.clear()
    exchangeViewModel.preItemArray.addAll(exchangeViewModel.krwExchangeModelList)

    for (i in 0 until exchangeViewModel.krwExchangeModelList.size) {
        exchangeViewModel.krwExchangeModelListPosition[exchangeViewModel.krwExchangeModelList[i].market] =
            i
    }

    for (i in 0 until exchangeViewModel.krwExchangeModelList.size) {
        exchangeViewModel.krwExchangeModelMutableStateList[i] =
            exchangeViewModel.krwExchangeModelList[i]
    }

    exchangeViewModel.isSocketRunning = true
}

@Composable
fun getButtonBackgroundColor(buttonNum: Int, buttonState: Int): ButtonColors {
    when (buttonNum) {
        1 -> {
            return if (buttonState == 0 || buttonState == 1) {
                ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.C0F0F5C))
            } else {
                ButtonDefaults.buttonColors(backgroundColor = Color.White)
            }
        }
        2 -> {
            return if (buttonState == 2 || buttonState == 3) {
                ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.C0F0F5C))
            } else {
                ButtonDefaults.buttonColors(backgroundColor = Color.White)
            }
        }
        3 -> {
            return if (buttonState == 4 || buttonState == 5) {
                ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.C0F0F5C))
            } else {
                ButtonDefaults.buttonColors(backgroundColor = Color.White)
            }
        }
        else -> return ButtonDefaults.buttonColors(backgroundColor = Color.White)
    }
}