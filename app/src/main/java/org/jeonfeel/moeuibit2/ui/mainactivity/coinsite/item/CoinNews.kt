package org.jeonfeel.moeuibit2.ui.mainactivity.coinsite.item

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.glide.GlideImage
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.mainactivity.coinsite.moveUrlOrApp

@Composable
fun CoinNews(coinNewsState: MutableState<Boolean>, context: Context) {
    val coinInfoImageUrl = getCoinNewsImageArray()
    val coinInfoUrl = stringArrayResource(id = R.array.coinNewsUrl)
    val packageMap = getCoinNewsPackageMap()
    val titles = packageMap.keys.toList()
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable { coinNewsState.value = !coinNewsState.value }) {
            Text(text = "코인 뉴스 / 호재", modifier = Modifier
                .padding(10.dp, 5.dp, 0.dp, 5.dp)
                .weight(1f, true)
                .align(Alignment.CenterVertically),
                fontSize = 20.sp)
            IconButton(onClick = { coinNewsState.value = !coinNewsState.value }) {
                Icon(
                    if (coinNewsState.value) {
                        Icons.Filled.KeyboardArrowUp
                    } else {
                        Icons.Filled.KeyboardArrowDown
                    }, contentDescription = null, tint = colorResource(
                        id = R.color.C0F0F5C)
                )
            }
        }
        Divider(modifier = Modifier.fillMaxWidth(),
            color = colorResource(id = R.color.C0F0F5C),
            1.dp)
        if (coinNewsState.value) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (i in 0 until 3) {
                    Column(modifier = Modifier
                        .padding(5.dp, 5.dp, 5.dp, 10.dp)
                        .weight(1f)
                        .height(100.dp)
                        .clickable {
                            moveUrlOrApp(context, coinInfoUrl[i], packageMap[titles[i]])
                        }) {
                        GlideImage(imageModel = coinInfoImageUrl[i],
                            modifier = Modifier.height(80.dp), contentScale = ContentScale.Fit)
                        Text(text = titles[i],
                            modifier = Modifier.fillMaxWidth(1f),
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp)
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier
                    .padding(5.dp, 5.dp, 5.dp, 10.dp)
                    .weight(1f)
                    .height(100.dp)
                    .clickable {
                        moveUrlOrApp(context, coinInfoUrl[3], packageMap[titles[3]])
                    }) {
                    GlideImage(imageModel = coinInfoImageUrl[3],
                        modifier = Modifier.height(80.dp), contentScale = ContentScale.Fit)
                    Text(text = titles[3],
                        modifier = Modifier.fillMaxWidth(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp)
                }
                Box(modifier = Modifier.weight(1f))
                Box(modifier = Modifier.weight(1f))
            }
            Divider(modifier = Modifier.fillMaxWidth(),
                color = colorResource(id = R.color.C0F0F5C),
                1.dp)
        }
    }
}

fun getCoinNewsImageArray(): Array<Int> {
    return arrayOf(
        R.drawable.img_coinness,
        R.drawable.img_coinmarket_cal,
        R.drawable.img_codal,
        R.drawable.img_coin_desk
    )
}

fun getCoinNewsPackageMap(): Map<String, String?> {
    return mapOf(
        "코인 니스" to "live.coinness",
        "코인 마켓 캘린더" to "com.coincal",
        "코인달인" to null,
        "코인데스크" to null
    )
}