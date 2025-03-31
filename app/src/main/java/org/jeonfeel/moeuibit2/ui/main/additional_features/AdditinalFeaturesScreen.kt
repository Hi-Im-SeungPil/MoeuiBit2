package org.jeonfeel.moeuibit2.ui.main.additional_features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.portfolioMainBackground

@Composable
fun AdditionalFeaturesScreen() {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar()
        Column {
            FeaturesItem(imgId = R.drawable.img_btc, text = "물타기 계산기") {

            }
        }
    }
    AverageCostCalculator()
}

@Composable
private fun TopAppBar() {
    Row(modifier = Modifier.background(commonBackground())) {
        Text(
            text = "부가 기능",
            modifier = Modifier
                .padding(10.dp, 0.dp, 0.dp, 0.dp)
                .weight(1f, true)
                .align(Alignment.CenterVertically),
            style = TextStyle(
                color = commonTextColor(),
                fontSize = DpToSp(20.dp),
                fontWeight = FontWeight.W600
            )
        )
        Text(
            text = "",
            modifier = Modifier
                .padding(21.dp)
                .wrapContentWidth(),
            style = TextStyle(
                color = commonTextColor(),
                fontSize = DpToSp(dp = 13.dp)
            )
        )
    }
}