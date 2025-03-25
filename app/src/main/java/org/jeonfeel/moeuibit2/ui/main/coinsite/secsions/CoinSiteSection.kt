package org.jeonfeel.moeuibit2.ui.main.coinsite.secsions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.portfolioMainBackground

@Composable
fun CoinSiteSection() {
    Row(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .background(color = portfolioMainBackground(), shape = RoundedCornerShape(10.dp))
            .padding(vertical = 20.dp, horizontal = 20.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.img_internet),
            modifier = Modifier
                .padding(end = 20.dp)
                .size(25.dp)
                .align(Alignment.CenterVertically),
            contentDescription = ""
        )

        Text(
            text = "코인 사이트 바로가기",
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            style = TextStyle(
                color = commonTextColor(),
                fontSize = DpToSp(16.dp)
            )
        )

        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            "",
            modifier = Modifier.align(Alignment.CenterVertically),
            tint = commonTextColor()
        )
    }
}