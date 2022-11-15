package org.jeonfeel.moeuibit2.ui.coindetail.order

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constant.SELECTED_BTC_MARKET
import org.jeonfeel.moeuibit2.constant.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.ui.util.clearFocusOnKeyboardDismiss
import org.jeonfeel.moeuibit2.util.secondDecimal
import org.jeonfeel.moeuibit2.util.showToast

@Composable
fun AdjustFeeDialog(
    dialogState: MutableState<Boolean>,
    feeStateList: SnapshotStateList<MutableState<Float>>,
) {
    val context = LocalContext.current
    val krwBidFeeState: MutableState<String> = remember {
        mutableStateOf("")
    }
    val krwAskFeeState: MutableState<String> = remember {
        mutableStateOf("")
    }
    val btcBidFeeState: MutableState<String> = remember {
        mutableStateOf("")
    }
    val btcAskFeeState: MutableState<String> = remember {
        mutableStateOf("")
    }
    if (dialogState.value) {
        Dialog(onDismissRequest = { dialogState.value = false }) {
            Card(
                modifier = Modifier
                    .padding(20.dp, 0.dp)
                    .wrapContentSize()
            ) {
                Column(
                    Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "수수료 조정",
                        modifier = Modifier
                            .padding(0.dp, 20.dp)
                            .fillMaxWidth(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    AdjustFeeDialogContent(
                        feeState = feeStateList[0],
                        textFieldState = krwBidFeeState,
                        subTitle = "KRW 매수",
                        closedFloatingPointRange = 0.05f..90f,
                        lastContent = false,
                        marketState = SELECTED_KRW_MARKET
                    )
                    AdjustFeeDialogContent(
                        feeState = feeStateList[1],
                        textFieldState = krwAskFeeState,
                        subTitle = "KRW 매도",
                        closedFloatingPointRange = 0.05f..90f,
                        lastContent = false,
                        marketState = SELECTED_KRW_MARKET
                    )
                    AdjustFeeDialogContent(
                        feeState = feeStateList[2],
                        textFieldState = btcBidFeeState,
                        subTitle = "BTC 매수",
                        closedFloatingPointRange = 0.25f..90f,
                        lastContent = false,
                        marketState = SELECTED_BTC_MARKET
                    )
                    AdjustFeeDialogContent(
                        feeState = feeStateList[3],
                        textFieldState = btcAskFeeState,
                        subTitle = "BTC 매도",
                        closedFloatingPointRange = 0.25f..90f,
                        lastContent = true,
                        marketState = SELECTED_BTC_MARKET
                    )

                    Divider(modifier = Modifier
                        .fillMaxWidth(), color = Color.LightGray, thickness = 1.dp)
                    Row {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
//                                    leftButtonAction()
                                }
                                .padding(0.dp, 10.dp),
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                        Text(
                            text = "", modifier = Modifier
                                .width(1.dp)
                                .border(0.5.dp, Color.LightGray)
                                .padding(0.dp, 10.dp), fontSize = 18.sp
                        )
                        Text(text = stringResource(id = R.string.save),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
//                                    rightButtonAction()
                                }
                                .padding(0.dp, 10.dp),
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 18.sp,
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
fun AdjustFeeDialogContent(
    feeState: MutableState<Float>,
    textFieldState: MutableState<String>,
    subTitle: String,
    closedFloatingPointRange: ClosedFloatingPointRange<Float>,
    lastContent: Boolean,
    marketState: Int,
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .padding(horizontal = 15.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .border(width = 0.7.dp, color = colorResource(id = R.color.C0F0F5C))
            .padding(vertical = 8.dp)
    ) {
        Text(text = subTitle,
            modifier = Modifier.padding(horizontal = 10.dp),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold)
        BasicTextField(value = textFieldState.value, onValueChange = {
            if (it.toDoubleOrNull() == null) {
                if (it.isEmpty()) {
                    textFieldState.value = it
                    feeState.value = 0.0f
                } else {
                    context.showToast("숫자만 입력 가능합니다.")
                }
            } else {
                val itToFloat = it.toFloat()
                if (itToFloat <= 90f) {
                    if (!it.contains(".") || it.substring(it.indexOf(".")).length < 4) {
                        if(marketState == SELECTED_KRW_MARKET) {
                            if (it.length >= 4 && itToFloat < closedFloatingPointRange.start) {
                                context.showToast("최소 수수료 미만 입력 불가.")
                                textFieldState.value = closedFloatingPointRange.start.toString()
                                feeState.value = closedFloatingPointRange.start
                            } else {
                                textFieldState.value = it
                                feeState.value = itToFloat.secondDecimal().toFloat()
                            }
                        } else {
                            if (it.length >= 3 && itToFloat < closedFloatingPointRange.start) {
                                context.showToast("최소 수수료 미만 입력 불가.")
                                textFieldState.value = closedFloatingPointRange.start.toString()
                                feeState.value = closedFloatingPointRange.start
                            } else {
                                textFieldState.value = it
                                feeState.value = itToFloat.secondDecimal().toFloat()
                            }
                        }
                    } else {
                        context.showToast("소수점 둘째자리 까지 입력 가능.")
                    }
                } else {
                    context.showToast("90 보다 큰 퍼센트 입력 불가.")
                }
            }
        }, singleLine = true,
            textStyle = TextStyle(color = Color.Black,
                fontSize = 17.sp, textAlign = TextAlign.Start),
            modifier = Modifier
                .weight(1f, true)
                .clearFocusOnKeyboardDismiss()
                .padding(0.dp, 0.dp, 9.dp, 0.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            decorationBox = { innerTextField ->
                Row(modifier = Modifier.weight(1f, true),
                    verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f, true)) {
                        if (textFieldState.value.isEmpty()) {
                            Text(
                                "0",
                                style = TextStyle(color = Color.Gray,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Start),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        innerTextField()
                    }
                }
            })
    }

    Slider(value = feeState.value,
        onValueChange = {
            val result = it.secondDecimal()
            feeState.value = result.toFloat()
            textFieldState.value = result
        },
        valueRange = closedFloatingPointRange,
        modifier = Modifier.padding(horizontal = 15.dp)
    )

//    if (!lastContent) {
//        Divider(modifier = Modifier
//            .padding(bottom = 15.dp)
//            .fillMaxWidth(), color = colorResource(id = R.color.C0F0F5C), thickness = 1.dp)
//    }
}