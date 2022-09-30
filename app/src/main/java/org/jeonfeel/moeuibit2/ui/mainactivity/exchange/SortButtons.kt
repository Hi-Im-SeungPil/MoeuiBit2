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
import org.jeonfeel.moeuibit2.constant.*
import org.jeonfeel.moeuibit2.ui.util.drawUnderLine

enum class SortButtons{
    SortPriceButton, SortRateButton, SortAmountButton
}

@Composable
fun SortButtons(mainViewModel: MainViewModel = viewModel()) {
    val sortButtonState = mainViewModel.sortButtonState
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
        SortButton(SortButtons.SortPriceButton, sortButtonState, mainViewModel)
        SortButton(SortButtons.SortRateButton, sortButtonState, mainViewModel)
        SortButton(SortButtons.SortAmountButton, sortButtonState, mainViewModel)
    }
}

@Composable
private fun RowScope.SortButton(
    buttonId: SortButtons,
    sortButtonState: MutableState<Int>,
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

    when (buttonId) {
        SortButtons.SortPriceButton -> {
            when (sortButtonState.value) {
                SORT_PRICE_DEC -> {
                    buttonText.value = "현재가↓"
                    textColor.value = Color.White
                    textBackground.value = colorResource(id = R.color.C0F0F5C)
                }
                SORT_PRICE_ASC -> {
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
        SortButtons.SortRateButton -> {
            when (sortButtonState.value) {
                SORT_RATE_DEC-> {
                    buttonText.value = "전일대비↓"
                    textColor.value = Color.White
                    textBackground.value = colorResource(id = R.color.C0F0F5C)
                }
                SORT_RATE_ASC -> {
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
        SortButtons.SortAmountButton -> {
            when (sortButtonState.value) {
                SORT_AMOUNT_DEC -> {
                    buttonText.value = "거래량↓"
                    textColor.value = Color.White
                    textBackground.value = colorResource(id = R.color.C0F0F5C)
                }
                SORT_AMOUNT_ASC -> {
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
                when (buttonId) {
                    SortButtons.SortPriceButton -> {
                        if (mainViewModel.updateExchange) {
                            when {
                                sortButtonState.value != SORT_PRICE_DEC && sortButtonState.value != SORT_PRICE_ASC -> {
                                    sortButtonState.value = SORT_PRICE_DEC
                                }
                                sortButtonState.value == SORT_PRICE_DEC -> {
                                    sortButtonState.value = SORT_PRICE_ASC
                                }
                                else -> {
                                    sortButtonState.value = SORT_DEFAULT
                                }
                            }
                            mainViewModel.sortList(sortButtonState.value)
                        }
                    }
                    SortButtons.SortRateButton -> {
                        if (mainViewModel.updateExchange) {
                            when {
                                sortButtonState.value != SORT_RATE_DEC && sortButtonState.value != SORT_RATE_ASC -> {
                                    sortButtonState.value = SORT_RATE_DEC
                                }
                                sortButtonState.value == SORT_RATE_DEC -> {
                                    sortButtonState.value = SORT_RATE_ASC
                                }
                                else -> {
                                    sortButtonState.value = SORT_DEFAULT
                                }
                            }
                            mainViewModel.sortList(sortButtonState.value)
                        }
                    }
                    SortButtons.SortAmountButton -> {
                        if (mainViewModel.updateExchange) {
                            when {
                                sortButtonState.value != SORT_AMOUNT_DEC && sortButtonState.value != SORT_AMOUNT_ASC -> {
                                    sortButtonState.value = SORT_AMOUNT_DEC
                                }
                                sortButtonState.value == SORT_AMOUNT_DEC -> {
                                    sortButtonState.value = SORT_AMOUNT_ASC
                                }
                                else -> {
                                    sortButtonState.value = SORT_DEFAULT
                                }
                            }
                            mainViewModel.sortList(sortButtonState.value)
                        }
                    }
                }
            }
            .align(Alignment.CenterVertically)
            .background(textBackground.value)
            .padding(0.dp, 7.dp),
        style = TextStyle(color = textColor.value,fontSize = 13.sp, textAlign = TextAlign.Center))
}