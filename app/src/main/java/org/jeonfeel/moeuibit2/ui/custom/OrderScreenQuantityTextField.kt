package org.jeonfeel.moeuibit2.ui.custom

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.CoinDetailViewModel
import org.jeonfeel.moeuibit2.constant.ASK_BID_SCREEN_BID_TAB
import org.jeonfeel.moeuibit2.ui.util.clearFocusOnKeyboardDismiss
import org.jeonfeel.moeuibit2.util.showToast

@Composable
fun OrderScreenQuantityTextField(
    modifier: Modifier = Modifier,
    placeholderText: String = "Placeholder",
    fontSize: TextUnit = MaterialTheme.typography.body2.fontSize,
    coinDetailViewModel: CoinDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val value = if(coinDetailViewModel.askBidSelectedTab.value == ASK_BID_SCREEN_BID_TAB) {
        coinDetailViewModel.bidQuantity
    } else {
        coinDetailViewModel.askQuantity
    }

    BasicTextField(value = value.value, onValueChange = {
        if(it.toDoubleOrNull() == null && it != "") {
            value.value = ""
            context.showToast("숫자만 입력 가능합니다.")
        } else if(coinDetailViewModel.currentTradePriceState == 0.0) {
            context.showToast("네트워크 통신 오류입니다.")
        } else {
            value.value = it
        }
    }, singleLine = true,
        textStyle = TextStyle(color = Color.Black,
            fontSize = 17.sp, textAlign = TextAlign.End),
        modifier = modifier
            .clearFocusOnKeyboardDismiss()
            .padding(0.dp, 0.dp, 9.dp, 0.dp),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        decorationBox = { innerTextField ->
            Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f,true)) {
                    if (value.value.isEmpty()) {
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