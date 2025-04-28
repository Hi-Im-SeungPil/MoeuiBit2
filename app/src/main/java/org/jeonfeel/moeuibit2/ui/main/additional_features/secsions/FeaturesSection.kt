package org.jeonfeel.moeuibit2.ui.main.additional_features.secsions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.main.coinsite.secsions.NativeAdTemplateView
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.portfolioMainBackground

@Composable
fun FeaturesSection(
    features: List<Triple<Int, String, () -> Unit>>,
    share: () -> Unit,
    copyURL: () -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 10.dp)
                    .background(portfolioMainBackground(), shape = RoundedCornerShape(size = 10.dp))
                    .padding(15.dp)
            ) {
                androidx.compose.material3.Text(
                    text = "AD",
                    style = TextStyle(
                        fontSize = DpToSp(14.dp),
                        fontWeight = FontWeight.W500,
                        color = commonTextColor()
                    )
                )

                NativeAdTemplateView()
            }
        }

        items(features.size) { index ->
            if (features[index].second == "DePIN 채굴 정보") {
                FeaturesItemForDePIN(
                    imgId = features[index].first,
                    text = features[index].second,
                    clickAction = features[index].third,
                    share = share,
                    copyURL = copyURL
                )
            } else {
                FeaturesItem(
                    imgId = features[index].first,
                    text = features[index].second,
                    clickAction = features[index].third
                )
            }
        }
    }
}

@Composable
private fun FeaturesItem(
    imgId: Int,
    text: String,
    clickAction: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(top = 13.dp, bottom = 13.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth()
            .wrapContentHeight(Alignment.CenterVertically)
            .background(portfolioMainBackground(), shape = RoundedCornerShape(size = 10.dp))
            .noRippleClickable { clickAction() }
            .padding(20.dp, 10.dp)
    ) {
        if (text == "App 채굴 정보" || text == "평단가 계산기") {
            Icon(
                painter = painterResource(id = imgId),
                modifier = Modifier.size(30.dp),
                contentDescription = "",
                tint = commonTextColor()
            )
        } else {
            Image(
                painter = painterResource(id = imgId),
                modifier = Modifier.size(30.dp),
                contentDescription = ""
            )
        }

        Text(
            text = text,
            modifier = Modifier
                .padding(start = 25.dp)
                .align(Alignment.CenterVertically),
            style = TextStyle(
                fontSize = DpToSp(15.dp),
                color = commonTextColor()
            )
        )
    }
}

@Composable
private fun FeaturesItemForDePIN(
    imgId: Int,
    text: String,
    clickAction: () -> Unit,
    share: () -> Unit,
    copyURL: () -> Unit,
) {
    Row {
        Row(
            modifier = Modifier
                .padding(top = 13.dp, bottom = 13.dp, start = 20.dp, end = 0.dp)
                .weight(1f)
                .wrapContentHeight(Alignment.CenterVertically)
                .background(portfolioMainBackground(), shape = RoundedCornerShape(size = 10.dp))
                .noRippleClickable { clickAction() }
                .padding(20.dp, 10.dp)
        ) {
            Image(
                painter = painterResource(id = imgId),
                modifier = Modifier.size(30.dp),
                contentDescription = ""
            )

            Text(
                text = text,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 25.dp)
                    .align(Alignment.CenterVertically),
                style = TextStyle(
                    fontSize = DpToSp(15.dp),
                    color = commonTextColor()
                )
            )

            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                "",
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                tint = commonTextColor()
            )
        }

        Icon(
            painter = painterResource(R.drawable.img_share),
            contentDescription = "",
            modifier = Modifier
                .padding(start = 20.dp, end = 10.dp)
                .align(Alignment.CenterVertically)
                .size(30.dp)
                .noRippleClickable {
                    share()
                },
            tint = commonTextColor()
        )

        Icon(
            painter = painterResource(R.drawable.img_copy),
            contentDescription = "",
            modifier = Modifier
                .padding(start = 10.dp, end = 20.dp)
                .align(Alignment.CenterVertically)
                .size(30.dp)
                .noRippleClickable {
                    copyURL()
                },
            tint = commonTextColor()
        )
    }
}