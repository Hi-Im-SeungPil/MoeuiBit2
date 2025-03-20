package org.jeonfeel.moeuibit2.ui.main.additional_features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.ui.additional_feature.AverageCostCalculator
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.portfolioMainBackground

@Composable
fun AdditionalFeaturesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(commonBackground())
    ) {
        TopAppBar()
        AverageCostCalculator()
    }
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

@Composable
private fun ColumnScope.AdditionalFeaturesList() {
    LazyColumn(modifier = Modifier.weight(1f)) {
//        AdditionalFeaturesItem()
    }
}

@Composable
private fun AdditionalFeaturesItem(imgId: Int, text: String, clickAction: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(10.dp, 20.dp, 10.dp, 0.dp)
            .fillMaxWidth()
            .wrapContentHeight(Alignment.CenterVertically)
            .background(portfolioMainBackground(), shape = RoundedCornerShape(size = 10.dp))
            .noRippleClickable { clickAction() }
            .padding(20.dp, 15.dp)
    ) {
        Icon(
            painter = painterResource(imgId),
            modifier = Modifier.size(20.dp),
            tint = commonTextColor(),
            contentDescription = ""
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 15.dp),
            style = TextStyle(
                fontSize = DpToSp(17.dp),
                color = commonTextColor()
            )
        )
    }
}