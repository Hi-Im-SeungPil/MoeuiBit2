package org.jeonfeel.moeuibit2.ui.main.coinsite.secsions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
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
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.portfolioMainBackground

@Composable
fun ExchangeRateSection() {
    Column(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .background(color = portfolioMainBackground(), shape = RoundedCornerShape(10.dp))
            .padding(vertical = 20.dp, horizontal = 20.dp),
    ) {
        Text(
            text = "환율 정보",
            style = TextStyle(
                fontSize = DpToSp(14.dp),
                fontWeight = FontWeight.W500,
                color = commonTextColor()
            )
        )
        ExchangeRateItem(
            unit1 = "USD",
            unit2 = "EUR",
            "1,460",
            "1,000",
            R.drawable.america,
            R.drawable.eu
        )
        Spacer(modifier = Modifier.height(15.dp))
        ExchangeRateItem(
            unit1 = "JPY",
            unit2 = "CNY",
            "800",
            "500",
            R.drawable.japan,
            R.drawable.china
        )
    }
}

@Composable
private fun ExchangeRateItem(
    unit1: String,
    unit2: String,
    unit1Price: String,
    unit2Price: String,
    unit1Flag: Int,
    unit2Flag: Int,
) {
    Row {
        Column(modifier = Modifier.weight(1f)) {
            Row {
                Image(
                    painter = painterResource(unit1Flag),
                    modifier = Modifier
                        .size(25.dp)
                        .clip(CircleShape)
                        .border(width = 1.dp, color = Color(0xFFE8E8E8), shape = CircleShape)
                        .background(Color.White)
                        .align(Alignment.CenterVertically),
                    contentDescription = ""
                )
                Text(
                    unit1,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .align(Alignment.CenterVertically),
                    style = TextStyle(
                        fontWeight = FontWeight.W500,
                        fontSize = DpToSp(15.dp),
                        color = commonTextColor()
                    )
                )
            }
            Text(
                unit1Price,
                modifier = Modifier
                    .padding(top = 15.dp, start = 25.dp),
                style = TextStyle(
                    fontSize = DpToSp(20.dp),
                    color = commonTextColor()
                )
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row {
                Image(
                    painter = painterResource(unit2Flag),
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .border(width = 1.dp, color = Color(0xFFE8E8E8), shape = CircleShape)
                        .background(Color.White)
                        .align(Alignment.CenterVertically),
                    contentDescription = ""
                )

                Text(
                    text = unit2,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .align(Alignment.CenterVertically),
                    style = TextStyle(
                        fontWeight = FontWeight.W500,
                        fontSize = DpToSp(15.dp),
                        color = commonTextColor()
                    )
                )
            }

            Text(
                unit2Price, modifier = Modifier
                    .padding(top = 15.dp, start = 25.dp),
                style = TextStyle(
                    fontSize = DpToSp(20.dp),
                    color = commonTextColor()
                )
            )
        }
    }
}