package org.jeonfeel.moeuibit2.ui.main.exchange.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor

@Composable
fun ExchangeNetworkDisconnectScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(140.dp))
        Icon(
            painter = painterResource(R.drawable.img_network_disconnect),
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterHorizontally),
            contentDescription = "",
            tint = commonTextColor()
        )
        Text(
            text = "인터넷 상태를 확인해 주세요.",
            modifier = Modifier
                .padding(top = 25.dp)
                .align(Alignment.CenterHorizontally),
            style = TextStyle(color = commonTextColor(), fontSize = DpToSp(17.dp))
        )
    }
}