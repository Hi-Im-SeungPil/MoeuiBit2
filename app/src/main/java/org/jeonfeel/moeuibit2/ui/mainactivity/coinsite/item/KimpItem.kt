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
fun KimpItem(kimpState: MutableState<Boolean>, context: Context) {
    val coinInfoImageUrl = getKimpImageArray()
    val coinInfoUrl = stringArrayResource(id = R.array.kimpUrl)
    val packageMap = getKimpPackageMap()
    val titles = packageMap.keys.toList()
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable { kimpState.value = !kimpState.value }) {
            Text(text = "김프 정보 (한국 프리미엄)", modifier = Modifier
                .padding(10.dp, 5.dp, 0.dp, 5.dp)
                .weight(1f, true)
                .align(Alignment.CenterVertically),
                fontSize = 20.sp)
            IconButton(onClick = { kimpState.value = !kimpState.value }) {
                Icon(
                    if (kimpState.value) {
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
        if (kimpState.value) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (i in 0 until 2) {
                    Column(modifier = Modifier
                        .padding(5.dp,5.dp,5.dp,10.dp)
                        .weight(1f)
                        .height(100.dp)
                        .clickable {
                            moveUrlOrApp(context, coinInfoUrl[i], packageMap[titles[i]])
                        }) {
                        GlideImage(imageModel = coinInfoImageUrl[i],
                            modifier = Modifier.height(80.dp), contentScale = ContentScale.Fit)
                        Text(text = titles[i],modifier = Modifier.fillMaxWidth(1f), textAlign = TextAlign.Center, fontSize = 15.sp)
                    }
                }
                Spacer(modifier = Modifier
                    .padding(5.dp)
                    .weight(1f)
                    .height(60.dp))
            }
            Divider(modifier = Modifier.fillMaxWidth(),
                color = colorResource(id = R.color.C0F0F5C),
                1.dp)
        }
    }
}

fun getKimpImageArray(): Array<Int> {
    return arrayOf(
        R.drawable.img_kimpga,
        R.drawable.img_cryprice,
    )
}

fun getKimpPackageMap(): Map<String, String?> {
    return mapOf(
        "김프가" to null,
        "크라이 프라이스" to null,
    )
}