package org.jeonfeel.moeuibit2.ui.kimp

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jeonfeel.moeuibit2.ui.util.drawUnderLine



@Composable
fun KimpScreenSortButtons() {
//    val buttonState = mainViewModel.selectedButtonState
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .drawUnderLine(lineColor = Color.DarkGray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "", Modifier.weight(1f))
        Text(text = "현재가", modifier = Modifier.weight(1.5f).padding(0.dp, 7.dp), fontSize = 13.sp, textAlign = TextAlign.Center)
        Text(text = "프리미엄", modifier = Modifier.weight(1f).padding(0.dp, 7.dp), fontSize = 13.sp, textAlign = TextAlign.Center)
        Text(text = "전일대비", modifier = Modifier.weight(1f).padding(0.dp, 7.dp), fontSize = 13.sp, textAlign = TextAlign.Center)
//        SortButton(1)
//        SortButton(2)
//        SortButton(3)
    }
}

@Composable
private fun RowScope.SortButton(
    buttonNum: Int,
//    buttonState: MutableState<Int>,
//    mainViewModel: MainViewModel,
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

//    when (buttonNum) {
//        1 -> {
//            when (buttonState.value) {
//                0 -> {
//                    buttonText.value = "현재가↓"
//                    textColor.value = Color.White
//                    textBackground.value = colorResource(id = R.color.C0F0F5C)
//                }
//                1 -> {
//                    buttonText.value = "현재가↑"
//                    textColor.value = Color.White
//                    textBackground.value = colorResource(id = R.color.C0F0F5C)
//                }
//                else -> {
//                    buttonText.value = "현재가↓↑"
//                    textColor.value = Color.Black
//                    textBackground.value = Color.White
//                }
//            }
//        }
//        2 -> {
//            when (buttonState.value) {
//                2 -> {
//                    buttonText.value = "프리미엄↓"
//                    textColor.value = Color.White
//                    textBackground.value = colorResource(id = R.color.C0F0F5C)
//                }
//                3 -> {
//                    buttonText.value = "프리미엄↑"
//                    textColor.value = Color.White
//                    textBackground.value = colorResource(id = R.color.C0F0F5C)
//                }
//                else -> {
//                    buttonText.value = "프리미엄↓↑"
//                    textColor.value = Color.Black
//                    textBackground.value = Color.White
//                }
//            }
//        }
//        3 -> {
//            when (buttonState.value) {
//                4 -> {
//                    buttonText.value = "전일대비↓"
//                    textColor.value = Color.White
//                    textBackground.value = colorResource(id = R.color.C0F0F5C)
//                }
//                5 -> {
//                    buttonText.value = "전일대비↑"
//                    textColor.value = Color.White
//                    textBackground.value = colorResource(id = R.color.C0F0F5C)
//                }
//                else -> {
//                    buttonText.value = "전일대비↓↑"
//                    textColor.value = Color.Black
//                    textBackground.value = Color.White
//                }
//            }
//        }
//    }
//    Text(text = buttonText.value,
//        modifier = Modifier
//            .weight(1f)
//            .clickable {
//                when (buttonNum) {
//                    1 -> {
//                        if (mainViewModel.updateExchange) {
//                            when {
//                                buttonState.value != 0 && buttonState.value != 1 -> {
//                                    buttonState.value = 0
//                                }
//                                buttonState.value == 0 -> {
//                                    buttonState.value = 1
//                                }
//                                else -> {
//                                    buttonState.value = -1
//                                }
//                            }
//                            mainViewModel.sortList(buttonState.value)
//                        }
//                    }
//                    2 -> {
//                        if (mainViewModel.updateExchange) {
//                            when {
//                                buttonState.value != 2 && buttonState.value != 3 -> {
//                                    buttonState.value = 2
//                                }
//                                buttonState.value == 2 -> {
//                                    buttonState.value = 3
//                                }
//                                else -> {
//                                    buttonState.value = -1
//                                }
//                            }
//                            mainViewModel.sortList(buttonState.value)
//                        }
//                    }
//                    3 -> {
//                        if (mainViewModel.updateExchange) {
//                            when {
//                                buttonState.value != 4 && buttonState.value != 5 -> {
//                                    buttonState.value = 4
//                                }
//                                buttonState.value == 4 -> {
//                                    buttonState.value = 5
//                                }
//                                else -> {
//                                    buttonState.value = -1
//                                }
//                            }
//                            mainViewModel.sortList(buttonState.value)
//                        }
//                    }
//                }
//            }
//            .align(Alignment.CenterVertically)
//            .background(textBackground.value)
//            .padding(0.dp, 7.dp),
//        style = TextStyle(color = textColor.value,fontSize = 13.sp, textAlign = TextAlign.Center))
}