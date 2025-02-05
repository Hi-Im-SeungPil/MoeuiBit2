package org.jeonfeel.moeuibit2.ui.main.coinsite.item

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.glide.GlideImage
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.main.coinsite.moveUrlOrApp
import org.jeonfeel.moeuibit2.ui.theme.newtheme.APP_PRIMARY_COLOR
import org.jeonfeel.moeuibit2.ui.theme.newtheme.coinsite.coinSiteUnSelectTabColor
import kotlin.reflect.KFunction1

@Composable
fun CoinNews(
    updateIsOpen: KFunction1<String, Unit>,
    exchangeIsOpen: Boolean,
    context: Context
) {
    val coinInfoImageUrl = getCoinNewsImageArray()
    val coinInfoUrl = stringArrayResource(id = R.array.coinNewsUrl)
    val packageMap = getCoinNewsPackageMap()
    val titles = packageMap.keys.toList()

    Column(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable { updateIsOpen("news") }) {
            Text(
                text = "코인 뉴스 / 호재",
                modifier = Modifier
                    .padding(35.dp, 0.dp, 10.dp, 0.dp)
                    .align(Alignment.CenterVertically),
                fontSize = DpToSp(18.dp),
                style = TextStyle(
                    fontWeight = FontWeight.W600,
                    color = if (exchangeIsOpen) APP_PRIMARY_COLOR else coinSiteUnSelectTabColor(),
                    textAlign = TextAlign.Center
                )
            )
            IconButton(onClick = { updateIsOpen("news") }) {
                Icon(
                    if (exchangeIsOpen) {
                        Icons.Filled.KeyboardArrowUp
                    } else {
                        Icons.Filled.KeyboardArrowDown
                    },
                    contentDescription = null,
                    tint = if (exchangeIsOpen) APP_PRIMARY_COLOR else coinSiteUnSelectTabColor(),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
        if (exchangeIsOpen) {
            Column(modifier = Modifier.fillMaxWidth()) {
                for (i in 0 until 2) {
                    CoinSiteCommonItem(
                        image = coinInfoImageUrl[i],
                        title = titles[i],
                        url = coinInfoUrl[i],
                        packageName = packageMap[titles[i]],
                        context = context
                    )
                }
                CoinSiteBigImageCommonItem(
                    image = coinInfoImageUrl[2],
                    title = titles[2],
                    url = coinInfoUrl[2],
                    packageName = packageMap[titles[2]],
                    context = context
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

fun getCoinNewsImageArray(): Array<Int> {
    return arrayOf(
        R.drawable.img_coinness,
        R.drawable.img_coinmarketcal,
        R.drawable.img_codal,
    )
}

fun getCoinNewsPackageMap(): Map<String, String?> {
    return mapOf(
        "코인 니스" to "live.coinness",
        "코인 마켓 캘린더" to "com.coincal",
        "코인달인" to null,
    )
}