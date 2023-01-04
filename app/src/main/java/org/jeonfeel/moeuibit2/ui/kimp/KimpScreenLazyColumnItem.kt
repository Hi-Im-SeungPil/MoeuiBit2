package org.jeonfeel.moeuibit2.ui.kimp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.custom.drawUnderLine

@Composable
fun KimpScreenLazyColumnItem(market: String, koreanName: String) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(55.dp)
        .drawUnderLine(Color.Companion.Gray, strokeWidth = 3f)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = koreanName,
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.Bottom),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center)
            Text(text = market.substring(4),
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center)
        }
        Column(Modifier.weight(1.5f)) {
            Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Image(painter = painterResource(id = R.drawable.img_upbit),
                    contentDescription = null,
                    modifier = Modifier
                        .height(25.dp)
                        .width(30.dp).align(Alignment.CenterVertically))
                Text(text = "31,000,000",
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center)
            }
            Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Image(painter = painterResource(id = R.drawable.img_binance),
                    contentDescription = null,
                    modifier = Modifier
                        .height(25.dp)
                        .width(30.dp).align(Alignment.CenterVertically))
                Text(text = "31,000,000",
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center)
            }
        }
        Text(text = "3%",
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight(),
            textAlign = TextAlign.Center)
        Text(text = "10%",
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight(),
            textAlign = TextAlign.Center)
    }
}