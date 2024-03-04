package org.jeonfeel.moeuibit2.ui.main.coinsite

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.common.DpToSp

@Composable
fun CoinSiteScreen() {
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = { CoinSiteTopAppBar() }) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
            CoinSiteLazyColumn()
        }
    }
}

@Composable
fun CoinSiteTopAppBar() {
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                10.dp,
                ambientColor = MaterialTheme.colorScheme.onBackground,
                spotColor = MaterialTheme.colorScheme.onBackground
            ),
        backgroundColor = MaterialTheme.colorScheme.background,

    ) {
        Text(
            text = stringResource(id = R.string.coinSite),
            modifier = Modifier
                .padding(5.dp, 0.dp, 0.dp, 0.dp)
                .weight(1f, true)
                .fillMaxHeight()
                .wrapContentHeight(),
            style = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = DpToSp(25.dp),
                fontWeight = FontWeight.Bold
            )
        )
    }
}