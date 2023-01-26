package org.jeonfeel.moeuibit2.ui.main.coinsite.item

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
import org.jeonfeel.moeuibit2.ui.custom.DpToSp
import org.jeonfeel.moeuibit2.ui.main.coinsite.moveUrlOrApp

@Composable
fun CommunityItem(communityState: MutableState<Boolean>, context: Context) {
    val communityImageUrl = getCommunityImageArray()
    val communityUrl = stringArrayResource(id = R.array.communityUrl)
    val packageMap = getCommunityPackageMap()
    val titles = packageMap.keys.toList()

    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable { communityState.value = !communityState.value }) {
            Text(text = "커뮤니티", modifier = Modifier
                .padding(10.dp, 5.dp, 0.dp, 5.dp)
                .weight(1f, true)
                .align(Alignment.CenterVertically),
                fontSize = DpToSp(20.dp)
            )
            IconButton(onClick = { communityState.value = !communityState.value }) {
                Icon(
                    if (communityState.value) {
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
        if (communityState.value) {


            Row(modifier = Modifier.fillMaxWidth()) {
                for (i in 0 until 3) {
                    Column(modifier = Modifier
                        .padding(5.dp)
                        .weight(1f)
                        .height(100.dp)
                        .clickable {
                            moveUrlOrApp(context, communityUrl[i], packageMap[titles[i]])
                        }
                    ) {
                        GlideImage(imageModel = communityImageUrl[i],
                            modifier = Modifier.height(80.dp), contentScale = ContentScale.Fit)
                        Text(text = titles[i],modifier = Modifier.fillMaxWidth(1f), textAlign = TextAlign.Center, fontSize = DpToSp(15.dp))
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                for (i in 3 until 6) {
                    Column(modifier = Modifier
                        .padding(5.dp)
                        .weight(1f)
                        .height(100.dp)
                        .clickable {
                            moveUrlOrApp(context, communityUrl[i], packageMap[titles[i]])
                        }
                    ) {
                        GlideImage(imageModel = communityImageUrl[i],
                            modifier = Modifier.height(80.dp), contentScale = ContentScale.Fit)
                        Text(text = titles[i],modifier = Modifier.fillMaxWidth(1f), textAlign = TextAlign.Center, fontSize = DpToSp(15.dp))
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                for (i in 6 until 9) {
                    Column(modifier = Modifier
                        .padding(5.dp,5.dp,5.dp,10.dp)
                        .weight(1f)
                        .height(100.dp)
                        .clickable {
                            moveUrlOrApp(context, communityUrl[i], packageMap[titles[i]])
                        }
                    ) {
                        GlideImage(imageModel = communityImageUrl[i],
                            modifier = Modifier.height(80.dp), contentScale = ContentScale.Fit)
                        Text(text = titles[i],modifier = Modifier.fillMaxWidth(1f), textAlign = TextAlign.Center, fontSize = DpToSp(15.dp))
                    }
                }
            }
            Divider(modifier = Modifier.fillMaxWidth(),
                color = colorResource(id = R.color.C0F0F5C),
                1.dp)
        }
    }
}

fun getCommunityImageArray(): Array<Int> {
    return arrayOf(R.drawable.ddengle,
        R.drawable.img_coinpan,
        R.drawable.img_moneynet,
        R.drawable.img_cobak,
        R.drawable.img_blockchain_hub,
        R.drawable.img_dcinside,
        R.drawable.img_fmkorea,
        R.drawable.img_bitman,
        R.drawable.img_musk
    )
}

fun getCommunityPackageMap(): Map<String, String?> {
    return mapOf(
        "땡글" to "com.ddengle.app",
        "코인판" to "com.coinpan.coinpan",
        "머니넷" to "mnet7.mobile",
        "코박" to "com.cobak.android",
        "블록체인 허브" to "kr.blockchainhub.app",
        "비트코인 갤러리" to "com.dcinside.app",
        "가상화폐" to "com.fmkorea.m.fmk",
        "비트맨" to null,
        "머스크 트위터" to null,
    )
}