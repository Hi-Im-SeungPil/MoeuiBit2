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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.glide.GlideImage
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.main.coinsite.moveUrlOrApp
import kotlin.reflect.KFunction1

@Composable
fun CoinInfoItem(updateIsOpen: KFunction1<String, Unit>, infoIsOpen: Boolean, context: Context) {
    val coinInfoImageUrl = getCoinInfoImageArray()
    val coinInfoUrl = stringArrayResource(id = R.array.coinInfoUrl)
    val packageMap = getCoinInfoPackageMap()
    val titles = packageMap.keys.toList()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()

    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xffFEF7FF))
            .clickable { updateIsOpen("info") }) {
            Text(
                text = "코인 정보", modifier = Modifier
                    .padding(10.dp, 5.dp, 0.dp, 5.dp)
                    .weight(1f, true)
                    .align(Alignment.CenterVertically),
                fontSize = DpToSp(20.dp),
                style = TextStyle(color = MaterialTheme.colorScheme.onBackground)
            )
            IconButton(onClick = { updateIsOpen("info") }) {
                Icon(
                    if (infoIsOpen) {
                        Icons.Filled.KeyboardArrowUp
                    } else {
                        Icons.Filled.KeyboardArrowDown
                    }, contentDescription = null, tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            1.dp
        )
        if (infoIsOpen) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (i in 0 until 3) {
                    Column(modifier = Modifier
                        .padding(5.dp, 5.dp, 5.dp, 10.dp)
                        .weight(1f)
                        .height(100.dp)
                        .clickable {
                            moveUrlOrApp(context, coinInfoUrl[i], packageMap[titles[i]])
                        }) {
                        GlideImage(
                            imageModel = coinInfoImageUrl[i],
                            modifier = Modifier.height(80.dp), contentScale = ContentScale.Fit
                        )
                        Text(
                            text = titles[i],
                            modifier = Modifier.fillMaxWidth(1f),
                            textAlign = TextAlign.Center,
                            fontSize = DpToSp(15.dp),
                            style = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                        )
                    }
                }
            }
            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                1.dp
            )
        }
    }
}

fun getCoinInfoImageArray(): Array<Int> {
    return arrayOf(
        R.drawable.img_coinmarket_cap,
        R.drawable.img_coingeco,
        R.drawable.img_xangle,
    )
}

fun getCoinInfoPackageMap(): Map<String, String?> {
    return mapOf(
        "코인마켓캡" to "com.coinmarketcap.android",
        "코인 게코" to "com.coingecko.coingeckoapp",
        "쟁글" to "com.crossangle.xangle"
    )
}