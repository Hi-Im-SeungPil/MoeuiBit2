package org.jeonfeel.moeuibit2.ui.common

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonDialogBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonDialogButtonsBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonHintTextColor
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
                            color = commonHintTextColor(),
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
fun prevew() {
    TwoButtonCommonDialog()
}

@Composable
fun OneButtonCommonDialog(
    dialogState: MutableState<Boolean>,
    title: String,
    content: String,
    buttonText: String,
    buttonAction: () -> Unit,
) {
    if (dialogState.value) {
        Dialog(onDismissRequest = { dialogState.value = false }) {
            Card(
                modifier = Modifier
                    .background(color = Color.Transparent)
                    .padding(20.dp, 0.dp)
                    .wrapContentSize(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    Modifier
                        .background(color = MaterialTheme.colorScheme.background)
                        .wrapContentHeight()
                        .fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        modifier = Modifier
                            .padding(0.dp, 20.dp)
                            .fillMaxWidth(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = DpToSp(25.dp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    )
                    Text(
                        text = content,
                        modifier = Modifier
                            .padding(10.dp, 10.dp, 10.dp, 20.dp)
                            .fillMaxWidth(),
                        style = TextStyle(
                            fontSize = DpToSp(18.dp),
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    )
                    Divider(modifier = Modifier.fillMaxWidth(), Color.LightGray, 1.dp)
                    Text(
                        text = buttonText, modifier = Modifier
                            .weight(1f)
                            .clickable {
                                buttonAction()
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