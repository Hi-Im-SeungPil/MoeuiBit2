package org.jeonfeel.moeuibit2.ui.main.exchange

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.viewmodels.MainViewModel
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.ui.custom.DpToSp
import org.jeonfeel.moeuibit2.ui.custom.drawUnderLine

enum class SortButtons {
    SortPriceButton, SortRateButton, SortAmountButton
}

@Composable
fun SortButtons(
    sortButtonState: MutableState<Int>,
    selectedMarketState: MutableState<Int>,
    isUpdateExchange: Boolean,
    sortList: (marketState: Int) -> Unit
) {
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

        val values = SortButtons.values()
        for (i in values.indices) {
            SortButton(
                buttonId = values[i],
                sortButtonState = sortButtonState,
                selectedMarketState = selectedMarketState,
                isUpdateExchange = isUpdateExchange,
                sortList = sortList
            )
        }
    }
}

@Composable
private fun RowScope.SortButton(
    buttonId: SortButtons,
    sortButtonState: MutableState<Int>,
    selectedMarketState: MutableState<Int>,
    isUpdateExchange: Boolean,
    sortList: (marketState: Int) -> Unit
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
                    buttonText.value = stringResource(id = R.string.currentPriceSortDec)
                    textColor.value = Color.White
                    textBackground.value = colorResource(id = R.color.C0F0F5C)
                }
                SORT_PRICE_ASC -> {
                    buttonText.value = stringResource(id = R.string.currentPriceSort)
                    textColor.value = Color.White
                    textBackground.value = colorResource(id = R.color.C0F0F5C)
                }
                else -> {
                    buttonText.value = stringResource(id = R.string.currentPriceButton)
                    textColor.value = Color.Black
                    textBackground.value = Color.White
                }
            }
        }
        SortButtons.SortRateButton -> {
            when (sortButtonState.value) {
                SORT_RATE_DEC -> {
                    buttonText.value = stringResource(id = R.string.changeSortDec)
                    textColor.value = Color.White
                    textBackground.value = colorResource(id = R.color.C0F0F5C)
                }
                SORT_RATE_ASC -> {
                    buttonText.value = stringResource(id = R.string.currentPriceSort)
                    textColor.value = Color.White
                    textBackground.value = colorResource(id = R.color.C0F0F5C)
                }
                else -> {
                    buttonText.value = stringResource(id = R.string.change)
                    textColor.value = Color.Black
                    textBackground.value = Color.White
                }
            }
        }
        SortButtons.SortAmountButton -> {
            when (sortButtonState.value) {
                SORT_AMOUNT_DEC -> {
                    buttonText.value = stringResource(id = R.string.volumeSortDec)
                    textColor.value = Color.White
                    textBackground.value = colorResource(id = R.color.C0F0F5C)
                }
                SORT_AMOUNT_ASC -> {
                    buttonText.value = stringResource(id = R.string.volumeSort)
                    textColor.value = Color.White
                    textBackground.value = colorResource(id = R.color.C0F0F5C)
                }
                else -> {
                    buttonText.value = stringResource(id = R.string.volume)
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
                        if (isUpdateExchange) {
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
                            sortList(selectedMarketState.value)
                        }
                    }
                    SortButtons.SortRateButton -> {
                        if (isUpdateExchange) {
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
                            sortList(selectedMarketState.value)
                        }
                    }
                    SortButtons.SortAmountButton -> {
                        if (isUpdateExchange) {
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
                            sortList(selectedMarketState.value)
                        }
                    }
                }
            }
            .align(Alignment.CenterVertically)
            .background(textBackground.value)
            .padding(0.dp, 7.dp),
        style = TextStyle(
            color = textColor.value,
            fontSize = DpToSp(dp = 13),
            textAlign = TextAlign.Center
        ))
}