package org.jeonfeel.moeuibit2.ui.main.exchange

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.NO_INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil.Companion.currentNetworkState

@Composable
fun ExchangeErrorScreen(
    checkErrorScreen: () -> Unit,
    context: Context
) {
    val errorText = when (currentNetworkState) {
        NO_INTERNET_CONNECTION -> context.getString(R.string.NO_INTERNET_CONNECTION)
        else -> context.getString(R.string.NETWORK_ERROR)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .height(50.dp)
                .align(Alignment.Center),
            backgroundColor = Color.Black
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = errorText, modifier = Modifier
                        .weight(3f)
                        .fillMaxSize()
                        .wrapContentHeight(),
                    style = TextStyle(color = Color.White, textAlign = TextAlign.Center),
                    fontSize = DpToSp(13.dp)
                )
                TextButton(onClick = {
                    checkErrorScreen()
                }) {
                    Text(
                        text = context.getString(R.string.retry),
                        style = TextStyle(
                            color = colorResource(id = R.color.C0054FF)
                        ),
                        fontSize = DpToSp(dp = 13.dp)
                    )
                }
            }
        }
    }
}