package org.jeonfeel.moeuibit2.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun CommonDialog(
    dialogState: MutableState<Boolean>,
    title: String,
    content: String,
    leftButtonText: String,
    rightButtonText: String,
    leftButtonAction: () -> Unit,
    rightButtonAction: () -> Unit
) {
    if(dialogState.value) {
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
                        text = title,
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
                    Text(
                        text = content,
                        modifier = Modifier
                            .padding(10.dp, 10.dp, 10.dp, 20.dp)
                            .fillMaxWidth(),
                        style = TextStyle(fontSize = 18.sp)
                    )
                    Divider(modifier = Modifier.fillMaxWidth(), Color.LightGray, 0.5.dp)
                    Row {
                        Text(
                            text = leftButtonText, modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    leftButtonAction()
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
                        Text(text = rightButtonText,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    rightButtonAction()
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