package org.jeonfeel.moeuibit2.ui.coindetail.order

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.CoinDetailViewModel
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.ui.mainactivity.exchange.clearFocusOnKeyboardDismiss
import org.jeonfeel.moeuibit2.util.Calculator
import kotlin.math.round

@Composable
fun TotalAmountDesignatedDialog(
    coinDetailViewModel: CoinDetailViewModel
) {
    if (coinDetailViewModel.askBidDialogState) {

        val userSeedMoney = Calculator.getDecimalFormat()
            .format(coinDetailViewModel.userSeedMoney - round(coinDetailViewModel.userSeedMoney * 0.0005).toLong())
        val userCoinValuable = Calculator.getDecimalFormat().format(round(coinDetailViewModel.userCoinQuantity * coinDetailViewModel.currentTradePriceState))

        Dialog(onDismissRequest = { coinDetailViewModel.askBidDialogState = false }) {
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
                        text = if (coinDetailViewModel.askBidSelectedTab.value == 1) "총액 지정 매수" else "총액 지정 매도",
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

                    CustomTextField(modifier = Modifier.fillMaxWidth(),"0",18.sp,coinDetailViewModel)

                    Row(
                        modifier = Modifier
                            .padding(10.dp, 20.dp, 10.dp, 20.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "거래 가능",
                            style = TextStyle(fontSize = 18.sp)
                        )
                        AutoSizeText(
                            text = if (coinDetailViewModel.askBidSelectedTab.value == 1) userSeedMoney else userCoinValuable,
                            modifier = Modifier.weight(1f, true),
                            textStyle = TextStyle(fontSize = 18.sp, textAlign = TextAlign.End)
                        )
                        Text(
                            text = " KRW",
                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                    Divider(modifier = Modifier.fillMaxWidth(), Color.LightGray, 0.5.dp)
                    Row {
                        Text(
                            text = stringResource(id = R.string.commonCancel), modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    coinDetailViewModel.askBidDialogState = false
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
                                .width(0.5.dp)
                                .border(0.5.dp, Color.LightGray)
                                .padding(0.dp, 10.dp), fontSize = 18.sp
                        )
                        Text(text = if (coinDetailViewModel.askBidSelectedTab.value == 1) "매수" else "매도",
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    coinDetailViewModel.askBidDialogState = false
                                }
                                .background(if (coinDetailViewModel.askBidSelectedTab.value == 1) Color.Red else Color.Blue)
                                .padding(0.dp, 10.dp),
                            style = TextStyle(
                                color = Color.White,
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
private fun CustomTextField(
    modifier: Modifier = Modifier,
    placeholderText: String = "Placeholder",
    fontSize: TextUnit = MaterialTheme.typography.body2.fontSize,
    coinDetailViewModel: CoinDetailViewModel = viewModel()
) {
    val context = LocalContext.current

    BasicTextField(value = coinDetailViewModel.totalPriceDesignated, onValueChange = {
        val result = it.replace(",","",false)
        if(result.toLongOrNull() == null && it != "") {
            coinDetailViewModel.totalPriceDesignated = ""
            Toast.makeText(context,"숫자만 입력 가능합니다.", Toast.LENGTH_SHORT).show()
        } else {
            coinDetailViewModel.totalPriceDesignated = Calculator.getDecimalFormat().format(result.toLong())
        }
    }, singleLine = true,
        textStyle = TextStyle(color = Color.Black,
            fontSize = 17.sp, textAlign = TextAlign.End),
        modifier = modifier
            .clearFocusOnKeyboardDismiss()
            .padding(0.dp, 0.dp, 9.dp, 0.dp),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.None),
        decorationBox = { innerTextField ->
            Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f,true)) {
                    if (coinDetailViewModel.totalPriceDesignated.isEmpty()) {
                        Text(
                            placeholderText,
                            style = TextStyle(color = Color.Black,
                                fontSize = fontSize,
                                textAlign = TextAlign.End),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    innerTextField()
                }
            }
        })
}