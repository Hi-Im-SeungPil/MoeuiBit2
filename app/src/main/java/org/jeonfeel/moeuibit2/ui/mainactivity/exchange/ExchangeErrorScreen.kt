package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.util.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.util.NETWORK_ERROR
import org.jeonfeel.moeuibit2.util.NO_INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.util.NetworkMonitorUtil.Companion.currentNetworkState
import org.jeonfeel.moeuibit2.viewmodel.ExchangeViewModel

@Composable
fun ExchangeErrorScreen(exchangeViewModel: ExchangeViewModel = viewModel()) {
    val context = LocalContext.current
    val errorText = when (currentNetworkState) {
        NO_INTERNET_CONNECTION -> context.getString(R.string.NO_INTERNET_CONNECTION)
        NETWORK_ERROR -> context.getString(R.string.NETWORK_ERROR)
        else -> ""
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 0.dp)
            .height(50.dp)
            .align(Alignment.Center),
            backgroundColor = Color.Black) {
            Row(modifier = Modifier.fillMaxSize()) {
                Text(text = errorText,
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxSize()
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center,
                    style = TextStyle(color = Color.White, fontSize = 13.sp))

                TextButton(onClick = {
                    if (exchangeViewModel.preItemArray.isEmpty()) {
                        exchangeViewModel.requestData()
                    } else {
                        if (currentNetworkState == INTERNET_CONNECTION || currentNetworkState == NETWORK_ERROR) {
                            exchangeViewModel.errorState.value = currentNetworkState
                            exchangeViewModel.requestKrwCoinList()
                        }
                    }
                }) {
                    Text(text = context.getString(R.string.retry),
                        style = TextStyle(color = colorResource(id = R.color.C0054FF),
                            fontSize = 17.sp))
                }
            }
        }
    }
}