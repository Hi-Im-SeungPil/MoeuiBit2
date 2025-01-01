package org.jeonfeel.moeuibit2.ui.main.coinsite.item

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import kotlin.reflect.KFunction1

@Composable
fun GlobalExchangeItem(
    updateIsOpen: KFunction1<String, Unit>,
    exchangeIsOpen: Boolean,
    context: Context
) {
    val exchangeImageUrl = getGlobalExchangeImageArray()
    val exchangeUrl = stringArrayResource(id = R.array.exchangeUrl)
    val packageMap = getGlobalExchangePackageMap()
    val titles = packageMap.keys.toList()
    Column(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = MaterialTheme.colorScheme.background),
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable { updateIsOpen("globalExchange") }) {
            Text(
                text = "해외 거래소",
                modifier = Modifier
                    .padding(10.dp, 0.dp, 10.dp, 0.dp)
                    .align(Alignment.CenterVertically),
                fontSize = DpToSp(18.dp),
                style = TextStyle(
                    fontWeight = FontWeight.W600,
                    color = if (exchangeIsOpen) Color(0xffF7A600) else Color.LightGray,
                    textAlign = TextAlign.Center
                )
            )
            IconButton(onClick = { updateIsOpen("globalExchange") }) {
                Icon(
                    if (exchangeIsOpen) {
                        Icons.Filled.KeyboardArrowUp
                    } else {
                        Icons.Filled.KeyboardArrowDown
                    },
                    contentDescription = null,
                    tint = if (exchangeIsOpen) Color(0xffF7A600) else Color.LightGray,
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

//@Composable
//fun CoinSiteCommonItem(
//    image: Int,
//    title: String,
//    url: String,
//    packageName: String?,
//    context: Context
//) {
//    Row(
//        modifier = Modifier
//            .padding(top = 15.dp)
//            .fillMaxWidth()
//            .padding(horizontal = 15.dp)
//            .background(color = Color.White)
//            .clickable { moveUrlOrApp(context, url, packageName) }
//    ) {
//        Image(
//            painterResource(image),
//            "",
//            modifier = Modifier
//                .size(40.dp)
//                .clip(RoundedCornerShape(10.dp))
//                .border(1.dp, color = Color(0xFFF1EFEF), RoundedCornerShape(10.dp))
//                .align(Alignment.CenterVertically)
//        )
//
//        Text(
//            text = title,
//            style = TextStyle(fontSize = DpToSp(14.dp)),
//            modifier = Modifier
//                .padding(start = 15.dp)
//                .weight(1f)
//                .align(Alignment.CenterVertically)
//        )
//
//        Icon(
//            Icons.AutoMirrored.Filled.KeyboardArrowRight,
//            "",
//            modifier = Modifier.align(Alignment.CenterVertically)
//        )
//    }
//}

private fun getGlobalExchangeImageArray(): Array<Int> {
    return arrayOf(
        R.drawable.img_binance,
        R.drawable.img_bybit,
        R.drawable.img_okx,
        R.drawable.img_gateio,
        R.drawable.img_bitget,
    )
}

private fun getGlobalExchangePackageMap(): Map<String, String?> {
    return mapOf(
        "바이낸스" to "com.dunamu.exchange",
        "바이비트" to "com.btckorea.bithumb",
        "OKX" to "coinone.co.kr.official",
        "게이트 IO" to "com.korbit.exchange",
        "비트겟" to "kr.co.gopax",
//        "바이낸스" to "com.binance.dev",
    )
}