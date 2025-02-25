package org.jeonfeel.moeuibit2.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonDialogBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonDialogButtonsBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonRejectTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor

@Composable
fun TwoButtonCommonDialog(
    dialogState: MutableState<Boolean> = mutableStateOf(true),
    icon: Int? = null,
    content: String = "반갑습니다",
    leftButtonText: String = "취소",
    rightButtonText: String = "확인",
    leftButtonAction: () -> Unit = {},
    rightButtonAction: () -> Unit = {},
) {
    if (dialogState.value) {
        Dialog(onDismissRequest = { dialogState.value = false }) {
            Column(
                Modifier
                    .background(color = commonDialogBackground(), shape = RoundedCornerShape(10.dp))
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {
                if (icon != null) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .size(50.dp)
                            .align(Alignment.CenterHorizontally),
                        tint = commonTextColor()
                    )
                }
                Text(
                    text = content,
                    modifier = Modifier
                        .padding(bottom = 35.dp, top = 30.dp)
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth(),
                    style = TextStyle(
                        fontSize = DpToSp(15.dp),
                        color = commonTextColor()
                    )
                )
                Row(
                    modifier = Modifier
                        .background(
                            color = commonDialogButtonsBackground(),
                            shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
                        )
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = leftButtonText, modifier = Modifier
                            .weight(1f)
                            .clickable {
                                leftButtonAction()
                            }
                            .padding(0.dp, 10.dp),
                        style = TextStyle(
                            color = commonRejectTextColor(),
                            fontSize = DpToSp(dp = 17.dp),
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(text = rightButtonText,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                rightButtonAction()
                            }
                            .padding(0.dp, 10.dp),
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
@Preview
private fun Preview() {
    TwoButtonCommonDialog()
}