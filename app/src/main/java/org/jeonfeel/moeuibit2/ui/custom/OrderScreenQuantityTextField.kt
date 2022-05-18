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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jeonfeel.moeuibit2.ui.mainactivity.exchange.clearFocusOnKeyboardDismiss
import org.jeonfeel.moeuibit2.viewmodel.coindetail.CoinDetailViewModel

@Composable
fun OrderScreenQuantityTextField(
    modifier: Modifier = Modifier,
    textFieldValue: MutableState<String> = mutableStateOf(""),
    placeholderText: String = "Placeholder",
    fontSize: TextUnit = MaterialTheme.typography.body2.fontSize,
    coinDetailViewModel: CoinDetailViewModel
) {
    val focusManager = LocalFocusManager.current

    val value = if(coinDetailViewModel.askBidSelectedTab.value == 1) {
        coinDetailViewModel.bidQuantity
    } else {
        coinDetailViewModel.askQuantity
    }

    BasicTextField(value = value.value, onValueChange = {
        value.value = it
    }, singleLine = true,
        textStyle = TextStyle(color = Color.Black,
            fontSize = 17.sp, textAlign = TextAlign.End),
        modifier = modifier.clearFocusOnKeyboardDismiss().padding(0.dp,0.dp,9.dp,0.dp),
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