package org.jeonfeel.moeuibit2.ui.main.portfolio

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.custom.DpToSp
import org.jeonfeel.moeuibit2.ui.viewmodels.MainViewModel

@Composable
fun EditUserHoldCoinDialog(mainViewModel: MainViewModel, dialogState: MutableState<Boolean>) {
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
                    text = stringResource(id = R.string.clearCoin),
                    modifier = Modifier
                        .padding(0.dp, 20.dp)
                        .fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        fontSize = DpToSp(25),
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = stringResource(id = R.string.cleanUpDialogContent),
                    modifier = Modifier
                        .padding(10.dp, 10.dp, 10.dp, 20.dp)
                        .fillMaxWidth(),
                    style = TextStyle(fontSize = DpToSp(18))
                )
                Divider(modifier = Modifier.fillMaxWidth(), Color.LightGray, 0.5.dp)
                Row {
                    Text(
                        text = stringResource(id = R.string.commonCancel), modifier = Modifier
                            .weight(1f)
                            .clickable {
                                dialogState.value = false
                            }
                            .padding(0.dp, 10.dp),
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = DpToSp(18),
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        text = "", modifier = Modifier
                            .width(0.5.dp)
                            .border(0.5.dp, Color.LightGray)
                            .padding(0.dp, 10.dp), fontSize = DpToSp(18)
                    )
                    Text(text = stringResource(id = R.string.commonAccept),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                mainViewModel.editUserHoldCoin()
                                dialogState.value = false
                            }
                            .padding(0.dp, 10.dp),
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = DpToSp(18),
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}