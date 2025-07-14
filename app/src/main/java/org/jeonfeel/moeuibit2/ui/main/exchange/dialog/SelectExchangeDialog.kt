package org.jeonfeel.moeuibit2.ui.main.exchange.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonDialogBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonDialogButtonsBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonHintTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonRejectTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor

@Composable
fun SelectExchangeDialog(
    dialogState: MutableState<Boolean>,
    initialExchange: String,
    onConfirm: (String) -> Unit,
) {
    val selectedExchange = remember { mutableStateOf(initialExchange) }

    if (dialogState.value) {
        Dialog(onDismissRequest = {
            selectedExchange.value = initialExchange
            dialogState.value = false
        }) {
            Column(
                modifier = Modifier
                    .background(commonDialogBackground(), RoundedCornerShape(10.dp))
            ) {
                // 옵션 아이템들
                SelectExchangeItem(
                    icon = R.drawable.img_upbit,
                    text = "업비트",
                    id = EXCHANGE_UPBIT,
                    selectedValue = selectedExchange.value,
                    clickAction = { selectedExchange.value = EXCHANGE_UPBIT }
                )
                SelectExchangeItem(
                    icon = R.drawable.img_bitthumb,
                    text = "빗썸",
                    id = EXCHANGE_BITTHUMB,
                    selectedValue = selectedExchange.value,
                    clickAction = { selectedExchange.value = EXCHANGE_BITTHUMB }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .background(
                            commonDialogButtonsBackground(),
                            shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
                        )
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        modifier = Modifier
                            .weight(1f)
                            .noRippleClickable {
                                selectedExchange.value = initialExchange
                                dialogState.value = false
                            }
                            .padding(vertical = 10.dp),
                        style = TextStyle(
                            color = commonRejectTextColor(),
                            fontSize = DpToSp(17.dp),
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        text = "적용",
                        modifier = Modifier
                            .weight(1f)
                            .noRippleClickable {
                                onConfirm(selectedExchange.value)
                                dialogState.value = false
                            }
                            .padding(vertical = 10.dp),
                        style = TextStyle(
                            color = commonTextColor(),
                            fontSize = DpToSp(17.dp),
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectExchangeItem(
    icon: Int,
    text: String,
    id: String,
    selectedValue: String,
    clickAction: () -> Unit,
) {
    val isSelected = selectedValue == id
    val tintColor = if (isSelected) commonTextColor() else commonHintTextColor()
    val borderColor = if (isSelected) commonTextColor() else commonDialogBackground()

    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .fillMaxWidth()
            .background(commonDialogBackground(), RoundedCornerShape(10.dp))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .padding(horizontal = 25.dp, vertical = 20.dp)
            .noRippleClickable { clickAction() }
    ) {

        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier
                .size(35.dp)
                .clip(RoundedCornerShape(8.dp))
                .align(Alignment.CenterVertically)
                .border(width = 1.dp, color = borderColor, RoundedCornerShape(8.dp)),
        )

        Text(
            text = text,
            modifier = Modifier
                .padding(start = 20.dp)
                .align(Alignment.CenterVertically),
            style = TextStyle(
                fontSize = DpToSp(17.dp),
                fontWeight = FontWeight.W500,
                color = tintColor,
                textAlign = TextAlign.Center
            )
        )
    }
}