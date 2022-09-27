package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.main.viewmodel.MainViewModel
import org.jeonfeel.moeuibit2.ui.util.drawUnderLine

@Composable
fun SortButtons(mainViewModel: MainViewModel = viewModel()) {
    val buttonState = mainViewModel.selectedButtonState
    val marketState = mainViewModel.selectedMarketState
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .drawUnderLine(lineColor = Color.DarkGray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1f), text = ""
        )
        SortButton(1, buttonState, mainViewModel)
        SortButton(2, buttonState, mainViewModel)
        SortButton(3, buttonState, mainViewModel)
    }
}

@Composable
private fun RowScope.SortButton(
    buttonNum: Int,
    buttonState: MutableState<Int>,
    mainViewModel: MainViewModel,
) {
    val buttonText = remember {
        mutableStateOf("")
    }
    val textColor = remember {
        mutableStateOf(Color.Black)
    }
    val textBackground = remember {
        mutableStateOf(Color.White)
    }

    when (buttonNum) {
        1 -> {
            when (buttonState.value) {
                0 -> {
                    buttonText.value = "현재가↓"
                    textColor.value = Color.White
                    textBackground.value = colorResource(id = R.color.C0F0F5C)
                }
                1 -> {
                    buttonText.value = "현재가↑"
                    textColor.value = Color.White
                    textBackground.value = colorResource(id = R.color.C0F0F5C)
                }
                else -> {
                    buttonText.value = "현재가↓↑"
                    textColor.value = Color.Black
                    textBackground.value = Color.White
                }
            }
        }
        2 -> {
            when (buttonState.value) {
                2 -> {
                    buttonText.value = "전일대비↓"
                    textColor.value = Color.White
                    textBackground.value = colorResource(id = R.color.C0F0F5C)
                }
                3 -> {
                    buttonText.value = "전일대비↑"
                    textColor.value = Color.White
                    textBackground.value = colorResource(id = R.color.C0F0F5C)
                }
                else -> {
                    buttonText.value = "전일대비↓↑"
                    textColor.value = Color.Black
                    textBackground.value = Color.White
                }
            }
        }
        3 -> {
            when (buttonState.value) {
                4 -> {
                    buttonText.value = "거래량↓"
                    textColor.value = Color.White
                    textBackground.value = colorResource(id = R.color.C0F0F5C)
                }
                5 -> {
                    buttonText.value = "거래량↑"
                    textColor.value = Color.White
                    textBackground.value = colorResource(id = R.color.C0F0F5C)
                }
                else -> {
                    buttonText.value = "거래량↓↑"
                    textColor.value = Color.Black
                    textBackground.value = Color.White
                }
            }
        }
    }
    Text(text = buttonText.value,
        modifier = Modifier
            .weight(1f)
            .clickable {
                when (buttonNum) {
                    1 -> {
                        if (mainViewModel.updateExchange) {
                            when {
                                buttonState.value != 0 && buttonState.value != 1 -> {
                                    buttonState.value = 0
                                }
                                buttonState.value == 0 -> {
                                    buttonState.value = 1
                                }
                                else -> {
                                    buttonState.value = -1
                                }
                            }
                            mainViewModel.sortList(buttonState.value)
                        }
                    }
                    2 -> {
                        if (mainViewModel.updateExchange) {
                            when {
                                buttonState.value != 2 && buttonState.value != 3 -> {
                                    buttonState.value = 2
                                }
                                buttonState.value == 2 -> {
                                    buttonState.value = 3
                                }
                                else -> {
                                    buttonState.value = -1
                                }
                            }
                            mainViewModel.sortList(buttonState.value)
                        }
                    }
                    3 -> {
                        if (mainViewModel.updateExchange) {
                            when {
                                buttonState.value != 4 && buttonState.value != 5 -> {
                                    buttonState.value = 4
                                }
                                buttonState.value == 4 -> {
                                    buttonState.value = 5
                                }
                                else -> {
                                    buttonState.value = -1
                                }
                            }
                            mainViewModel.sortList(buttonState.value)
                        }
                    }
                }
            }
            .align(Alignment.CenterVertically)
            .background(textBackground.value)
            .padding(0.dp, 7.dp),
        style = TextStyle(color = textColor.value,fontSize = 13.sp, textAlign = TextAlign.Center))
}