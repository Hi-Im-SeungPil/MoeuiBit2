package org.jeonfeel.moeuibit2.ui.main.coinsite.item

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.main.coinsite.moveUrlOrApp
import org.jeonfeel.moeuibit2.ui.theme.newtheme.APP_PRIMARY_COLOR
import org.jeonfeel.moeuibit2.ui.theme.newtheme.coinsite.coinSiteIconBorderColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.coinsite.coinSiteUnSelectTabColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import kotlin.reflect.KFunction1

@Composable
fun KoreanExchangeItem(
    updateIsOpen: KFunction1<String, Unit>,
    exchangeIsOpen: Boolean,
    context: Context
) {
    val exchangeImageUrl = getKoreaExchangeImageArray()
    val exchangeUrl = stringArrayResource(id = R.array.exchangeUrl)
    val packageMap = getExchangePackageMap()
    val titles = packageMap.keys.toList()
    Column(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable { updateIsOpen("koreaExchange") }) {
            Text(
                text = "국내 거래소",
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
            IconButton(onClick = { updateIsOpen("koreaExchange") }) {
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
                for (i in 0 until 5) {
                    CoinSiteCommonItem(
                        image = exchangeImageUrl[i],
                        title = titles[i],
                        url = exchangeUrl[i],
                        packageName = packageMap[titles[i]],
                        context = context
                    )
                }
            }
        }
    }
}

@Composable
fun CoinSiteCommonItem(
    image: Int,
    title: String,
    url: String,
    packageName: String?,
    context: Context
) {
    Row(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .clickable { moveUrlOrApp(context, url, packageName) }
    ) {
//        Image(
//            painterResource(image),
//            "",
//            modifier = Modifier
//                .size(40.dp)
//                .clip(RoundedCornerShape(10.dp))
//                .border(1.3.dp, color = coinSiteIconBorderColor(), RoundedCornerShape(10.dp))
//                .align(Alignment.CenterVertically)
//        )

        Text(
            text = title,
            style = TextStyle(fontSize = DpToSp(18.dp), color = commonTextColor()),
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

fun getKoreaExchangeImageArray(): Array<Int> {
    return arrayOf(
        R.drawable.img_upbit,
        R.drawable.img_bitthumb,
        R.drawable.img_coinone,
        R.drawable.img_korbit,
        R.drawable.img_gopax,
    )
}

fun getExchangePackageMap(): Map<String, String?> {
    return mapOf(
        "업비트" to "com.dunamu.exchange",
        "빗썸" to "com.btckorea.bithumb",
        "코인원" to "coinone.co.kr.official",
        "코빗" to "com.korbit.exchange",
        "고팍스" to "kr.co.gopax",
//        "바이낸스" to "com.binance.dev",
    )
}