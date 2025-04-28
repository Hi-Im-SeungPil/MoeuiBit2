package org.jeonfeel.moeuibit2.ui.mining

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.glide.GlideImage
import org.jeonfeel.moeuibit2.data.network.retrofit.response.gitjson.GitJsonReferralItem
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.newtheme.coinsite.coinSiteIconBorderColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor

@Composable
fun MiningScreen(
    miningInfoList: List<GitJsonReferralItem>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .background(color = commonBackground())
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(miningInfoList.size) {
                MiningScreenItem(
                    imageUrl = miningInfoList[it].imageUrl,
                    title = miningInfoList[it].title,
                    url = miningInfoList[it].url
                )
            }
        }
    }
}

@Composable
fun MiningScreenItem(
    imageUrl: String,
    title: String,
    url: String,
) {
    Row(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .clickable {

            }
    ) {
        GlideImage(
            imageModel = imageUrl,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(1.3.dp, color = coinSiteIconBorderColor(), RoundedCornerShape(10.dp))
                .align(Alignment.CenterVertically)
        )

        Text(
            text = title,
            style = TextStyle(fontSize = DpToSp(14.dp), color = commonTextColor()),
            modifier = Modifier
                .padding(vertical = 15.dp)
                .padding(start = 15.dp)
                .weight(1f)
                .align(Alignment.CenterVertically)
        )

        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            "",
            modifier = Modifier.align(Alignment.CenterVertically),
            tint = commonTextColor()
        )
    }
}