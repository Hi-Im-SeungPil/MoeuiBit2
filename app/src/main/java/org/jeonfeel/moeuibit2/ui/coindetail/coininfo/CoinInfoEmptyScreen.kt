package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun CoinInfoEmptyScreen() {
    Text(
        text = "코인 정보 준비중입니다...!",
        modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight(),
        style = TextStyle(
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 23.sp,
            textAlign = TextAlign.Center
        )
    )
}