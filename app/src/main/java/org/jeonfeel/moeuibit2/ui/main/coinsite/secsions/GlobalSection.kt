package org.jeonfeel.moeuibit2.ui.main.coinsite.secsions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonDividerColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.portfolioMainBackground

@Composable
fun GlobalSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = portfolioMainBackground(), shape = RoundedCornerShape(10.dp))
            .padding(vertical = 20.dp, horizontal = 20.dp),
    ) {
        Text(
            text = "글로벌 정보",
            style = TextStyle(
                fontSize = DpToSp(14.dp),
                fontWeight = FontWeight.W500,
                color = commonTextColor()
            )
        )
        DominanceItem(imgId = R.drawable.img_btc, "비트코인", "60") { }
        DominanceItem(imgId = R.drawable.img_eth, "이더리움", "20") { }
    }
}

@Composable
private fun DominanceItem(imgId: Int, coinName: String, dominance: String, onClick: () -> Unit) {
    Row(modifier = Modifier
        .padding(top = 10.dp)
        .fillMaxWidth()
        .noRippleClickable { onClick.invoke() }) {
        Image(
            painter = painterResource(imgId),
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .border(width = 1.dp, color = Color(0xFFE8E8E8), shape = CircleShape)
                .background(Color.White)
                .align(Alignment.CenterVertically),
            contentDescription = ""
        )

        Text(
            text = "$coinName 도미넌스",
            modifier = Modifier
                .padding(start = 10.dp)
                .align(Alignment.CenterVertically),
            style = TextStyle(fontSize = DpToSp(13.dp))
        )

        Text(
            text = dominance, modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f)
                .align(Alignment.CenterVertically),
            style = TextStyle(fontSize = DpToSp(13.dp))
        )

        Text(
            text = "차트",
            modifier = Modifier.align(Alignment.CenterVertically),
            style = TextStyle(fontSize = DpToSp(11.dp))
        )
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            "",
            modifier = Modifier.align(Alignment.CenterVertically),
            tint = commonTextColor()
        )
    }

    Divider(
        modifier = Modifier
            .padding(top = 10.dp, start = 10.dp)
            .height(1.dp)
            .fillMaxWidth()
            .background(color = commonDividerColor())
    )
}