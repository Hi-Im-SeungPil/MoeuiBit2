package org.jeonfeel.moeuibit2.ui.main.coinsite

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.theme.chargingKrwBackgroundColor

@Composable
fun CoinSiteScreen() {
    Column {
        Row {
            Text(
                text = stringResource(id = R.string.coinSite),
                modifier = Modifier
                    .padding(10.dp, 0.dp, 0.dp, 0.dp)
                    .weight(1f, true)
                    .align(Alignment.CenterVertically),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = DpToSp(20.dp),
                    fontWeight = FontWeight.W600
                )
            )
            Card(
                modifier = Modifier
                    .background(color = Color.Transparent)
                    .padding(0.dp, 12.dp, 8.dp, 12.dp)
                    .wrapContentWidth(),
            ) {
                Text(
                    text = "",
                    modifier = Modifier
                        .background(color = chargingKrwBackgroundColor())
                        .padding(9.dp)
                        .wrapContentWidth(),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = DpToSp(dp = 13.dp)
                    )
                )
            }
        }
        Divider(Modifier.fillMaxWidth().height(1.dp), color = Color(0xFFDFDFDF))
        Box(modifier = Modifier.fillMaxSize()) {
            CoinSiteLazyColumn()
        }
    }
}

@Composable
fun CoinSiteTopAppBar() {
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.background,

        ) {

    }
}