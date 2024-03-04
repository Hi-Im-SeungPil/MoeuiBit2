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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.glide.GlideImage
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.main.coinsite.moveUrlOrApp

@Composable
fun ExchangeItem(exchangeState: MutableState<Boolean>, context: Context) {
    val exchangeImageUrl = getExchangeImageArray()
    val exchangeUrl = stringArrayResource(id = R.array.exchangeUrl)
    val packageMap = getExchangePackageMap()
    val titles = packageMap.keys.toList()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable { exchangeState.value = !exchangeState.value }) {
            Text(
                text = "거래소",
                modifier = Modifier
                    .padding(10.dp, 5.dp, 0.dp, 5.dp)
                    .weight(1f, true)
                    .align(Alignment.CenterVertically),
                fontSize = DpToSp(20.dp),
                style = TextStyle(color = MaterialTheme.colorScheme.onBackground)
            )
            IconButton(onClick = { exchangeState.value = !exchangeState.value }) {
                Icon(
                    if (exchangeState.value) {
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
        if (exchangeState.value) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (i in 0 until 3) {
                    Column(modifier = Modifier
                        .padding(5.dp)
                        .weight(1f)
                        .height(100.dp)
                        .clickable {
                            moveUrlOrApp(context, exchangeUrl[i], packageMap[titles[i]])
                        }) {
                        GlideImage(
                            imageModel = exchangeImageUrl[i],
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
            Row(modifier = Modifier.fillMaxWidth()) {
                for (i in 3 until 6) {
                    Column(modifier = Modifier
                        .padding(5.dp, 5.dp, 5.dp, 10.dp)
                        .weight(1f)
                        .height(100.dp)
                        .clickable {
                            moveUrlOrApp(context, exchangeUrl[i], packageMap[titles[i]])
                        }
                    ) {
                        GlideImage(
                            imageModel = exchangeImageUrl[i],
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

fun getExchangeImageArray(): Array<Int> {
    return arrayOf(
        R.drawable.img_upbit,
        R.drawable.img_bithumb,
        R.drawable.img_coinone,
        R.drawable.img_korbit,
        R.drawable.img_gopax,
        R.drawable.img_binance
    )
}

fun getExchangePackageMap(): Map<String, String?> {
    return mapOf(
        "업비트" to "com.dunamu.exchange",
        "빗썸" to "com.btckorea.bithumb",
        "코인원" to "coinone.co.kr.official",
        "코빗" to "com.korbit.exchange",
        "고팍스" to "kr.co.gopax",
        "바이낸스" to "com.binance.dev",
    )
}