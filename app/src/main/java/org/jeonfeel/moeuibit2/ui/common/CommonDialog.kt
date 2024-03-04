package org.jeonfeel.moeuibit2.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun TwoButtonCommonDialog(
    dialogState: MutableState<Boolean>,
    title: String,
    content: String,
    leftButtonText: String,
    rightButtonText: String,
    leftButtonAction: () -> Unit,
    rightButtonAction: () -> Unit,
) {
    if (dialogState.value) {
        Dialog(onDismissRequest = { dialogState.value = false }) {
            Card(
                modifier = Modifier
                    .background(color = Color.Transparent)
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
                            fontSize = DpToSp(dp = 25.dp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = content,
                        modifier = Modifier
                            .padding(10.dp, 10.dp, 10.dp, 20.dp)
                            .fillMaxWidth(),
                        style = TextStyle(
                            fontSize = DpToSp(18.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Divider(modifier = Modifier.fillMaxWidth(), Color.LightGray, 1.dp)
                    Row {
                        Text(
                            text = leftButtonText, modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    leftButtonAction()
                                }
                                .padding(0.dp, 10.dp),
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = DpToSp(dp = 18.dp),
                                textAlign = TextAlign.Center
                            )
                        )
                        Text(
                            text = "", modifier = Modifier
                                .width(1.dp)
                                .border(1.dp, Color.LightGray)
                                .padding(0.dp, 10.dp), fontSize = DpToSp(18.dp)
                        )
                        Text(text = rightButtonText,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    rightButtonAction()
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