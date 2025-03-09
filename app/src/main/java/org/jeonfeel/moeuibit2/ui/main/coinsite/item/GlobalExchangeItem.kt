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
import org.jeonfeel.moeuibit2.ui.theme.newtheme.APP_PRIMARY_COLOR
import org.jeonfeel.moeuibit2.ui.theme.newtheme.coinsite.coinSiteUnSelectTabColor
import kotlin.reflect.KFunction1

@Composable
fun GlobalExchangeItem(
    updateIsOpen: KFunction1<String, Unit>,
    exchangeIsOpen: Boolean,
    context: Context
) {
    val exchangeImageUrl = getGlobalExchangeImageArray()
    val exchangeUrl = getRefferalUrl()
    val packageMap = getGlobalExchangePackageMap()
    val titles = packageMap.keys.toList()
    Column(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable { updateIsOpen("globalExchange") }) {
            Text(
                text = "해외 거래소",
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
            IconButton(onClick = { updateIsOpen("globalExchange") }) {
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
        "바이낸스" to "com.binance.dev",
        "바이비트" to "com.bybit.app",
        "OKX" to "com.okinc.okex.gp",
        "게이트 IO" to "com.gateio.gateio",
        "비트겟" to "com.bitget.exchange",
    )
}

private fun getRefferalUrl(): List<String> {
    return listOf(
        "https://www.binance.info/activity/referral-entry/CPA?ref=CPA_00K7KK4OY0",
        "https://www.bybit.com/invite?ref=0RZLJJN",
        "https://okx.com/join/74392423",
        "https://www.gate.io/signup/VFdHVA0O?ref_type=103",
        "https://www.bitget.com/asia/"
    )
}