package org.jeonfeel.moeuibit2.ui.coindetail.coininfo.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.common.DpToSp

@Composable
fun CoinInfoEmptyScreen() {
    Text(
        text = stringResource(id = R.string.noInfo),
        modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight(),
        style = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = DpToSp(23.dp),
            textAlign = TextAlign.Center
        )
    )
}