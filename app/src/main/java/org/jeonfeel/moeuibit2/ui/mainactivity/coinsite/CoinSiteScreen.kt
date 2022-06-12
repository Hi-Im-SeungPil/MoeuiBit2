package org.jeonfeel.moeuibit2.ui.mainactivity.coinsite

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jeonfeel.moeuibit2.R

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
            .fillMaxWidth(),
        backgroundColor = colorResource(id = R.color.design_default_color_background)
    ) {
        Text(
            text = "코인 사이트",
            modifier = Modifier
                .padding(5.dp, 0.dp, 0.dp, 0.dp)
                .weight(1f, true)
                .fillMaxHeight()
                .wrapContentHeight(),
            style = TextStyle(
                color = Color.Black,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}