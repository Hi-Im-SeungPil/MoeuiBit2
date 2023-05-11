package org.jeonfeel.moeuibit2.ui.coindetail.order.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.viewmodels.CoinDetailViewModel
import org.jeonfeel.moeuibit2.constants.SELECTED_BTC_MARKET
import org.jeonfeel.moeuibit2.constants.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.constants.SYMBOL_BTC
import org.jeonfeel.moeuibit2.constants.SYMBOL_KRW
import org.jeonfeel.moeuibit2.ui.custom.DpToSp
import org.jeonfeel.moeuibit2.ui.custom.clearFocusOnKeyboardDismiss
import org.jeonfeel.moeuibit2.ui.theme.decreaseColor
import org.jeonfeel.moeuibit2.ui.theme.increaseColor
import org.jeonfeel.moeuibit2.utils.secondDecimal
import org.jeonfeel.moeuibit2.utils.showToast

@Composable
fun AdjustCommissionDialog(
    dialogState: MutableState<Boolean>,
    commissionStateList: SnapshotStateList<MutableState<Float>>,
    coinDetailViewModel: CoinDetailViewModel,
) {
    val context = LocalContext.current
    if (dialogState.value) {
        val scrollState = rememberScrollState()
        val krwBidCommissionState: MutableState<String> = remember {
            mutableStateOf(commissionStateList[0].value.toString())
        }
        val krwAskCommissionState: MutableState<String> = remember {
            mutableStateOf(commissionStateList[1].value.toString())
        }
        val btcBidCommissionState: MutableState<String> = remember {
            mutableStateOf(commissionStateList[2].value.toString())
        }
        val btcAskCommissionState: MutableState<String> = remember {
            mutableStateOf(commissionStateList[3].value.toString())
        }
        Dialog(
            onDismissRequest = { dialogState.value = false },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Card(
                modifier = Modifier
                    .padding(20.dp, 0.dp)
                    .wrapContentSize()
            ) {
                Column(
                    Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .wrapContentHeight()
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.adjustCommission),
                        modifier = Modifier
                            .padding(0.dp, 20.dp)
                            .fillMaxWidth(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = DpToSp(25.dp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .verticalScroll(scrollState)
                    ) {
                        AdjustCommissionDialogContent(
                            commissionState = commissionStateList[0],
                            textFieldState = krwBidCommissionState,
                            subTitle = stringResource(id = R.string.krwBid),
                            closedFloatingPointRange = 0.05f..50f,
                            marketState = SELECTED_KRW_MARKET
                        )
                        AdjustCommissionDialogContent(
                            commissionState = commissionStateList[1],
                            textFieldState = krwAskCommissionState,
                            subTitle = stringResource(id = R.string.krwAsk),
                            closedFloatingPointRange = 0.05f..50f,
                            marketState = SELECTED_KRW_MARKET
                        )
                        AdjustCommissionDialogContent(
                            commissionState = commissionStateList[2],
                            textFieldState = btcBidCommissionState,
                            subTitle = stringResource(id = R.string.btcBid),
                            closedFloatingPointRange = 0.25f..50f,
                            marketState = SELECTED_BTC_MARKET
                        )
                        AdjustCommissionDialogContent(
                            commissionState = commissionStateList[3],
                            textFieldState = btcAskCommissionState,
                            subTitle = stringResource(id = R.string.btcAsk),
                            closedFloatingPointRange = 0.25f..50f,
                            marketState = SELECTED_BTC_MARKET
                        )
                    }

                    Divider(
                        modifier = Modifier
                            .fillMaxWidth(), color = Color.LightGray, thickness = 1.dp
                    )
                    Row {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    coinDetailViewModel.initAdjustCommission()
                                    dialogState.value = false
                                }
                                .padding(0.dp, 10.dp),
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = DpToSp(18.dp),
                                textAlign = TextAlign.Center
                            )
                        )
                        Text(
                            text = "", modifier = Modifier
                                .width(1.dp)
                                .border(0.5.dp, Color.LightGray)
                                .padding(0.dp, 10.dp), fontSize = DpToSp(18.dp)
                        )
                        Text(text = stringResource(id = R.string.save),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (krwBidCommissionState.value.isNotEmpty() && krwAskCommissionState.value.isNotEmpty() && btcAskCommissionState.value.isNotEmpty() && btcBidCommissionState.value.isNotEmpty()) {
                                        if (krwBidCommissionState.value.toFloat() >= 0.05 && krwAskCommissionState.value.toFloat() >= 0.05 && btcBidCommissionState.value.toFloat() >= 0.25 && btcAskCommissionState.value.toFloat() >= 0.25) {
                                            coinDetailViewModel.adjustCommission()
                                            dialogState.value = false
                                        } else {
                                            context.showToast(context.getString(R.string.commissionMinMessage))
                                        }
                                    } else {
                                        context.showToast(context.getString(R.string.inputNotEmptyMessage))
                                    }
                                }
                                .padding(0.dp, 10.dp),
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = DpToSp(18.dp),
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnScope.AdjustCommissionDialogContent(
    commissionState: MutableState<Float>,
    textFieldState: MutableState<String>,
    subTitle: String,
    closedFloatingPointRange: ClosedFloatingPointRange<Float>,
    marketState: Int,
) {
    val context = LocalContext.current
    val doneState = remember {
        mutableStateOf(true)
    }
    val subTitleResult = buildAnnotatedString {
        val splitTitle = subTitle.split(" ")
        if (splitTitle[0].startsWith(SYMBOL_KRW)) {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(SYMBOL_KRW)
            }
        } else {
            withStyle(
                style = SpanStyle(
                    color = colorResource(id = R.color.teal_700),
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(SYMBOL_BTC)
            }
        }
        if (splitTitle[1] == (stringResource(id = R.string.bid))) {
            withStyle(
                style = SpanStyle(
                    color = increaseColor(),
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(" ".plus(stringResource(id = R.string.bid)))
            }
        } else {
            withStyle(
                style = SpanStyle(
                    color = decreaseColor(),
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(" ".plus(stringResource(id = R.string.ask)))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .padding(horizontal = 15.dp)
                .border(width = 0.7.dp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                .padding(vertical = 8.dp)
        ) {
            if (doneState.value) {
                Image(
                    imageVector = Icons.Filled.Done,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.Green),
                    modifier = Modifier.padding(start = 8.dp)
                )
            } else {
                Image(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(increaseColor()),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Text(
                text = subTitleResult,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .align(Alignment.CenterVertically),
                fontSize = DpToSp(15.dp),
                fontWeight = FontWeight.Bold,
            )
            BasicTextField(value = textFieldState.value, onValueChange = {
                if (it.toDoubleOrNull() == null) {
                    if (it.isEmpty()) {
                        textFieldState.value = it
                        doneState.value = false
                    } else {
                        context.showToast(context.getString(R.string.onlyNumberMessage))
                    }
                } else {
                    val itToFloat = it.toFloat()
                    if (itToFloat <= 50f) {
                        if (!it.contains(".") || it.substring(it.indexOf(".")).length < 4) {
                            if (itToFloat < closedFloatingPointRange.start) {
                                textFieldState.value = it
                                doneState.value = false
                                // 최소 수수료
                            } else {
                                textFieldState.value = it
                                commissionState.value = itToFloat.secondDecimal().toFloat()
                                doneState.value = true
                            }
                        } else {
                            context.showToast(context.getString(R.string.commissionSecondDecimalMessage))
                        }
                    } else {
                        context.showToast(context.getString(R.string.commissionMaxMessage))
                    }
                }
            }, singleLine = true,
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = DpToSp(17.dp), textAlign = TextAlign.Start
                ),
                modifier = Modifier
                    .weight(1f, true)
                    .clearFocusOnKeyboardDismiss()
                    .align(Alignment.CenterVertically)
                    .padding(0.dp, 0.dp, 9.dp, 0.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier.weight(1f, true),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.weight(1f, true)) {
                            if (textFieldState.value.isEmpty()) {
                                Text(
                                    "입력",
                                    style = TextStyle(
                                        color = Color.Gray,
                                        fontSize = DpToSp(15.dp),
                                        textAlign = TextAlign.Start
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            innerTextField()
                        }
                    }
                })
        }
        if (!doneState.value && marketState == SELECTED_KRW_MARKET) {
            Text(
                text = stringResource(id = R.string.krwMinimumMessage),
                fontSize = DpToSp(11.dp),
                style = TextStyle(color = increaseColor()),
                modifier = Modifier.padding(start = 15.dp)
            )
        } else if (!doneState.value && marketState == SELECTED_BTC_MARKET) {
            Text(
                text = stringResource(id = R.string.btcMinimumMessage),
                fontSize = DpToSp(11.dp),
                style = TextStyle(color = increaseColor()),
                modifier = Modifier.padding(start = 15.dp)
            )
        }
        Slider(
            value = commissionState.value,
            onValueChange = {
                val result = it.secondDecimal()
                commissionState.value = result.toFloat()
                textFieldState.value = result
                doneState.value = true
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
                activeTrackColor = Color.Green,
                disabledActiveTrackColor = Color.Green.copy(alpha = 0.5f)
            ),
            valueRange = closedFloatingPointRange,
            modifier = Modifier.padding(horizontal = 15.dp)
        )
    }
}